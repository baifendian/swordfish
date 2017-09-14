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

import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import java.util.concurrent.ExecutorService;

/**
 * workflow 执行的上下文 <p>
 */
public class FlowRunnerContext {
  /**
   * {@link ExecutionFlow}
   */
  private ExecutionFlow executionFlow;

  /**
   * {@link ExecutorService}
   */
  private ExecutorService nodeExecutorService;

  /**
   * 调度信息
   */
  private Schedule schedule;

  /**
   * 一个节点失败后的策略类型, 比如是停止还是继续
   */
  private FailurePolicyType failurePolicyType;

  /**
   * 最大重试次数
   */
  private int maxTryTimes;

  /**
   * 节点最大的超时时间
   */
  private int timeout;

  /**
   * getter method
   *
   * @return the executionFlow
   * @see FlowRunnerContext#executionFlow
   */
  public ExecutionFlow getExecutionFlow() {
    return executionFlow;
  }

  /**
   * setter method
   *
   * @param executionFlow the executionFlow to set
   * @see FlowRunnerContext#executionFlow
   */
  public void setExecutionFlow(ExecutionFlow executionFlow) {
    this.executionFlow = executionFlow;
  }

  /**
   * getter method
   *
   * @return the nodeExecutorService
   * @see FlowRunnerContext#nodeExecutorService
   */
  public ExecutorService getNodeExecutorService() {
    return nodeExecutorService;
  }

  /**
   * setter method
   *
   * @param nodeExecutorService the nodeExecutorService to set
   * @see FlowRunnerContext#nodeExecutorService
   */
  public void setNodeExecutorService(ExecutorService nodeExecutorService) {
    this.nodeExecutorService = nodeExecutorService;
  }

  /**
   * getter method
   *
   * @return the schedule
   * @see FlowRunnerContext#schedule
   */
  public Schedule getSchedule() {
    return schedule;
  }

  /**
   * setter method
   *
   * @param schedule the schedule to set
   * @see FlowRunnerContext#schedule
   */
  public void setSchedule(Schedule schedule) {
    this.schedule = schedule;
  }

  /**
   * getter method
   *
   * @return the failurePolicyType
   * @see FlowRunnerContext#failurePolicyType
   */
  public FailurePolicyType getFailurePolicyType() {
    return failurePolicyType;
  }

  /**
   * setter method
   *
   * @param failurePolicyType the failurePolicyType to set
   * @see FlowRunnerContext#failurePolicyType
   */
  public void setFailurePolicyType(FailurePolicyType failurePolicyType) {
    this.failurePolicyType = failurePolicyType;
  }

  /**
   * getter method
   *
   * @return the maxTryTimes
   * @see FlowRunnerContext#maxTryTimes
   */
  public int getMaxTryTimes() {
    return maxTryTimes;
  }

  /**
   * setter method
   *
   * @param maxTryTimes the maxTryTimes to set
   * @see FlowRunnerContext#maxTryTimes
   */
  public void setMaxTryTimes(int maxTryTimes) {
    this.maxTryTimes = maxTryTimes;
  }

  /**
   * getter method
   *
   * @return the timeout
   * @see FlowRunnerContext#timeout
   */
  public int getTimeout() {
    return timeout;
  }

  /**
   * setter method
   *
   * @param timeout the timeout to set
   * @see FlowRunnerContext#timeout
   */
  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }
}
