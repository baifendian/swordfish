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
package com.baifendian.swordfish.dao.model;

import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.baifendian.swordfish.dao.utils.json.JsonObjectSerializer;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.IOException;
import java.util.List;

public class FlowNode {

  /**
   * 结点 id
   */
  @JsonIgnore
  private int id;

  /**
   * 结点名称
   */
  private String name;

  /**
   * 结点描述
   */
  private String desc;

  /**
   * 结点类型
   */
  private String type;

  /**
   * 所属工作流
   */
  @JsonIgnore
  private int flowId;

  /**
   * 参数信息
   */
  //@JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  private String parameter;


  /**
   * 依赖信息
   */
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  private String dep;

  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  private String extras;


  @JsonIgnore
  private List<String> depList;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

  public int getFlowId() {
    return flowId;
  }

  public void setFlowId(int flowId) {
    this.flowId = flowId;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public String getDep() {
    return dep;
  }

  public void setDep(String dep) throws IOException {
    this.dep = dep;
    this.depList = JsonUtil.parseObjectList(dep, String.class);
  }

  public String getExtras() {
    return extras;
  }

  public void setExtras(String extras) {
    this.extras = extras;
  }

  public List<String> getDepList() {
    return depList;
  }

  public void setDepList(List<String> depList) throws JsonProcessingException {
    this.depList = depList;
    this.dep = JsonUtil.toJsonString(depList);
  }

}
