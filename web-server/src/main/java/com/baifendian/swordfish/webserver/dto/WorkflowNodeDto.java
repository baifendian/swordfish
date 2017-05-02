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
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * 工作流节点返回
 */
public class WorkflowNodeDto {
  private String name;
  private String desc;
  private String type;
  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String parameter;
  private List<String> dep;
  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String extras;

  public WorkflowNodeDto() {
  }

  /**
   * 通过一个数据实体实例化DTO
   * @param flowNode
   */
  public WorkflowNodeDto(FlowNode flowNode) {
    if (flowNode != null){
      this.name = flowNode.getName();
      this.desc = flowNode.getDesc();
      this.type = flowNode.getType();
      this.parameter = flowNode.getParameter();
      this.dep = flowNode.getDepList();
      this.extras = flowNode.getExtras();
    }
  }

  /**
   * 把DTO转化为一个数据库实体
   * @return
   * @throws JsonProcessingException
   */
  public FlowNode convertFlowNode() throws JsonProcessingException {
    FlowNode flowNode = new FlowNode();
    flowNode.setName(this.name);
    flowNode.setDesc(this.desc);
    flowNode.setDepList(this.dep);
    flowNode.setType(this.type);
    flowNode.setParameter(this.parameter);
    flowNode.setExtras(this.extras);
    return flowNode;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public List<String> getDep() {
    return dep;
  }

  public void setDep(List<String> dep) {
    this.dep = dep;
  }

  public String getExtras() {
    return extras;
  }

  public void setExtras(String extras) {
    this.extras = extras;
  }
}
