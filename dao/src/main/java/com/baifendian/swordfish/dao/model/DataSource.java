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

import com.baifendian.swordfish.dao.enums.DbType;
import java.util.Date;

public class DataSource {
  /**
   * 数据源 id
   */
  private int id;

  /**
   * owner id
   */
  private int ownerId;

  /**
   * owner 名称
   */
  private String ownerName;

  /**
   * 项目 id
   */
  private int projectId;

  /**
   * 项目名称
   */
  private String projectName;

  /**
   * 数据源名称
   */
  private String name;

  /**
   * 数据源描述
   */
  private String desc;

  /**
   * 数据源类型
   */
  private DbType type;

  /**
   * 参数信息
   */
  private String parameter;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 修改时间
   */
  private Date modifyTime;

  public DataSource() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

  public DbType getType() {
    return type;
  }

  public void setType(DbType type) {
    this.type = type;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
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

  @Override
  public String toString() {
    return "DataSource{" +
        "id=" + id +
        ", ownerId=" + ownerId +
        ", ownerName='" + ownerName + '\'' +
        ", projectId=" + projectId +
        ", name='" + name + '\'' +
        ", desc='" + desc + '\'' +
        ", type=" + type +
        ", parameter='" + parameter + '\'' +
        ", createTime=" + createTime +
        ", modifyTime=" + modifyTime +
        '}';
  }
}
