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

import com.baifendian.swordfish.dao.model.flow.params.Property;
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.baifendian.swordfish.dao.utils.json.JsonObjectSerializer;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectFlow {

  /**
   * 工作流 id
   */
  @JsonIgnore
  private int id;

  /**
   * 工作流名称
   */
  private String name;

  /**
   * 项目 id
   */
  @JsonIgnore
  private int projectId;

  /**
   * 项目名称
   */
  private String projectName;

  /**
   * 工作流描述
   */
  private String desc;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 修改时间
   */
  private Date modifyTime;

  /**
   * owner id
   */
  @JsonIgnore
  private int ownerId;

  /**
   * owner 名称
   */
  private String owner;

  /**
   * 代理用户
   */
  private String proxyUser;

  /**
   * 用户定义参数
   */
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  @JsonIgnore
  private String userDefinedParams;

  /**
   * 用户定义参数的 map 结构,
   */
  @JsonIgnore
  private Map<String, String> userDefinedParamMap;

  /**
   * 额外字段
   */
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  @JsonIgnore
  private String extras;

  /**
   * 队列信息
   */
  private String queue;

  /**
   * 结点信息, 数据库中数据解析出来的
   */
  @JsonIgnore
  private List<FlowNode> flowsNodes;

  /**
   * 该数据结构其实是 db 中没有的, 用于构建的, 需要返回的
   */
  private ProjectFlowData data = new ProjectFlowData();

  public ProjectFlow() {
  }

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

  public static class ProjectFlowData {
    /**
     * 结点信息
     */
    private List<FlowNode> nodes;

    /**
     * 用户自定义参数
     */
    private List<Property> userDefParams;

    /**
     * 额外信息
     */
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    @JsonSerialize(using = JsonObjectSerializer.class)
    private String extras;

    public List<FlowNode> getNodes() {
      return nodes;
    }

    public void setNodes(List<FlowNode> nodes) {
      this.nodes = nodes;
    }

    public List<Property> getUserDefParams() {
      return userDefParams;
    }

    public void setUserDefParams(List<Property> userDefParams) {
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
