/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.baifendian.swordfish.execserver.flow;

import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.mysql.MyBatisSqlSessionFactoryUtil;
import com.baifendian.swordfish.dao.mysql.enums.FailurePolicyType;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.dao.mysql.mapper.ExecutionNodeMapper;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.ExecutionNode;
import com.baifendian.swordfish.dao.mysql.model.Schedule;
import com.baifendian.swordfish.execserver.node.ResourceHelper;
import com.baifendian.swordfish.execserver.parameter.CustomParamManager;
import com.baifendian.swordfish.execserver.parameter.SystemParamManager;
import com.bfd.harpc.monitor.NamedThreadFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Flow 执行管理器
 * <p>
 * 
 * @author : liujin
 * @date : 2017年3月01日
 */
public class FlowRunnerManager {

    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** {@link FlowDao} */
    private final FlowDao flowDao;

    /** {@link ExecutionNodeMapper} */
    private final ExecutionNodeMapper executionNodeMapper;

    /** {@link ExecutorService} */
    private final ExecutorService flowExecutorService;

    /** {@link ExecutorService} */
    private final ExecutorService nodeExecutorService;

    /** {@link ExecutorService} */
    private final ExecutorService jobExecutorService;

    /** 默认的最大重试次数为 0 次 */
    private final int defaultMaxTryTimes = 0;

    /** 默认的最大超时时间是 10 小时 */
    private final int defaultMaxTimeout = 10 * 3600;

    /** 默认的节点失败后的执行策略 */
    private final FailurePolicyType defaultFailurePolicyType = FailurePolicyType.END;

    private final Map<Long, FlowRunner> runningFlows = new ConcurrentHashMap<>();

    /**
     * constructor
     */
    public FlowRunnerManager() {
        this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
        this.executionNodeMapper = MyBatisSqlSessionFactoryUtil.getSqlSession().getMapper(ExecutionNodeMapper.class);

        // 启动清除线程
        startCleanThread();

        NamedThreadFactory flowThreadFactory = new NamedThreadFactory("Exec-Worker-FlowRunner");
        flowExecutorService = Executors.newCachedThreadPool(flowThreadFactory);

        NamedThreadFactory nodeThreadFactory = new NamedThreadFactory("Exec-Worker-NodeRunner");
        nodeExecutorService = Executors.newCachedThreadPool(nodeThreadFactory);

        NamedThreadFactory jobThreadFactory = new NamedThreadFactory("Exec-Worker-Job");
        jobExecutorService = Executors.newCachedThreadPool(jobThreadFactory);
    }

