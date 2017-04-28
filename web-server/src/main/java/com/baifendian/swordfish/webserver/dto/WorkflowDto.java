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

import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.baifendian.swordfish.webserver.dto.WorkflowData;
import com.baifendian.swordfish.webserver.dto.WorkflowNodeDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工作流返回DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowDto {
  private String name;
  private String desc;
  private WorkflowData data;
  private String proxyUser;
  private String queue;
  private Date createTime;
  private Date modifyTime;
  private String owner;
  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String extras;
  private String projectName;

  public WorkflowDto() {
  }

  public WorkflowDto(ProjectFlow projectFlow) {
    if (projectFlow != null){
      this.name = projectFlow.getName();
      this.desc = projectFlow.getDesc();
      if (CollectionUtils.isNotEmpty(projectFlow.getFlowsNodes())){
        List<WorkflowNodeDto> workflowNodeResponseList = new ArrayList<>();
        for (FlowNode flowNode:projectFlow.getFlowsNodes()){
          workflowNodeResponseList.add(new WorkflowNodeDto(flowNode));
        }
        this.data = new WorkflowData(workflowNodeResponseList,projectFlow.getUserDefinedParamList());
      }
      this.proxyUser = projectFlow.getProxyUser();
      this.queue = projectFlow.getQueue();
      this.createTime = projectFlow.getCreateTime();
      this.modifyTime = projectFlow.getModifyTime();
      this.owner = projectFlow.getOwner();
      this.extras = projectFlow.getExtras();
      this.projectName = projectFlow.getProjectName();
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public WorkflowData getData() {
    return data;
  }

  public void setData(WorkflowData data) {
    this.data = data;
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

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Date modifyTime) {
    this.modifyTime = modifyTime;
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
}
