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
package com.baifendian.swordfish.webserver.dto;

import com.baifendian.swordfish.common.json.JsonOrdinalSerializer;
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * 执行flow返回response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecutionFlowDto {
  private int execId;
  private String projectName;
  private String workflowName;
  private ExecType execType;
  private Date submitTime;
  private Date startTime;
  private Date endTime;
  private int duration;
  private String submitUser;
  private String proxyUser;
  private String queue;
  @JsonSerialize(using = JsonOrdinalSerializer.class)
  private FlowStatus status;
  private String owner;
  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String extras;
  private ExecutionFlowData data;


  public ExecutionFlowDto() {
  }

  public ExecutionFlowDto(ExecutionFlow executionFlow) {
    if (executionFlow != null){
      this.execId = executionFlow.getId();
      this.projectName = executionFlow.getProjectName();
      this.workflowName = executionFlow.getWorkflowName();
      this.execType = executionFlow.getExecType();
      this.submitTime = executionFlow.getSubmitTime();
      this.startTime = executionFlow.getStartTime();
      this.endTime = executionFlow.getEndTime();
      if (this.startTime != null && this.endTime != null) {
        this.duration = Math.toIntExact((this.endTime.getTime() - this.startTime.getTime()) / 1000);
      }
      this.submitUser = executionFlow.getSubmitUser();
      this.proxyUser = executionFlow.getProxyUser();
      this.queue = executionFlow.getQueue();
      this.status = executionFlow.getStatus();
      this.owner = executionFlow.getOwner();
      this.extras = executionFlow.getExtras();
      if (StringUtils.isNotEmpty(executionFlow.getWorkflowData())){
        this.data = JsonUtil.parseObject(executionFlow.getWorkflowData(),ExecutionFlowData.class);
      }
    }
  }

  public int getExecId() {
    return execId;
  }

  public void setExecId(int execId) {
    this.execId = execId;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getWorkflowName() {
    return workflowName;
  }

  public void setWorkflowName(String workflowName) {
    this.workflowName = workflowName;
  }

  public ExecType getExecType() {
    return execType;
  }

  public void setExecType(ExecType execType) {
    this.execType = execType;
  }

  public Date getSubmitTime() {
    return submitTime;
  }

  public void setSubmitTime(Date submitTime) {
    this.submitTime = submitTime;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getSubmitUser() {
    return submitUser;
  }

  public void setSubmitUser(String submitUser) {
    this.submitUser = submitUser;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  public FlowStatus getStatus() {
    return status;
  }

  public void setStatus(FlowStatus status) {
    this.status = status;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getExtras() {
    return extras;
  }

  public void setExtras(String extras) {
    this.extras = extras;
  }

  public ExecutionFlowData getData() {
    return data;
  }

  public void setData(ExecutionFlowData data) {
    this.data = data;
  }
}
