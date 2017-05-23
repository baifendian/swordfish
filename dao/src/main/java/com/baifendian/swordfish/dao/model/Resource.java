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

import java.util.Date;

public class Resource {
  /**
   * 资源id
   */
  private int id;

  /**
   * 资源名称
   */
  private String name;

  /**
   * 资源文件名称
   */
  private String originFilename;

  /**
   * 描述信息
   */
  private String desc;

  /**
   * owner 的 id
   */
  private int ownerId;

  /**
   * 资源所有者
   */
  private String owner;

  /**
   * 项目 id
   */
  private int projectId;

  /**
   * 项目名
   */
  private String projectName;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 修改时间
   */
  private Date modifyTime;

  public Resource() {
  }

  public Resource(int id, String name, String originFilename, String desc, int ownerId, String owner, int projectId, String projectName, Date createTime, Date modifyTime) {
    this.id = id;
    this.name = name;
    this.originFilename = originFilename;
    this.desc = desc;
    this.ownerId = ownerId;
    this.owner = owner;
    this.projectId = projectId;
    this.projectName = projectName;
    this.createTime = createTime;
    this.modifyTime = modifyTime;
  }

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

  public String getOriginFilename() {
    return originFilename;
  }

  public void setOriginFilename(String originFilename) {
    this.originFilename = originFilename;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
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

  @Override
  public String toString() {
    return "Resource{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", originFilename='" + originFilename + '\'' +
        ", desc='" + desc + '\'' +
        ", ownerId=" + ownerId +
        ", owner='" + owner + '\'' +
        ", projectId=" + projectId +
        ", projectName='" + projectName + '\'' +
        ", createTime=" + createTime +
        ", modifyTime=" + modifyTime +
        '}';
  }
}
