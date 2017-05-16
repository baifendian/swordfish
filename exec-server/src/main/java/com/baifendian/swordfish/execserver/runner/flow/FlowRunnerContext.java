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

import java.util.Map;
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
  private ExecutorService executorService;

  /**
   * {@link ExecutorService}
   */
  private ExecutorService jobExecutorService;

  /**
   * 调度信息
   */
  private Schedule schedule;

  /**
   * 一个节点失败后的策略类型
   */
  private FailurePolicyType failurePolicyType;

  /**
   * 最大重试次数
   */
  private int maxTryTimes;

  /**
   * 节点最大的超时时间 (2)
   */
  private int timeout;

  /**
   * 系统参数
   */
  private Map<String, String> systemParamMap;

  /**
   * 自定义参数
   */
  private Map<String, String> customParamMap;

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
   * @return the executorService
   * @see FlowRunnerContext#executorService
   */
  public ExecutorService getExecutorService() {
    return executorService;
  }

  /**
   * setter method
   *
   * @param executorService the executorService to set
   * @see FlowRunnerContext#executorService
   */
  public void setExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
  }

  /**
   * getter method
   *
   * @return the jobExecutorService
   * @see FlowRunnerContext#jobExecutorService
   */
  public ExecutorService getJobExecutorService() {
    return jobExecutorService;
  }

  /**
   * setter method
   *
   * @param jobExecutorService the jobExecutorService to set
   * @see FlowRunnerContext#jobExecutorService
   */
  public void setJobExecutorService(ExecutorService jobExecutorService) {
    this.jobExecutorService = jobExecutorService;
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

  /**
   * getter method
   *
   * @return the systemParamMap
   * @see FlowRunnerContext#systemParamMap
   */
  public Map<String, String> getSystemParamMap() {
    return systemParamMap;
  }

  /**
   * setter method
   *
   * @param systemParamMap the systemParamMap to set
   * @see FlowRunnerContext#systemParamMap
   */
  public void setSystemParamMap(Map<String, String> systemParamMap) {
    this.systemParamMap = systemParamMap;
  }

  /**
   * getter method
   *
   * @return the customParamMap
   * @see FlowRunnerContext#customParamMap
   */
  public Map<String, String> getCustomParamMap() {
    return customParamMap;
  }

  /**
   * setter method
   *
   * @param customParamMap the customParamMap to set
   * @see FlowRunnerContext#customParamMap
   */
  public void setCustomParamMap(Map<String, String> customParamMap) {
    this.customParamMap = customParamMap;
  }
}
