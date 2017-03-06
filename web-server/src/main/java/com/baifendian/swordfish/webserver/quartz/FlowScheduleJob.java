/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月24日
 * File Name      : FlowScheduleJob.java
 */

package com.baifendian.swordfish.webserver.quartz;

import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.common.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.mysql.enums.*;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.ProjectFlow;
import com.baifendian.swordfish.dao.mysql.model.Schedule;
import com.baifendian.swordfish.dao.mysql.model.flow.DepWorkflow;
import com.baifendian.swordfish.rpc.WorkerService;
import com.baifendian.swordfish.webserver.config.MasterConfig;
import com.baifendian.swordfish.dao.mail.EmailManager;
import com.bfd.harpc.RpcException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Workflow 调度 Job
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月24日
 */
public class FlowScheduleJob implements Job {

    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(FlowScheduleJob.class);

    /** 名称分割符 */
    private static final String NAME_SEPARATOR = "_";

    /** FlowScheduleJob 名称前缀 */
    private static final String FLOW_SCHEDULE_JOB_NAME_PRIFIX = "Job_Flow";

    /** FlowScheduleJob 组名称前缀 */
    private static final String FLOW_SCHEDULE_JOB_GROUP_NAME_PRIFIX = "JobGroup_Flow";

    /** projectId */
    public static final String PARAM_PROJECT_ID = "projectId";

    /** flowId */
    public static final String PARAM_FLOW_ID = "flowId";

    /** flowType */
    public static final String PARAM_FLOW_TYPE = "flowType";

    /** schedule */
    private static final String PARAM_SCHEDULE = "schedule";

    /** worker rpc client */
    private static WorkerService.Iface worker;

    /** {@link FlowDao} */
    private static FlowDao flowDao;

    /** 检测依赖的等待时间，默认 30 s */
    private static long checkInterval = 30 * 1000;

    /**
     * 初始化 Job （使用该调度 Job 前，必须先调用该函数初始化）
     * <p>
     *
     * @param worker
     * @param flowDao
     */
    public static void init(WorkerService.Iface worker, FlowDao flowDao) {
        FlowScheduleJob.worker = worker;
        FlowScheduleJob.flowDao = flowDao;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 1. 获取参数
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int projectId = dataMap.getInt(PARAM_PROJECT_ID);
        int flowId = dataMap.getInt(PARAM_FLOW_ID);
        FlowType flowType = (FlowType) dataMap.get(PARAM_FLOW_TYPE);
        // Schedule schedule =
        // JsonUtil.parseObject(dataMap.getString(PARAM_SCHEDULE),
        // Schedule.class);
        Date scheduledFireTime = context.getScheduledFireTime();
        // 起始时间 (ms)
        long startTime = System.currentTimeMillis();

        ProjectFlow flow = flowDao.queryFlow(flowId);
        // 若 workflow 被删除，那么直接删除当前 job
        if (flow == null) {
            deleteJob(projectId, flowId, flowType);
            LOGGER.warn("workflow 不存在，删除 projectId:{},flowId:{} 的调度作业", projectId, flowId);
            return;
        }

        // 获取依赖的 workflow 的调度信息，判断当前 workflow 是否可以执行
        Schedule schedule = flowDao.querySchedule(flowId);
        if (schedule == null) {
            deleteJob(projectId, flowId, flowType);
            LOGGER.warn("workflow 的调度信息不存在，删除 projectId:{},flowId:{} 的调度作业", projectId, flowId);
            return;
        }

        // 插入 ExecutionFlow
        int scheduledTime = BFDDateUtils.getSecs(scheduledFireTime);
        ExecutionFlow executionFlow = flowDao.scheduleFlowToExecution(projectId, flowId, flow.getOwnerId(), scheduledTime, FlowRunType.DISPATCH);
        executionFlow.setProjectId(projectId);
        executionFlow.setFlowType(flowType);

        // 自动依赖上一调度周期才能结束
        boolean isNotUpdateWaitingDep = true;
        if (schedule.getDepPolicy() == DepPolicyType.DEP_PRE) {
            Date previousFireTime = context.getPreviousFireTime();
            // 存在上一调度周期
            if (previousFireTime != null) {
                if (isNotUpdateWaitingDep) { // 需要更新状态为 WAITING_DEP
                    updateWaitingDepFlowStatus(executionFlow);
                    isNotUpdateWaitingDep = false;
                }

                // 如果自依赖的上一个调度周期失败，那么本次也失败
                if (!checkDepWorkflowStatus(flowId, previousFireTime, startTime, schedule.getTimeout())) {
                    executionFlow.setStatus(FlowStatus.FAILED);
                    executionFlow.setEndTime(BFDDateUtils.getSecs());
                    executionFlow.setErrorCode(FlowErrorCode.DEP_PRE_FAILED);
                    flowDao.updateExecutionFlow(executionFlow);
                    LOGGER.error("自依赖的上一周期执行失败");
                    // 发送邮件
                    if (BooleanUtils.isTrue(schedule.getSuccessEmails()) || BooleanUtils.isTrue(schedule.getFailureEmails())) {
                        EmailManager.sendEmail(executionFlow);
                    }
                    return;
                }
            }
        }

        List<DepWorkflow> deps = JsonUtil.parseObjectList(schedule.getDepWorkflows(), DepWorkflow.class);
        if (deps != null) {
            if (isNotUpdateWaitingDep) { // 需要更新状态为 WAITING_DEP
                updateWaitingDepFlowStatus(executionFlow);
            }

            // 检测依赖
            boolean isSuccess = checkDeps(schedule, scheduledFireTime, deps, startTime, schedule.getTimeout());

            // 依赖失败，则当前任务也失败
            if (!isSuccess) {
                executionFlow.setStatus(FlowStatus.FAILED);
                executionFlow.setEndTime(BFDDateUtils.getSecs());
                executionFlow.setErrorCode(FlowErrorCode.DEP_FAILED);
                flowDao.updateExecutionFlow(executionFlow);
                LOGGER.error("依赖的 workflow 执行失败");
                // 发送邮件
                if (BooleanUtils.isTrue(schedule.getSuccessEmails()) || BooleanUtils.isTrue(schedule.getFailureEmails())) {
                    EmailManager.sendEmail(executionFlow);
                }
                return;
            }
        }

        // 发送执行任务到 worker
        sendExecutionToWoker(executionFlow, scheduledFireTime);
    }

