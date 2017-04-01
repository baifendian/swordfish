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

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectFlow {

  private int id;

  @JsonIgnore
  private List<FlowNode> flowsNodes;

  private String name;

  private String desc;

  private Date createTime;

  private Date modifyTime;

  private int lastModifyBy;

  private String lastModifyByName;

  private int ownerId;

  private String owner;

  private String proxyUser;

  private int projectId;

  private String projectName;

  @JsonIgnore
  private String extras;

  private String queue;

  private ProjectFlowData data = new ProjectFlowData();


  @JsonDeserialize(using = StringNodeJsonDeserializer.class)
  @JsonSerialize(using = StringNodeJsonSerializer.class)
  @JsonIgnore
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
    this.data.setNodes(flowsNodes);
    this.flowsNodes = flowsNodes;
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

  public int getLastModifyBy() {
    return lastModifyBy;
  }

  public void setLastModifyBy(int lastModifyBy) {
    this.lastModifyBy = lastModifyBy;
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

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
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

  public String getExtras() {
    return extras;
  }

  public void setExtras(String extras) {
    this.data.setExtras(extras);
    this.extras = extras;
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  public ProjectFlowData getData() {
    return data;
  }

  public void setData(ProjectFlowData data) {
    this.data = data;
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

  public class ProjectFlowData {
    private List<FlowNode> nodes;

    private String userDefParams;

    private String extras;

    public ProjectFlowData() {
    }

    public List<FlowNode> getNodes() {
      return nodes;
    }

    public void setNodes(List<FlowNode> nodes) {
      this.nodes = nodes;
    }

    public String getUserDefParams() {
      return userDefParams;
    }

    public void setUserDefParams(String userDefParams) {
      this.userDefParams = userDefParams;
    }

    public String getExtras() {
      return extras;
    }

    public void setExtras(String extras) {
      this.extras = extras;
    }
  }
}
