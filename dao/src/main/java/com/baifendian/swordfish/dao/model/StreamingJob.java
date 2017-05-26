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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * 描述一个流任务的结构
 */
public class StreamingJob {

  /**
   * 流任务 id
   **/
  private int id;

  /**
   * 流任务
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
   * DTO 需要字段
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
   * DTO 需要字段
   */
  private String owner;

  /**
   * 结点类型
   * 数据库映射字段/DTO需要字段
   */
  private String type;

  /**
   * 结点参数
   * 数据库映射字段/DTO需要字段
   */
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  private String parameter;

  /**
   * 用户定义参数
   * 数据库映射字段
   */
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  private String userDefinedParams;

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

  public String getUserDefinedParams() {
    return userDefinedParams;
  }

  public void setUserDefinedParams(String userDefinedParams) {
    this.userDefinedParams = userDefinedParams;
  }
}
