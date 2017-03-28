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
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.webserver.ExecutorClient;
import com.baifendian.swordfish.webserver.ExecutorServerInfo;
import com.baifendian.swordfish.webserver.ExecutorServerManager;
import com.baifendian.swordfish.webserver.config.MasterConfig;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * 提交exec flow到exec-server 的线程 <p>
 */
public class Submit2ExecutorServerThread extends Thread {

  /**
   * LOGGER
   */
  private final Logger logger = LoggerFactory.getLogger(Submit2ExecutorServerThread.class);

  /**
   * executor server manager
   */
  private final ExecutorServerManager executorServerManager;

  /**
   * {@link FlowDao}
   */
  private final FlowDao flowDao;

  /**
   * workflow 执行队列
   */
  private final BlockingQueue<ExecutionFlow> executionFlowQueue;

  /**
   * @param executorServerManager
   * @param flowDao
   * @param executionFlowQueue
   */
  public Submit2ExecutorServerThread(ExecutorServerManager executorServerManager, FlowDao flowDao, BlockingQueue<ExecutionFlow> executionFlowQueue) {
    this.executorServerManager = executorServerManager;
    this.flowDao = flowDao;
    this.executionFlowQueue = executionFlowQueue;

    this.setName("Master-submitExecFlowToWorker");
  }

  @Override
  public void run() {
    while (true) {
      ExecutionFlow executionFlow;
      try {
        executionFlow = executionFlowQueue.take();
        logger.info("get execution flow from queue, flowId:{} execId:{}", executionFlow.getFlowId(), executionFlow.getId());
      } catch (InterruptedException e) {
        logger.error(e.getMessage(), e);
        break; // 中断则退出
      }

      long execId = executionFlow.getId();
      boolean isSucess = false; // 是否请求成功
      boolean isExecutorServerError = false;
      ExecutorServerInfo executorServerInfo = executorServerManager.getExecutorServer();
      if (executorServerInfo == null) {
        logger.error("can't found active executor server wait 5 seconds...");
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        executionFlowQueue.add(executionFlow);
        continue;
      }
      logger.info("get execution flow from queue, flowId:{} execId:{} submit to exec {}:{}",
              executionFlow.getFlowId(), executionFlow.getId(), executorServerInfo.getHost(), executorServerInfo.getPort());
      for (int i = 0; i < MasterConfig.failRetryCount; i++) {
        isExecutorServerError = false;
        try {
          ExecutorClient executorClient = new ExecutorClient(executorServerInfo);
          executionFlow.setWorker(String.format("%s:%d", executorServerInfo.getHost(), executorServerInfo.getPort()));
          logger.debug("execId:{}", execId);
          logger.debug("projectId:{}", executionFlow.getProjectId());
          logger.debug("client:{}", executorClient);
          executorClient.execFlow(execId);
          flowDao.updateExecutionFlow(executionFlow);
          isSucess = true;
          break; // 请求成功，结束重试请求
        } catch (TException e) {
          ExecutionFlow temp = flowDao.queryExecutionFlow(execId);
          // 如果执行被取消或者状态已经更新，结束重试请求
          if (temp == null || temp.getStatus() != FlowStatus.INIT) {
            break;
          }
          logger.error("run executor get error", e);
          isExecutorServerError = true;
        } catch (Exception e) { // 内部错误
          logger.error("inner error", e);
        }
      }

      // 多次重试后仍然失败
      if (!isSucess) {
        if (isExecutorServerError) {
          /** executor server error，将执行数据放回队列，将该executor server从executor server列表删除 */
          executionFlowQueue.add(executionFlow);
          logger.info("connect to executor server error, remove {}:{}", executorServerInfo.getHost(), executorServerInfo.getPort());
          ExecutorServerInfo removedExecutionServerInfo = executorServerManager.removeServer(executorServerInfo);
          resubmitExecFlow(removedExecutionServerInfo);
        } else {
          flowDao.updateExecutionFlowStatus(execId, FlowStatus.FAILED);
        }
      }
    }
  }

  private void resubmitExecFlow(ExecutorServerInfo executorServerInfo) {
    if (executorServerInfo.getHeartBeatData() != null && executorServerInfo.getHeartBeatData().getExecIdsSize() > 0) {
      for (Long execId : executorServerInfo.getHeartBeatData().getExecIds()) {
        ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
        if (executionFlow != null) {
          if (!executionFlow.getStatus().typeIsFinished()) {
            logger.info("executor server fault reschedule workflow execId:{}", execId);
            executionFlowQueue.add(executionFlow);
          }
        } else {
          logger.warn("executor server fault reschedule workflow execId:{} not exists", execId);
        }
      }
    }
  }

}
