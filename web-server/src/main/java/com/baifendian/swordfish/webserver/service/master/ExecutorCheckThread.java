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
package com.baifendian.swordfish.webserver.service.master;

import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.webserver.ExecutorServerInfo;
import com.baifendian.swordfish.webserver.ExecutorServerManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * executor server 容错处理服务线程
 *
 * @author : liujin
 * @date : 2017-03-13 10:04
 */
public class ExecutorCheckThread implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(ExecutorCheckThread.class);

  private ExecutorServerManager executorServerManager;

  private int timeoutInterval;

  private FlowDao flowDao;

  private BlockingQueue<ExecutionFlow> executionFlowBlockingQueue;

  public ExecutorCheckThread(ExecutorServerManager executorServerManager, int timeoutInterval,
                             BlockingQueue<ExecutionFlow> executionFlowBlockingQueue, FlowDao flowDao) {
    this.executorServerManager = executorServerManager;
    this.timeoutInterval = timeoutInterval;
    this.executionFlowBlockingQueue = executionFlowBlockingQueue;
    this.flowDao = flowDao;
  }

  @Override
  public void run() {
    logger.debug("blocking queue size:{}, {}", executionFlowBlockingQueue.size(), executionFlowBlockingQueue);
    List<ExecutorServerInfo> faultServers = executorServerManager.checkTimeoutServer(timeoutInterval);
    if (faultServers != null) {
      logger.debug("get fault servers:{}", faultServers);
      for (ExecutorServerInfo executorServerInfo : faultServers) {
        if (executorServerInfo.getHeartBeatData() != null && executorServerInfo.getHeartBeatData().getExecIdsSize() > 0) {
          for (Long execId : executorServerInfo.getHeartBeatData().getExecIds()) {
            ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
            if (executionFlow != null) {
              if (!executionFlow.getStatus().typeIsFinished()) {
                logger.info("executor server fault reschedule workflow execId:{}", execId);
                executionFlowBlockingQueue.add(executionFlow);
              }
            } else {
              logger.warn("executor server fault reschedule workflow execId:{} not exists", execId);
            }
          }
        }
      }
    }
  }
}