    /**
     * 启动清除线程
     * <p>
     */
    private void startCleanThread() {
        Thread cleanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 清除老的任务
                cleanOldExecutions();
            }
        }, "Exec-Worker-CleanOldExec");
        cleanThread.setDaemon(true);
        cleanThread.start();
    }

    /**
     * 清除老的执行任务
     * <p>
     */
    private void cleanOldExecutions() {
        File execPath = new File(ResourceHelper.getExecBasePath());
        if (execPath.exists()) {
            File[] files = execPath.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        Long execId = Long.valueOf(file.getName());
                        ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
                        // 如果状态不是完成状态，则更新状态为失败
                        if (!executionFlow.getStatus().typeIsFinished()) {
                            flowDao.updateExecutionFlowStatus(execId, FlowStatus.FAILED);
                            List<ExecutionNode> executionNodes = executionNodeMapper.selectExecNodesStatus(execId, executionFlow.getFlowId());
                            if (CollectionUtils.isNotEmpty(executionNodes)) {
                                for (ExecutionNode executionNode : executionNodes) {
                                    if (!executionNode.getStatus().typeIsFinished()) {
                                        executionNode.setStatus(FlowStatus.FAILED);
                                        flowDao.updateExecutionNode(executionNode);
                                    }
                                }
                            }
                        }

                        // 删除文件夹
                        FileUtils.deleteQuietly(file);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * 提交 workflow 执行
     * <p>
     *
     * @param executionFlow
     */
    public void submitFlow(ExecutionFlow executionFlow) {
        // 系统参数
        Date scheduleDate = new Date();
        Date addDate = new Date();
        Map<String, String> systemParamMap = SystemParamManager.buildSystemParam(executionFlow, scheduleDate, addDate);

        // 自定义参数
        String cycTimeStr = systemParamMap.get(SystemParamManager.CYC_TIME);
        Map<String, String> customParamMap = CustomParamManager.buildCustomParam(executionFlow, cycTimeStr);

        int maxTryTimes = defaultMaxTryTimes;
        if (executionFlow.getFlowType() == FlowType.LONG) {
            maxTryTimes = 0; // 长任务暂时不重试
        }

        // 构造 flow runner
        FlowRunnerContext context = new FlowRunnerContext();

        context.setExecutionFlow(executionFlow);
        context.setExecutorService(nodeExecutorService);
        context.setJobExecutorService(jobExecutorService);
        context.setMaxTryTimes(maxTryTimes);
        context.setTimeout(defaultMaxTimeout);
        context.setFailurePolicyType(defaultFailurePolicyType);
        context.setSystemParamMap(systemParamMap);
        context.setCustomParamMap(customParamMap);

        FlowRunner flowRunner = new FlowRunner(context);

        runningFlows.put(executionFlow.getId(), flowRunner);
        flowExecutorService.submit(flowRunner);
    }

    public void submitFlow(long execId) {
        ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
        submitFlow(executionFlow);
    }

    /**
     * 提交调度的 workflow 执行
     * <p>
     *
     * @param executionFlow
     * @param schedule
     * @param scheduleDate
     */
    public void submitFlow(ExecutionFlow executionFlow, Schedule schedule, Date scheduleDate) {
        int maxTryTimes = schedule.getMaxTryTimes() != null ? schedule.getMaxTryTimes() : defaultMaxTryTimes;
        int timeout = schedule.getTimeout() != null ? schedule.getTimeout() : defaultMaxTimeout;
        FailurePolicyType failurePolicy = schedule.getFailurePolicy() != null ? schedule.getFailurePolicy() : defaultFailurePolicyType;

        // 系统参数
        Map<String, String> systemParamMap = SystemParamManager.buildSystemParam(executionFlow, scheduleDate, scheduleDate);
        // 自定义参数
        String cycTimeStr = systemParamMap.get(SystemParamManager.CYC_TIME);
        Map<String, String> customParamMap = CustomParamManager.buildCustomParam(executionFlow, cycTimeStr);

        FlowRunnerContext context = new FlowRunnerContext();
        context.setSchedule(schedule);
        context.setExecutionFlow(executionFlow);
        context.setExecutorService(nodeExecutorService);
        context.setJobExecutorService(jobExecutorService);
        context.setMaxTryTimes(maxTryTimes);
        context.setTimeout(timeout);
        context.setFailurePolicyType(failurePolicy);
        context.setSystemParamMap(systemParamMap);
        context.setCustomParamMap(customParamMap);
        FlowRunner flowRunner = new FlowRunner(context);

        runningFlows.put(executionFlow.getId(), flowRunner);
        flowExecutorService.submit(flowRunner);
    }

    /**
     * 销毁资源
     * <p>
     */
    public void destroy() {
        if (!flowExecutorService.isShutdown()) {
            try {
                flowExecutorService.shutdownNow();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        if (!nodeExecutorService.isShutdown()) {
            try {
                nodeExecutorService.shutdownNow();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        if (!jobExecutorService.isShutdown()) {
            try {
                jobExecutorService.shutdownNow();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void cancelFlow(long execId, String user)   {
        FlowRunner flowRunner = runningFlows.get(execId);

        if(flowRunner == null){
            throw new ExecException("Execution " + execId + "is not running");
        }

        flowRunner.kill(user);
        runningFlows.remove(execId);
    }

}
