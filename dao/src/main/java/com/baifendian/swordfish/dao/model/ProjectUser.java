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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * 项目用户关系实体
 */
public class ProjectUser {
  /**
   * 项目ID
   */
  @JsonIgnore
  private int projectId;

  /**
   * 项目名
   */
  private String projectName;

  /**
   * 用户ID
   */
  @JsonIgnore
  private int userId;

  /**
   * 用户名
   */
  private String userName;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 创建时间
   */
  private Date modifyTime;

  /**
   * 用户在 project 中的权限
   */
  private int perm;

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public int getUserId() {
    return userId;
  }

  public Date getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Date modifyTime) {
    this.modifyTime = modifyTime;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getPerm() {
    return perm;
  }

  public void setPerm(int perm) {
    this.perm = perm;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }
}
