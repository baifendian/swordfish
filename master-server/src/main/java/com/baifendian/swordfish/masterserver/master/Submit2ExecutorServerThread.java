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

import com.baifendian.swordfish.common.mail.EmailManager;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.masterserver.config.MasterConfig;
import com.baifendian.swordfish.masterserver.exec.ExecutorClient;
import com.baifendian.swordfish.masterserver.exec.ExecutorServerInfo;
import com.baifendian.swordfish.masterserver.exec.ExecutorServerManager;
import com.baifendian.swordfish.rpc.RetInfo;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提交exec flow到exec-server 的线程 <p>
 */
public class Submit2ExecutorServerThread extends Thread {

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
  private final BlockingQueue<ExecFlowInfo> executionFlowQueue;

  /**
   * 控制是否继续执行
   */
  private volatile boolean running;

  /**
   * @param executorServerManager
   * @param flowDao
   * @param executionFlowQueue
   */
  public Submit2ExecutorServerThread(ExecutorServerManager executorServerManager, FlowDao flowDao,
      BlockingQueue<ExecFlowInfo> executionFlowQueue) {
    this.executorServerManager = executorServerManager;
    this.flowDao = flowDao;
    this.executionFlowQueue = executionFlowQueue;
    this.running = true;

    this.setName("JobExecManager-submitExecFlowToWorker");
  }

  @Override
  public void run() {
    while (running) {
      ExecFlowInfo execFlowInfo;

      try {
        // 得到要执行的 executor flow 信息
        try {
          logger.info("execution flow size: {}", executionFlowQueue.size());

          execFlowInfo = executionFlowQueue.take();
          logger.info("get execution flow from queue, exec info:{}", execFlowInfo);
        } catch (InterruptedException e) {
          logger.error("Catch interrupt exception", e);
          break;
        }

        int execId = execFlowInfo.getExecId();

        // 更新执行该任务的 worker 信息
        ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);

        // 判断 exec flow 是否超时
        if (isScheduleTimeout(executionFlow.getTimeout(), executionFlow.getStartTime().getTime())) {
          Date now = new Date();

          executionFlow.setEndTime(now);
          executionFlow.setStatus(FlowStatus.KILL);

          flowDao.updateExecutionFlow(executionFlow);

          // 发送报警
          EmailManager.sendMessageOfExecutionFlow(executionFlow);

          continue;
        }

        // 获取相应的 executor server 来执行任务
        String host = execFlowInfo.getHost();
        int port = execFlowInfo.getPort();

        ExecutorServerInfo executorServerInfo = executorServerManager.getExecutorServer(host, port);

        // 如果没有 executor server 来运行该任务, 则会重新放回
        if (executorServerInfo == null) {
          logger.error("can't found active executor server wait 5 seconds...");

          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            logger.error("Catch interrupt exception", e);
            break;
          }

          executionFlowQueue.add(execFlowInfo);
          continue;
        }

        // 设置新的提交的服务器信息
        execFlowInfo.setHost(executorServerInfo.getHost());
        execFlowInfo.setPort(executorServerInfo.getPort());

        logger.info("get execution flow from queue, execId:{} submit to exec {}:{}",
            execId, executorServerInfo.getHost(), executorServerInfo.getPort());

        executionFlow.setWorker(
            String.format("%s:%d", executorServerInfo.getHost(), executorServerInfo.getPort()));

        // 注意, 这里是不会修改起始时间等的, 起始只有 worker(实际运行的 exec-server)
        flowDao.updateExecutionFlow(executionFlow);

        boolean isSuccess = false;
        boolean isExecutorServerError = false;

        // 得到了要执行的任务, 以及要执行的 executor server, 则需要开始执行
        for (int i = 0; i < MasterConfig.failRetryCount; ++i) {
          isExecutorServerError = false;

          try {
            ExecutorClient executorClient = new ExecutorClient(executorServerInfo);

            logger.info("exec id:{}, project id:{}, executor client:{}:{}, try count:{}", execId,
                executionFlow.getProjectId(),
                executorServerInfo.getHost(), executorServerInfo.getPort(), i);

            // 可能抛出 TException 异常
            // 这里实际上不对执行状态做判断, 失败则失败了
            RetInfo retinfo = executorClient.execFlow(execId);

            isSuccess = retinfo.getStatus() == 0;

            break;
          } catch (TException e) {
            logger.error("run executor get error", e);
            isExecutorServerError = true;
          } catch (Exception e) { // 如果返回结果为 false, 这里会抛出 runtime 异常
            logger.error("inner error", e);
            break;
          }
        }

        // 多次重试后仍然失败
        if (!isSuccess) {
          // 并且是 executor server 失败
          if (isExecutorServerError) {
            // executor server error，将执行数据放回队列，将该 executor server 从 executor server 列表删除
            executionFlowQueue.add(execFlowInfo);

            logger
                .info("connect to executor server error, remove {}:{}",
                    executorServerInfo.getHost(),
                    executorServerInfo.getPort());

            // 如果是 executor server 有问题, 对有问题的 executor server 进行重新提交
            // 如果是 executor server 异常了, 可以重新对其上面的任务进行提交
            ExecutorServerInfo removedExecutionServerInfo = executorServerManager
                .removeServer(executorServerInfo);

            // 这里是 exec-server 出现了问题
            resubmitExecFlow(removedExecutionServerInfo);
          } else {
            // 如果是其它的异常情况, 直接更新状态即可
            flowDao.updateExecutionFlowStatus(execId, FlowStatus.FAILED);

            // 发送报警
            EmailManager.sendMessageOfExecutionFlow(executionFlow);
          }
        }
      } catch (Exception e) {
        logger.error("thread exception", e);
      }
    }

    logger.warn("stop execution workflow thread");
  }

  /**
   * 判断是否超时
   *
   * @param timeout 单位是秒
   * @param startTime 单位是毫秒
   */
  private boolean isScheduleTimeout(int timeout, long startTime) {
    int remainTime = timeout - (int) ((System.currentTimeMillis() - startTime) / 1000);

    if (remainTime <= 0) {
      return true;
    }

    return false;
  }

  /**
   * 重新提交工作流执行:
   *
   * 1. 超时
   * 2. 被报告下线
   * 3. 调用频繁超时
   * ......
   */
  public void resubmitExecFlow(ExecutorServerInfo executorServerInfo) {
    logger.warn("reschedule workflow of server: {} ", executorServerInfo);

    // 这里使用数据库查询到的数据保证准确性，避免内存数据出现不一致的情况
    List<ExecutionFlow> executionFlows = flowDao
        .queryNoFinishFlow(executorServerManager.getKey(executorServerInfo));

    if (!CollectionUtils.isEmpty(executionFlows)) {
      for (ExecutionFlow execFlow : executionFlows) {
        Integer execId = execFlow.getId();

        ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);

        if (executionFlow != null) {
          if (!executionFlow.getStatus().typeIsFinished()) {
            logger.warn("reschedule exec id: {}", execId);

            Pair<String, Integer> pair = CommonUtil.parseWorker(executionFlow.getWorker());

            ExecFlowInfo execFlowInfo = (pair == null) ? new ExecFlowInfo(executionFlow.getId())
                : new ExecFlowInfo(pair.getLeft(), pair.getRight(), executionFlow.getId());

            executionFlowQueue.add(execFlowInfo);
          }
        } else {
          logger.warn("executor server fault reschedule workflow execId:{} not exists", execId);
        }
      }
    }
  }

  /**
   * 取消执行
   */
  public void disable() {
    this.running = false;
  }
}
