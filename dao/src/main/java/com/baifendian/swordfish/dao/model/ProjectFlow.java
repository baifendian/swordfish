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

import com.baifendian.swordfish.dao.mapper.utils.EqualUtils;
import com.baifendian.swordfish.dao.model.flow.Property;
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.baifendian.swordfish.dao.utils.json.JsonObjectSerializer;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectFlow {

  /**
   * 工作流 id
   * 数据库映射字段
   **/
  private int id;

  /**
   * 工作流名称
   * 数据库映射字段/DTO也需要使用
   */
  private String name;

  /**
   * 项目 id
   * 数据库映射字段
   */
  private int projectId;

  /**
   * 项目名称
   * DTO需要字段
   */
  private String projectName;

  /**
   * 工作流描述
   * 数据库映射字段/DTO需要字段
   */
  private String desc;

  /**
   * 创建时间
   * 数据库映射字段/DTO需要字段
   */
  private Date createTime;

  /**
   * 修改时间
   * 数据库映射字段/DTO需要字段
   */
  private Date modifyTime;

  /**
   * owner id
   * 数据库映射字段
   */
  private int ownerId;

  /**
   * owner 名称
   * DTO需要字段
   */
  private String owner;

  /**
   * 代理用户
   * 数据库映射字段/DTO需要字段
   */
  private String proxyUser;

  /**
   * 用户定义参数
   * 数据库映射字段
   */
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  private String userDefinedParams;

  /**
   * 用户定义参数
   * 已经反序列化后的结果，用于DTO显示
   */
  private List<Property> userDefinedParamList;

  /**
   * 用户定义参数的 map 结构,
   */
  private Map<String, String> userDefinedParamMap;

  /**
   * 额外字段
   * 数据库映射字段/DTO需要字段
   */
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  private String extras;

  /**
   * 队列信息
   * 数据库映射字段/DTO需要字段
   */
  private String queue;

  /**
   * 结点信息, 数据库中数据解析出来的
   */
  private List<FlowNode> flowsNodes;

  /**
   * 是否是临时节点
   * 数据库映射字段
   */
  private Integer flag;

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
    this.extras = extras;
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  public String getUserDefinedParams() {
    return userDefinedParams;
  }

  public void setUserDefinedParams(String userDefinedParams) {
    this.userDefinedParamList = JsonUtil.parseObjectList(userDefinedParams,Property.class);
    this.userDefinedParams = userDefinedParams;
  }

  public List<Property> getUserDefinedParamList() {
    return userDefinedParamList;
  }

  public void setUserDefinedParamList(List<Property> userDefinedParamList) {
    this.userDefinedParams = JsonUtil.toJsonString(userDefinedParamList);
    this.userDefinedParamList = userDefinedParamList;
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

  public Integer getFlag() {
    return flag;
  }

  public void setFlag(Integer flag) {
    this.flag = flag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProjectFlow that = (ProjectFlow) o;
    return id == that.id &&
            projectId == that.projectId &&
            ownerId == that.ownerId &&
            Objects.equals(name, that.name) &&
            Objects.equals(projectName, that.projectName) &&
            Objects.equals(desc, that.desc) &&
            //Objects.equals(createTime, that.createTime) &&
            //Objects.equals(modifyTime, that.modifyTime) &&
            Objects.equals(owner, that.owner) &&
            Objects.equals(proxyUser, that.proxyUser) &&
            Objects.equals(userDefinedParams, that.userDefinedParams) &&
            Objects.equals(extras, that.extras) &&
            Objects.equals(queue, that.queue) &&
            EqualUtils.equalLists(flowsNodes, that.flowsNodes);
  }
}