    /**
     * 更新 workflow 的执行状态为 WAITING_DEP
     * <p>
     *
     * @param executionFlow
     */
    private void updateWaitingDepFlowStatus(ExecutionFlow executionFlow) {
        executionFlow.setStatus(FlowStatus.WAITING_DEP);
        flowDao.updateExecutionFlow(executionFlow);
    }

    /**
     * 检测一个 workflow 的 某一调度时刻的执行状态
     * <p>
     *
     * @param flowId
     * @param previousFireTime
     * @param startTime
     * @param timeout
     */
    private boolean checkDepWorkflowStatus(int flowId, Date previousFireTime, long startTime, Integer timeout) {
        // 循环检测，直到检测到依赖是否成功
        while (true) {
            boolean isFind = false;
            boolean isNotFinshed = false;
            // 看上一个调度周期是否有成功的
            List<ExecutionFlow> executionFlows = flowDao.queryFlowLastStatus(flowId, previousFireTime);
            if (CollectionUtils.isNotEmpty(executionFlows)) {
                isFind = true;
                for (ExecutionFlow executionFlow : executionFlows) {
                    FlowStatus flowStatus = executionFlow.getStatus();
                    if (flowStatus != null && flowStatus.typeIsSuccess()) {
                        return true; // 已经执行成功
                    } else if (flowStatus == null || !flowStatus.typeIsFinished()) {
                        isNotFinshed = true;
                    }
                }
            }

            if (!isFind) {
                return false; // 没有找到上一调度周期的执行，那么这里可能是上一次执行没有更新到数据库就失败了，那么本次也应该失败
            }

            if (isNotFinshed) { // 没有结束
                // 如果超时
                if (checkTimeout(startTime, timeout)) {
                    LOGGER.error("等待上一调度周期的任务超时");
                    return false; // 也认为是执行失败
                }

                // 等待一定的时间，再进行下一次检测
                try {
                    Thread.sleep(checkInterval);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                    return false; // 也认为是执行失败
                }
            } else {
                return false; // 全部执行失败
            }
        }

    }

    /**
     * 检测是否超时
     * <p>
     * 
     * @param startTime
     * @param timeout
     * @return 是否超时
     */
    private boolean checkTimeout(long startTime, Integer timeout) {
        if (timeout == null) {
            return false;
        }
        int usedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
        return timeout <= usedTime;
    }

