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
package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ExecutionNode;
import com.baifendian.swordfish.dao.model.FlowNode;
import java.util.concurrent.Semaphore;

public class JobContext {
  /**
   * 执行 flow 信息
   */
  private ExecutionFlow executionFlow;

  /**
   * 执行 node 信息
   */
  private ExecutionNode executionNode;

  /**
   * flow node 的具体信息
   */
  private FlowNode flowNode;

  /**
   * 信号量
   */
  private Semaphore semaphore;

  public ExecutionFlow getExecutionFlow() {
    return executionFlow;
  }

  public void setExecutionFlow(ExecutionFlow executionFlow) {
    this.executionFlow = executionFlow;
  }

  public ExecutionNode getExecutionNode() {
    return executionNode;
  }

  public void setExecutionNode(ExecutionNode executionNode) {
    this.executionNode = executionNode;
  }

  public FlowNode getFlowNode() {
    return flowNode;
  }

  public void setFlowNode(FlowNode flowNode) {
    this.flowNode = flowNode;
  }

  public Semaphore getSemaphore() {
    return semaphore;
  }

  public void setSemaphore(Semaphore semaphore) {
    this.semaphore = semaphore;
  }
}
