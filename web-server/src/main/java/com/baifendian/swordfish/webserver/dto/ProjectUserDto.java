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
package com.baifendian.swordfish.webserver.dto;

import com.baifendian.swordfish.dao.model.ProjectUser;

import java.util.Date;

/**
 * 描述用户在项目中关系的DTO
 */
public class ProjectUserDto {
  private String projectName;
  private String userName;
  private int perm;
  private Date createTime;
  private Date modifyTime;
  private String owner;

  public ProjectUserDto() {
  }

  public ProjectUserDto(ProjectUser projectUser) {
    if(projectUser != null){
      this.projectName = projectUser.getProjectName();
      this.userName = projectUser.getUserName();
      this.perm = projectUser.getPerm();
      this.createTime = projectUser.getCreateTime();
      this.modifyTime = projectUser.getModifyTime();
      this.owner = projectUser.getOwner();
    }
  }

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

  public Date getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Date modifyTime) {
    this.modifyTime = modifyTime;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }
}