    /**
     * 是否成功
     * <p>
     *
     * @param schedule
     * @param scheduledFireTime
     * @param deps
     * @param startTime
     * @param timeout
     */
    private boolean checkDeps(Schedule schedule, Date scheduledFireTime, List<DepWorkflow> deps, long startTime, Integer timeout) {
        for (DepWorkflow depWorkflow : deps) {
            int depFlowId = depWorkflow.getWorkflowId();
            Schedule depSchedule = flowDao.querySchedule(depFlowId);
            if (depSchedule != null) {
                Map.Entry<Date, Date> cycleDate;
                if (depSchedule.getScheduleType().ordinal() > schedule.getScheduleType().ordinal()) {
                    cycleDate = calcCycleDate(scheduledFireTime, depSchedule.getScheduleType());
                } else {
                    cycleDate = calcCycleDate(scheduledFireTime, schedule.getScheduleType());
                }

                // 检测依赖的最新状态
                if (!checkDepWorkflowStatus(scheduledFireTime, depFlowId, cycleDate, startTime, timeout)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 检测依赖的 workflow 的状态
     * <p>
     *
     * @param scheduledFireTime
     * @param depFlowId
     * @param cycleDate
     * @param startTime
     * @param startTime
     * @param timeout
     * @return 是否成功
     */
    private boolean checkDepWorkflowStatus(Date scheduledFireTime, int depFlowId, Map.Entry<Date, Date> cycleDate, long startTime, Integer timeout) {
        // 循环检测，直到检测到依赖是否成功
        while (true) {
            boolean isFind = false;
            boolean isNotFinshed = false;
            // 看当前周期（月、周、天 等）最开始的任务是不是成功的
            List<ExecutionFlow> executionFlows = flowDao.queryFlowLastStatus(depFlowId, cycleDate.getValue(), scheduledFireTime);
            if (CollectionUtils.isNotEmpty(executionFlows)) {
                isFind = true;
                for (ExecutionFlow executionFlow : executionFlows) {
                    FlowStatus flowStatus = executionFlow.getStatus();
                    if (flowStatus != null && flowStatus.typeIsSuccess()) {
                        return true; // 已经执行成功
                    } else if (flowStatus == null || !flowStatus.typeIsFinished()) {
                        isNotFinshed = true;
                    }
                }
            }

            // 看上一个周期（月、周、天 等）最后的任务是不是成功的
            executionFlows = flowDao.queryFlowLastStatus(depFlowId, cycleDate.getKey(), cycleDate.getValue());
            if (CollectionUtils.isNotEmpty(executionFlows)) {
                isFind = true;
                for (ExecutionFlow executionFlow : executionFlows) {
                    FlowStatus flowStatus = executionFlow.getStatus();
                    if (flowStatus != null && flowStatus.typeIsSuccess()) {
                        return true; // 已经执行成功
                    } else if (flowStatus == null || !flowStatus.typeIsFinished()) {
                        isNotFinshed = true;
                    }
                }
            }

            if (!isFind) {
                return true; // 没有找到依赖的 workflow 的执行，那么可能是当前 workflow
                             // 的调度已经停止，姑且认为是成功
            }

            if (isNotFinshed) { // 没有结束
                // 如果超时
                if (checkTimeout(startTime, timeout)) {
                    LOGGER.error("等待依赖的 workflow 任务超时");
                    return false; // 也认为是执行失败
                }

                // 等待一定的时间，再进行下一次检测
                try {
                    Thread.sleep(checkInterval);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                    return false; // 也认为是执行失败
                }
            } else {
                return false; // 全部执行失败
            }
        }
    }

    /**
     * 计算上一个周期的时间起始时间和结束时间（起始时间 <= 有效时间 < 结束时间）
     * <p>
     *
     * @param scheduledFireTime
     * @param scheduleType
     * @return 周期的起始和结束时间
     */
    private Map.Entry<Date, Date> calcCycleDate(Date scheduledFireTime, ScheduleType scheduleType) {
        // 起始时间
        Calendar scheduleStartTime = Calendar.getInstance();
        scheduleStartTime.setTime(scheduledFireTime);

        // 介绍时间
        Calendar scheduleEndTime = Calendar.getInstance();
        scheduleEndTime.setTime(scheduledFireTime);
        switch (scheduleType) {
            case MINUTE:
                // 上一分钟 ~ 当前分钟的开始
                scheduleStartTime.add(Calendar.MINUTE, -1);
                scheduleEndTime.set(Calendar.SECOND, 0);
                break;

            case HOUR:
                // 上一小时 ~ 当前小时的开始
                scheduleStartTime.add(Calendar.HOUR_OF_DAY, -1);
                scheduleEndTime.set(Calendar.MINUTE, 0);
                scheduleEndTime.set(Calendar.SECOND, 0);
                break;

            case DAY:
                // 上一天 ~ 当前天的开始
                scheduleStartTime.add(Calendar.DAY_OF_MONTH, -1);
                scheduleEndTime.set(Calendar.HOUR_OF_DAY, 0);
                scheduleEndTime.set(Calendar.MINUTE, 0);
                scheduleEndTime.set(Calendar.SECOND, 0);
                break;

            case WEEK:
                // 上一周 ~ 当前周的开始(周一)
                scheduleStartTime.add(Calendar.WEEK_OF_YEAR, -1);
                scheduleEndTime.setTime(scheduleStartTime.getTime());
                scheduleEndTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                scheduleEndTime.set(Calendar.HOUR_OF_DAY, 0);
                scheduleEndTime.set(Calendar.MINUTE, 0);
                scheduleEndTime.set(Calendar.SECOND, 0);

                break;

            case MONTH:
                // 上一月
                scheduleStartTime.add(Calendar.MONTH, -1);
                scheduleEndTime.setTime(scheduleStartTime.getTime());
                scheduleEndTime.set(Calendar.DAY_OF_MONTH, 1);
                scheduleEndTime.set(Calendar.HOUR_OF_DAY, 0);
                scheduleEndTime.set(Calendar.MINUTE, 0);
                scheduleEndTime.set(Calendar.SECOND, 0);
                break;

            default:
                break;
        }
        return new AbstractMap.SimpleImmutableEntry<Date, Date>(scheduleStartTime.getTime(), scheduleEndTime.getTime());
    }

    /**
     * 删除 job
     * <p>
     *
     * @param projectId
     * @param flowId
     * @param flowType
     */
    private void deleteJob(int projectId, int flowId, FlowType flowType) {
        String jobName = genJobName(flowId, flowType);
        String jobGroupName = genJobGroupName(projectId);
        QuartzManager.deleteJob(jobName, jobGroupName);
    }

    /**
     * 发送执行任务到 worker
     * <p>
     *
     * @param executionFlow
     * @param scheduleDate
     */
    private void sendExecutionToWoker(ExecutionFlow executionFlow, Date scheduleDate) {
        long execId = executionFlow.getId();
        boolean isSucess = false; // 是否请求成功
        for (int i = 0; i < MasterConfig.failRetryCount + 1; i++) {
            try {
                worker.scheduleExecFlow(executionFlow.getProjectId(), execId, executionFlow.getFlowType().name(), scheduleDate.getTime());
                isSucess = true;
                break; // 请求成功，结束重试请求
            } catch (RpcException e) {
                ExecutionFlow temp = flowDao.queryExecutionFlow(execId);
                // 如果执行被取消或者状态已经更新，结束重试请求
                if (temp == null || temp.getStatus() != FlowStatus.INIT) {
                    break;
                }
            } catch (Exception e) { // 内部错误
                LOGGER.error(e.getMessage(), e);
            }
        }

        // 多次重试后仍然失败
        if (!isSucess) {
            flowDao.updateExecutionFlowStatus(execId, FlowStatus.FAILED);
        }
    }

    /**
     * 生成 workflow 调度任务名称
     * <p>
     *
     * @param flowId
     * @param flowType
     * @return Job名称
     */
    public static String genJobName(int flowId, FlowType flowType) {
        StringBuilder builder = new StringBuilder(FLOW_SCHEDULE_JOB_NAME_PRIFIX);
        appendParam(builder, flowType);
        appendParam(builder, flowId);
        return builder.toString();
    }

    /**
     * 生成 workflow 调度任务组名称
     * <p>
     *
     * @param projectId
     * @return Job名称
     */
    public static String genJobGroupName(int projectId) {
        StringBuilder builder = new StringBuilder(FLOW_SCHEDULE_JOB_GROUP_NAME_PRIFIX);
        appendParam(builder, projectId);
        return builder.toString();
    }

    /**
     * 生成参数映射（用于参数传递）
     * <p>
     *
     * @param projectId
     * @param flowId
     * @param flowType
     * @param schedule
     * @return 参数映射
     */
    public static Map<String, Object> genDataMap(int projectId, int flowId, FlowType flowType, Schedule schedule) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(PARAM_PROJECT_ID, projectId);
        dataMap.put(PARAM_FLOW_ID, flowId);
        dataMap.put(PARAM_FLOW_TYPE, flowType);
        dataMap.put(PARAM_SCHEDULE, JsonUtil.toJsonString(schedule));
        return dataMap;
    }

    /**
     * 拼接参数
     * <p>
     *
     * @param builder
     * @param object
     */
    private static void appendParam(StringBuilder builder, Object object) {
        builder.append(NAME_SEPARATOR);
        builder.append(object);
    }

}
