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

/**
 * 项目用户关系实体
 */
public class ProjectUser {
  private int projectId;
  private int userId;
  private Date createTime;
  private int perm;

  public ProjectUser() {
  }

  public ProjectUser(int projectId, int userId, Date createTime, int perm) {
    this.projectId = projectId;
    this.userId = userId;
    this.createTime = createTime;
    this.perm = perm;
  }

  public ProjectUser(int projectId, int userId, Date createTime) {
    this.projectId = projectId;
    this.userId = userId;
    this.createTime = createTime;
  }

  public ProjectUser(int projectId, int userId) {
    this.projectId = projectId;
    this.userId = userId;
    this.createTime = new Date();
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
