/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.execserver.runner.flow;

import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.mapper.ExecutionNodeMapper;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.execserver.exception.ExecException;
import com.baifendian.swordfish.execserver.utils.Constants;
import com.baifendian.swordfish.execserver.parameter.CustomParamManager;
import com.baifendian.swordfish.execserver.parameter.SystemParamManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Flow 执行管理器 <p>
 */
public class FlowRunnerManager {

  /**
   * LOGGER
   */
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  /**
   * {@link FlowDao}
   */
  private final FlowDao flowDao;

  /**
   * {@link ExecutionNodeMapper}
   */
  private final ExecutionNodeMapper executionNodeMapper;

  /**
   * {@link ExecutorService}
   */
  private final ExecutorService flowExecutorService;

  /**
   * {@link ExecutorService}
   */
  private final ExecutorService nodeExecutorService;

  /**
   * {@link ExecutorService}
   */
  private final ExecutorService jobExecutorService;

  /**
   * 默认的最大重试次数为 0 次
   */
  private final int defaultMaxTryTimes = 0;

  /**
   * 默认的最大超时时间是 10 小时
   */
  private final int defaultMaxTimeout = 10 * 3600;

  /**
   * 默认的节点失败后的执行策略
   */
  private final FailurePolicyType defaultFailurePolicyType = FailurePolicyType.END;

  private final Map<Integer, FlowRunner> runningFlows = new ConcurrentHashMap<>();

  private final Configuration conf;

  /**
   * constructor
   */
  public FlowRunnerManager(Configuration conf) {
    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    this.executionNodeMapper = ConnectionFactory.getSqlSession().getMapper(ExecutionNodeMapper.class);
    this.conf = conf;

    int flowThreads = conf.getInt(Constants.EXECUTOR_FLOWRUNNER_THREADS, 20);
    ThreadFactory flowThreadFactory = new ThreadFactoryBuilder().setNameFormat("Exec-Worker-FlowRunner").build();
    flowExecutorService = Executors.newFixedThreadPool(flowThreads, flowThreadFactory);

    int nodeThreads = conf.getInt(Constants.EXECUTOR_NODERUNNER_THREADS, 100);
    ThreadFactory nodeThreadFactory = new ThreadFactoryBuilder().setNameFormat("Exec-Worker-NodeRunner").build();
    nodeExecutorService = Executors.newFixedThreadPool(nodeThreads, nodeThreadFactory);

    ThreadFactory jobThreadFactory = new ThreadFactoryBuilder().setNameFormat("Exec-Worker-Job").build();
    jobExecutorService = Executors.newCachedThreadPool(jobThreadFactory);

    Thread cleanThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while(true) {
          try {
            cleanFinishedFlows();
            Thread.sleep(5000);
          } catch (Exception e){
            LOGGER.error("clean thread error ", e);
          }
        }
      }
    });
    cleanThread.setDaemon(true);
    cleanThread.setName("finishedFlowClean");
    cleanThread.start();
  }

  /**
   * 提交 workflow 执行 <p>
   */
  public void submitFlow(ExecutionFlow executionFlow) {
    // 系统参数
    Date scheduleDate = executionFlow.getScheduleTime();
    Date addDate = new Date();
    Map<String, String> systemParamMap = SystemParamManager.buildSystemParam(executionFlow, scheduleDate, addDate);

    // 自定义参数ex
    String cycTimeStr = systemParamMap.get(SystemParamManager.CYC_TIME);
    Map<String, String> customParamMap = CustomParamManager.buildCustomParam(executionFlow, cycTimeStr);

    int maxTryTimes = executionFlow.getMaxTryTimes() != null ? executionFlow.getMaxTryTimes() : defaultMaxTryTimes;
    int timeout = executionFlow.getTimeout() != null ? executionFlow.getTimeout() : defaultMaxTimeout;

    // 构造 flow runner
    FlowRunnerContext context = new FlowRunnerContext();

    context.setExecutionFlow(executionFlow);
    context.setExecutorService(nodeExecutorService);
    context.setJobExecutorService(jobExecutorService);
    context.setMaxTryTimes(maxTryTimes);
    context.setTimeout(timeout);
    context.setFailurePolicyType(defaultFailurePolicyType);
    context.setSystemParamMap(systemParamMap);
    context.setCustomParamMap(customParamMap);

    FlowRunner flowRunner = new FlowRunner(context);

    runningFlows.put(executionFlow.getId(), flowRunner);
    flowExecutorService.submit(flowRunner);
  }

  public void submitFlow(int execId) {
    ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
    submitFlow(executionFlow);
  }

  /**
   * 提交调度的 workflow 执行 <p>
   */
  public void submitFlow(ExecutionFlow executionFlow, Schedule schedule, Date scheduleDate) {
    //int maxTryTimes = schedule.getMaxTryTimes() != null ? schedule.getMaxTryTimes() : defaultMaxTryTimes;
    int maxTryTimes = schedule.getMaxTryTimes();
    int timeout = schedule.getTimeout() != 0 ? schedule.getTimeout() : defaultMaxTimeout;
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

  private void cleanFinishedFlows(){
    for(Map.Entry<Integer, FlowRunner> entry: runningFlows.entrySet()){
      ExecutionFlow executionFlow = flowDao.queryExecutionFlow(entry.getKey());
      if(executionFlow.getStatus().typeIsFinished()){
        runningFlows.remove(entry.getKey());
      }
    }
  }

  /**
   * 销毁资源 <p>
   */
  public void destroy() {

    if (!flowExecutorService.isShutdown()) {
      try {
        flowExecutorService.shutdown();
        flowExecutorService.awaitTermination(3, TimeUnit.SECONDS);
        flowExecutorService.shutdownNow();
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    if (!nodeExecutorService.isShutdown()) {
      try {
        nodeExecutorService.shutdown();
        nodeExecutorService.awaitTermination(3, TimeUnit.SECONDS);
        nodeExecutorService.shutdownNow();
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    for (FlowRunner flowRunner : runningFlows.values()) {
      flowRunner.kill();
    }

    if (!jobExecutorService.isShutdown()) {
      try {
        jobExecutorService.shutdown();
        jobExecutorService.awaitTermination(3, TimeUnit.SECONDS);
        jobExecutorService.shutdownNow();
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  public void cancelFlow(int execId, String user) {
    FlowRunner flowRunner = runningFlows.get(execId);

    if (flowRunner == null) {
      throw new ExecException("Execution " + execId + "is not running");
    }

    flowRunner.kill(user);
    runningFlows.remove(execId);
  }

  public void cancelFlow(int execId) {
    FlowRunner flowRunner = runningFlows.get(execId);

    if (flowRunner == null) {
      throw new ExecException("Execution " + execId + "is not running");
    }

    flowRunner.kill();
    runningFlows.remove(execId);
  }

}
