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
package com.baifendian.swordfish.execserver.runner.node;

import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ExecutionNode;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.execserver.job.Job;
import com.baifendian.swordfish.execserver.job.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 节点执行器 <p>
 */
public class NodeRunner implements Runnable {

  /**
   * logger
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * {@link FlowDao}
   */
  private final FlowDao flowDao;

  /**
   * {@link ExecutionFlow}
   */
  private final ExecutionFlow executionFlow;

  /**
   * {@link ExecutionNode}
   */
  private final ExecutionNode executionNode;

  /**
   * 同步对象
   */
  private final Object synObject;

  /**
   * job 处理器
   */
  private JobHandler jobHandler;

  /**
   * @param executionFlow
   * @param executionNode
   * @param node
   * @param executorService
   * @param synObject
   * @param timeout
   * @param customParamMap
   * @param systemParamMap
   */
  public NodeRunner(ExecutionFlow executionFlow, ExecutionNode executionNode, FlowNode node, ExecutorService executorService, Object synObject, int timeout,
                    Map<String, String> systemParamMap, Map<String, String> customParamMap) {
    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    this.executionFlow = executionFlow;
    this.executionNode = executionNode;
    this.synObject = synObject;
    this.jobHandler = new JobHandler(flowDao, executionFlow, executionNode, node, executorService, timeout, systemParamMap, customParamMap);
  }

  @Override
  public void run() {
    FlowStatus status = null;
    try {
      // 具体执行
      status = jobHandler.handle();

      logger.info("run executor:{} node:{} finished, status:{}", executionFlow.getId(), executionNode.getName(), status);
    } catch (Exception e) {
      logger.error(String.format("job %s error", jobHandler.getJobIdLog()), e);
    } finally {
      if (status == null) {
        updateExecutionNode(FlowStatus.FAILED);
      }

      // 唤醒 flow runner 线程
      notifyFlowRunner();
    }
  }

  /**
   * 更新数据库中的 ExecutionNode 信息 <p>
   */
  private void updateExecutionNode(FlowStatus flowStatus) {
    Date now = new Date();

    executionNode.setStatus(flowStatus);
    executionNode.setEndTime(now);
    flowDao.updateExecutionNode(executionNode);
  }

  /**
   * 唤醒 flow runner 线程 <p>
   */
  private void notifyFlowRunner() {
    synchronized (synObject) {
      synObject.notifyAll();
    }
  }

  /**
   * 关闭 node 运行
   */
  public void kill() {
    logger.info("kill has been called on node:{} ", executionNode.getName());
    if (executionNode.getStatus().typeIsFinished()) {
      logger.debug("node:{} status is {} ignore", executionNode.getName(), executionNode.getStatus().name());
      return;
    }

    Job job = jobHandler.getJob();
    if (job == null) {
      logger.info("Job hasn't started");
      return;
    }

    try {
      job.cancel();
    } catch (Exception e) {
      logger.error("cancel job error", e);
    }

    updateExecutionNode(FlowStatus.KILL);
  }
}
