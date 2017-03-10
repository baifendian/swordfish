/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月25日
 * File Name      : MasterServiceImpl.java
 */

package com.baifendian.swordfish.webserver.service.master;

import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.ProjectFlow;
import com.baifendian.swordfish.dao.mysql.model.Schedule;
import com.baifendian.swordfish.dao.mysql.model.flow.ScheduleMeta;
import com.baifendian.swordfish.rpc.HeartBeatData;
import com.baifendian.swordfish.rpc.MasterService.Iface;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.ScheduleInfo;
import com.baifendian.swordfish.rpc.WorkerService;
import com.baifendian.swordfish.webserver.ExecutorServerInfo;
import com.baifendian.swordfish.webserver.ExecutorServerManager;
import com.baifendian.swordfish.webserver.config.MasterConfig;
import com.baifendian.swordfish.webserver.quartz.FlowScheduleJob;
import com.baifendian.swordfish.webserver.quartz.QuartzManager;
import com.baifendian.swordfish.execserver.result.ResultHelper;
import com.bfd.harpc.RpcException;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MasterService 实现
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月25日
 */
public class MasterServiceImpl implements Iface {

    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** worker rpc client */
    private final ExecutorServerManager executorServerManager;

    /** {@link FlowDao} */
    private final FlowDao flowDao;

    /** workflow 执行队列 */
    private final BlockingQueue<ExecutionFlow> executionFlowQueue;

    /** {@link RetryToWorkerThread} */
    private final RetryToWorkerThread retryToWorkerThread;

    /** {@link FlowExecManager} */
    private final FlowExecManager flowExecManager;

    /**
     */
    public MasterServiceImpl() {
        executorServerManager = new ExecutorServerManager();
        this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);

        flowExecManager = new FlowExecManager(executorServerManager, flowDao);

        // 启动请求 worker 失败的处理线程
        executionFlowQueue = new LinkedBlockingQueue<>(MasterConfig.failRetryQueueSize);
        retryToWorkerThread = new RetryToWorkerThread(worker, flowDao, executionFlowQueue);
        retryToWorkerThread.setDaemon(true);
        retryToWorkerThread.start();

