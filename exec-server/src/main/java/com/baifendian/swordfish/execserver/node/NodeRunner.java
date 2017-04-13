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
package com.baifendian.swordfish.execserver.node;

import com.baifendian.swordfish.common.job.Job;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ExecutionNode;
import com.baifendian.swordfish.dao.model.FlowNode;
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
   * LOGGER
   */
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  /**
   * 超时时间
   */
  private final int timeout;

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
   * {@link FlowNode}
   */
  private final FlowNode node;

  /**
   * {@link ExecutorService}
   */
  private final ExecutorService executorService;

  /**
   * 同步对象
   */
  private final Object synObject;

  /**
   * 系统参数
   */
  private final Map<String, String> systemParamMap;

  /**
   * 自定义参数
   */
  private final Map<String, String> customParamMap;

  private boolean killed = false;

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
    this.node = node;
    this.executorService = executorService;
    this.synObject = synObject;
    this.timeout = timeout;
    this.systemParamMap = systemParamMap;
    this.customParamMap = customParamMap;
    // 生成具体 handler
    this.jobHandler = new JobHandler(flowDao, executionFlow, executionNode, node, executorService, timeout, systemParamMap, customParamMap);
  }

  @Override
  public void run() {
    FlowStatus status = null;
    try {
      // 具体执行
      status = jobHandler.handle();

      LOGGER.info("run executor:{} node:{} finished, status:{}", executionFlow.getId(), executionNode.getName(), status);

      // 更新 executionNode 信息
      updateExecutionNode(status);

    } catch (Exception e) {
      LOGGER.error("{}", jobHandler.getJobIdLog() + e.getMessage(), e);
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
    executionNode.setStatus(flowStatus);
    executionNode.setEndTime(new Date());
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

  public void kill() {
    LOGGER.info("kill has been called on node:{} ", executionNode.getName());
    if (executionNode.getStatus().typeIsFinished()) {
      LOGGER.debug("node:{} status is {} ignore", executionNode.getName(), executionNode.getStatus().name());
      return;
    }
    killed = true;

    Job job = jobHandler.getJob();
    if (job == null) {
      LOGGER.info("Job hasn't started");
      return;
    }

    try {
      job.cancel();
    } catch (Exception e) {
      LOGGER.error("cancel job error", e);
    }
    updateExecutionNode(FlowStatus.KILL);
  }

}
