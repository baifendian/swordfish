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
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.execserver.utils.Constants;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow 执行管理器 <p>
 */
public class FlowRunnerManager {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * {@link FlowDao}
   */
  private final FlowDao flowDao;

  /**
   * flow 的调度执行管理线程池
   */
  private final ExecutorService flowExecutorService;

  /**
   * 任务结点调度执行管理线程池
   */
  private final ExecutorService nodeExecutorService;

  /**
   * 正在运行的 flows, key => flow id, value => flow runner 线程
   */
  private final Map<Integer, FlowRunner> runningFlows = new ConcurrentHashMap<>();

  public FlowRunnerManager(Configuration conf) {
    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);

    int flowThreads = conf
        .getInt(Constants.EXECUTOR_FLOWRUNNER_THREADS, Constants.defaultFlowRunnerThreadNum);
    ThreadFactory flowThreadFactory = new ThreadFactoryBuilder()
        .setNameFormat("Exec-Worker-FlowRunner").build();
    flowExecutorService = Executors.newFixedThreadPool(flowThreads, flowThreadFactory);

    int nodeThreads = conf
        .getInt(Constants.EXECUTOR_NODERUNNER_THREADS, Constants.defaultNodeRunnerThreadNum);
    ThreadFactory nodeThreadFactory = new ThreadFactoryBuilder()
        .setNameFormat("Exec-Worker-NodeRunner").build();
    nodeExecutorService = Executors.newFixedThreadPool(nodeThreads, nodeThreadFactory);

    // 主要指清理 runningFlows 中运行完成的任务
    Thread cleanThread = new Thread(() -> {
      while (true) {
        try {
          cleanFinishedFlows();
        } catch (Exception e) {
          logger.error("clean thread error ", e);
        } finally {
          try {
            Thread.sleep(Constants.defaultCleanFinishFlowInterval);
          } catch (InterruptedException e) {
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
    if (runningFlows.containsKey(executionFlow.getId())) {
      logger.info("flow is in running: {}", executionFlow.getId());
      return;
    }

    int maxTryTimes = executionFlow.getMaxTryTimes() != null ? executionFlow.getMaxTryTimes()
        : Constants.defaultMaxTryTimes;
    int timeout = executionFlow.getTimeout() != null ? executionFlow.getTimeout()
        : Constants.defaultMaxTimeout;

    // 构造 flow runner 的上下文信息
    FlowRunnerContext context = new FlowRunnerContext();

    context.setExecutionFlow(executionFlow);
    context.setNodeExecutorService(nodeExecutorService);
    context.setMaxTryTimes(maxTryTimes);
    context.setTimeout(timeout);
    context.setFailurePolicyType(executionFlow.getFailurePolicy());

    FlowRunner flowRunner = new FlowRunner(context);

    // 如果没有运行, 则提交
    if (!runningFlows.containsKey(executionFlow.getId())) {
      runningFlows.putIfAbsent(executionFlow.getId(), flowRunner);

      logger.info(
          "submit flow, exec id: {}, project name: {}, workflow name: {}, max try times: {}, timeout: {}",
          executionFlow.getId(), executionFlow.getProjectName(), executionFlow.getWorkflowName(),
          maxTryTimes, timeout);

      flowExecutorService.submit(flowRunner);
    }
  }

  /**
   * 清理完成的 flows
   */
  private void cleanFinishedFlows() {
    List<Integer> finishFlows = new ArrayList<>();

    synchronized (this) {
      for (Map.Entry<Integer, FlowRunner> entry : runningFlows.entrySet()) {
        ExecutionFlow executionFlow = flowDao.queryExecutionFlow(entry.getKey());

        if (executionFlow != null && executionFlow.getStatus().typeIsFinished()) {
          finishFlows.add(entry.getKey());
        }
      }
    }

    for (Integer id : finishFlows) {
      runningFlows.remove(id);
    }
  }

  /**
   * 销毁资源 <p>
   */
  public void destroy() {
    // 关闭 flow executor 线程池, 不接受任务
    shutdownExecutorService(flowExecutorService, false);

    // 关闭 node executor 线程池, 不接受任务
    shutdownExecutorService(nodeExecutorService, false);

    for (FlowRunner flowRunner : runningFlows.values()) {
      flowRunner.shutdown();

      // 更新状态
      flowRunner.clean(false);

      // 更新为 kill 状态
      flowRunner.updateExecutionFlowToKillStatus(false);
    }

    // 关闭 flow executor 线程池, 强险停止
    shutdownExecutorService(flowExecutorService, true);

    // 关闭 flow executor 线程池, 强险停止
    shutdownExecutorService(nodeExecutorService, true);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      logger.error("Catch an interrupt exception", e);
    }
  }

  /**
   * 关闭 executor service
   */
  private void shutdownExecutorService(ExecutorService executorService, boolean shutdownNow) {
    if (!executorService.isShutdown()) {
      try {
        if (!shutdownNow) {
          executorService.shutdown();
        } else {
          executorService.shutdownNow();
        }

        executorService.awaitTermination(3, TimeUnit.SECONDS);
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
  }

  /**
   * 取消执行的 flow
   */
  public void cancelFlow(ExecutionFlow executionFlow) {
    int execId = executionFlow.getId();

    FlowRunner flowRunner = runningFlows.get(execId);

    if (flowRunner == null) {
      logger.error("Execution id {} is not running", execId);
      return;
    }

    flowRunner.clean(true);
    runningFlows.remove(execId);
  }
}