        // 初始化调度作业
        FlowScheduleJob.init(worker, flowDao);
    }

    /**
    * 保证 flow 事务执行:
    * 1. 首先更新执行状态为 INIT (在 Api 中状态为 null)
    * 2. Master 调用 Worker ：
    *   1) 调用成功，直接返回；
    *   2) 调用失败，若状态为 INIT 插入失败重试队列，并返回成功
    * 3. 其他异常，返回失败(需要排查错误)
    *
    * @param projectId
    * @param execId
    * @param flowType
    * @return
    * @throws TException
    */
    @Override
    public RetInfo execFlow(int projectId, long execId, String flowType) throws TException {
        // 更新状态
        flowDao.updateExecutionFlowStatus(execId, FlowStatus.INIT);

        try {
            return worker.execFlow(projectId, execId, flowType);
        } catch (RpcException e) {
            LOGGER.error(e.getMessage(), e);

            ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
            if (executionFlow != null) {
                // 如果状态仍然为 INIT,表示请求没有达到 worker，那么插入失败重试队列
                if (executionFlow.getStatus() == FlowStatus.INIT) {
                    boolean isInsert = executionFlowQueue.offer(executionFlow);
                    if (!isInsert) { // 插入失败，那么重试队列已满，直接返回失败
                        flowDao.updateExecutionFlowStatus(execId, FlowStatus.FAILED);
                        return ResultHelper.createErrorResult("服务忙，请稍后再试");
                    }
                }
            } else {
                LOGGER.error("execId 对应的任务不存在");
                return ResultHelper.createErrorResult("execId 对应的任务不存在");
            }

        } catch (Exception e) { // 内部错误
            LOGGER.error(e.getMessage(), e);
            flowDao.updateExecutionFlowStatus(execId, FlowStatus.FAILED);
            return ResultHelper.createErrorResult("Master 异常");
        }

        return ResultHelper.SUCCESS;
    }

    /**
    * 设置调度信息, 最终设置的是 Crontab 表达式(其实是按照 Quartz 的语法)
     *
    * @param projectId
    * @param flowId
    * @param flowType
    * @param scheduleInfo
    * @return
    * @throws TException
    */
    @Override
    public RetInfo setSchedule(int projectId, int flowId, String flowType, ScheduleInfo scheduleInfo) throws TException {
        if (StringUtils.isEmpty(flowType)) {
            return ResultHelper.createErrorResult("flowType 参数不能为空");
        }

        if (scheduleInfo == null || StringUtils.isEmpty(scheduleInfo.getCronExpression())) {
            return ResultHelper.createErrorResult("scheduleInfo 参数内容不能为空");
        }

        try {
            Schedule schedule = flowDao.querySchedule(flowId);
            if (schedule == null) {
                return ResultHelper.createErrorResult("schedule 元数据为空");
            }

            // 解析参数
            FlowType type = FlowType.valueOf(flowType);
            Date startDate = new Date(scheduleInfo.getStartDate());
            Date endDate = new Date(scheduleInfo.getEndDate());

            String jobName = FlowScheduleJob.genJobName(flowId, type);
            String jobGroupName = FlowScheduleJob.genJobGroupName(projectId);
            Map<String, Object> dataMap = FlowScheduleJob.genDataMap(projectId, flowId, type, schedule);
            QuartzManager.addJobAndTrigger(jobName, jobGroupName, FlowScheduleJob.class, startDate, endDate, scheduleInfo.getCronExpression(), dataMap);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResultHelper.createErrorResult(e.getMessage());
        }

        return ResultHelper.SUCCESS;
    }

    /**
    * 删除调度信息
     *
    * @param projectId
    * @param flowId
    * @param flowType
    * @return
    * @throws TException
    */
    @Override
    public RetInfo deleteSchedule(int projectId, int flowId, String flowType) throws TException {
        if (StringUtils.isEmpty(flowType)) {
            return ResultHelper.createErrorResult("flowType 参数不能为空");
        }

        try {
            FlowType type = FlowType.valueOf(flowType);
            String jobName = FlowScheduleJob.genJobName(flowId, type);
            String jobGroupName = FlowScheduleJob.genJobGroupName(projectId);
            QuartzManager.deleteJob(jobName, jobGroupName);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResultHelper.createErrorResult(e.getMessage());
        }

        return ResultHelper.SUCCESS;
    }

    /**
    * 删除一个项目的所有调度信息
    *
    * @param projectId
    * @return
    * @throws TException
    */
    @Override
    public RetInfo deleteSchedules(int projectId) throws TException {
        try {
            String jobGroupName = FlowScheduleJob.genJobGroupName(projectId);
            QuartzManager.deleteJobs(jobGroupName);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResultHelper.createErrorResult(e.getMessage());
        }

        return ResultHelper.SUCCESS;
    }

    /**
    * 补数据
    *
    * @param projectId
    * @param flowId
    * @param scheduleMeta
    * @return
    * @throws TException
    */
    @Override
    public RetInfo appendWorkFlow(int projectId, int flowId, String scheduleMeta) throws TException {
        ProjectFlow flow = flowDao.queryFlow(flowId);
        // 若 workflow 被删除
        if (flow == null) {
            LOGGER.error("projectId:{},flowId:{} 的工作流不存在", projectId, flowId);
            return ResultHelper.createErrorResult("当前workflow 不存在");
        }

        ScheduleMeta meta = null;
        try {
            meta = JsonUtil.parseObject(scheduleMeta, ScheduleMeta.class);
            String crontabStr = meta.getCrontab();
            CronExpression cron = new CronExpression(crontabStr);

            Date startDateTime = new Date(meta.getStartDate()*1000);
            Date endDateTime = new Date(meta.getEndDate()*1000);

            // 提交补数据任务
            flowExecManager.submitAddData(flow, cron, startDateTime, endDateTime);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResultHelper.createErrorResult(e.getMessage());
        }

        if (meta == null) {
            return ResultHelper.createErrorResult("scheduleMeta 信息不正确");
        }

        return ResultHelper.SUCCESS;
    }

    /**
     * 销毁资源
     * <p>
     */
    public void destory() {
        flowExecManager.destroy();
    }

    public RetInfo registerExecutor(String host, int port) throws TException{
        String key = String.format("%s:%d", host, port);
        if(executorServerManager.serverExists(key)){
            return ResultHelper.createErrorResult("executor is register before");
        }

        ExecutorServerInfo executorServerInfo = new ExecutorServerInfo();
        executorServerInfo.setHost(host);
        executorServerInfo.setPort(port);
        executorServerInfo.setHeartBeatData(null);

        executorServerManager.addServer(key, executorServerInfo);
        return ResultHelper.SUCCESS;
    }

    /**
     * execServer汇报心跳
     * host : host地址
     * port : 端口号
     *
     * @param host
     * @param port
     * @param heartBeatData
     */
    public RetInfo executorReport(String host, int port, HeartBeatData heartBeatData) throws org.apache.thrift.TException {
        String key = String.format("%s:%d", host, port);
        ExecutorServerInfo executorServerInfo = new ExecutorServerInfo();
        executorServerInfo.setHost(host);
        executorServerInfo.setPort(port);
        executorServerInfo.setHeartBeatData(heartBeatData);

        executorServerManager.addServer(key, executorServerInfo);
        return ResultHelper.SUCCESS;
    }

}
