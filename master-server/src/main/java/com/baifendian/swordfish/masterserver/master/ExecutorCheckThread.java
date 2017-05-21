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
package com.baifendian.swordfish.masterserver.master;

import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.masterserver.exec.ExecutorServerInfo;
import com.baifendian.swordfish.masterserver.exec.ExecutorServerManager;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * executor server 容错处理服务线程
 */
public class ExecutorCheckThread implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(ExecutorCheckThread.class);

  // 管理 executor 的 server
  private ExecutorServerManager executorServerManager;

  // 执行的 flow 队列
  private final BlockingQueue<ExecFlowInfo> executionFlowQueue;

  // 心跳超时时间
  private int timeoutInterval;

  // 工作流数据库接口
  private FlowDao flowDao;

  public ExecutorCheckThread(ExecutorServerManager executorServerManager, int timeoutInterval,
                             BlockingQueue<ExecFlowInfo> executionFlowQueue, FlowDao flowDao) {
    this.executorServerManager = executorServerManager;
    this.executionFlowQueue = executionFlowQueue;
    this.timeoutInterval = timeoutInterval;
    this.flowDao = flowDao;
  }

  @Override
  public void run() {
    logger.debug("execution flow queue size:{}", executionFlowQueue.size());

    try {
      // 得到超时的工作流列表
      List<ExecutorServerInfo> faultServers = executorServerManager.checkTimeoutServer(timeoutInterval);

      if (CollectionUtils.isEmpty(faultServers)) {
        return;
      }

      logger.error("get fault servers:{}", faultServers);

      for (ExecutorServerInfo executorServerInfo : faultServers) {
        // 查询超时工作流上的任务(这里使用数据库查询到的数据保证准确性，避免内存数据出现不一致的情况)
        List<ExecutionFlow> executionFlows = flowDao.queryNoFinishFlow(executorServerInfo.getHost() + ":" + executorServerInfo.getPort());

        // 如果查到了相应的任务
        if (!CollectionUtils.isEmpty(executionFlows)) {
          logger.info("executor server {} fault, execIds size:{} ", executorServerInfo, executionFlows.size());

          for (ExecutionFlow execFlow : executionFlows) {
            Integer execId = execFlow.getId();

            logger.info("reschedule workflow execId:{} ", execId);

            try {
              ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);

              // 重新开始调度
              if (executionFlow != null) {
                if (!executionFlow.getStatus().typeIsFinished()) {

                  logger.info("executor server fault reschedule workflow execId:{}", execId);

                  ExecFlowInfo execFlowInfo = new ExecFlowInfo(executionFlow.getId());
                  executionFlowQueue.add(execFlowInfo);
                }
              } else {
                logger.error("executor server fault reschedule workflow execId:{}, but execId:{} not exists", execId, execId);
              }
            } catch (Exception e) {
              logger.error("reschedule get error", e);
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("check thread get error", e);
    }
  }
}
