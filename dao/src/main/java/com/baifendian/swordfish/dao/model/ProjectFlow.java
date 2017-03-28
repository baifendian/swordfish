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

import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.utils.json.StringNodeJsonDeserializer;
import com.baifendian.swordfish.dao.utils.json.StringNodeJsonSerializer;
import com.baifendian.swordfish.dao.model.flow.params.Property;
import com.baifendian.swordfish.dao.enums.FlowType;
import com.baifendian.swordfish.dao.enums.ScheduleStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectFlow {

  private int id;

  @JsonIgnore
  private List<FlowNode> flowsNodes;

  @JsonIgnore
  private List<FlowNodeRelation> flowsNodesRelation;

  private String name;

  private Date createTime;

  private Date modifyTime;

  private int lastModifyBy;

  private String lastModifyByName;

  private int ownerId;

  private String ownerName;

  private String proxyUser;

  private int projectId;

  private String projectName;

  private String extras;

//  private String

  @JsonDeserialize(using = StringNodeJsonDeserializer.class)
  @JsonSerialize(using = StringNodeJsonSerializer.class)
  private String userDefinedParams;

  @JsonIgnore
  private Map<String, String> userDefinedParamMap;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<FlowNode> getFlowsNodes() {
    return flowsNodes;
  }

  public void setFlowsNodes(List<FlowNode> flowsNodes) {
    this.flowsNodes = flowsNodes;
  }

  public List<FlowNodeRelation> getFlowsNodesRelation() {
    return flowsNodesRelation;
  }

  public void setFlowsNodesRelation(List<FlowNodeRelation> flowsNodesRelation) {
    this.flowsNodesRelation = flowsNodesRelation;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getLastModifyBy() {
    return lastModifyBy;
  }

  public String getLastModifyByName() {
    return lastModifyByName;
  }

  public void setLastModifyByName(String lastModifyByName) {
    this.lastModifyByName = lastModifyByName;
  }

  public int getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
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

  public void setLastModifyBy(int lastModifyBy) {
    this.lastModifyBy = lastModifyBy;
  }

  public String getUserDefinedParams() {
    return userDefinedParams;
  }

  public void setUserDefinedParams(String userDefinedParams) {
    this.userDefinedParams = userDefinedParams;
  }

  public Map<String, String> getUserDefinedParamMap() {
    List<Property> propList;
    if (userDefinedParamMap == null && StringUtils.isNotEmpty(userDefinedParams)) {
      propList = JsonUtil.parseObjectList(userDefinedParams, Property.class);
      userDefinedParamMap = propList.stream().collect(Collectors.toMap(Property::getProp, Property::getValue));
    }
    return userDefinedParamMap;
  }

  public void setUserDefinedParamMap(Map<String, String> userDefinedParamMap) {
    this.userDefinedParamMap = userDefinedParamMap;
  }
}
