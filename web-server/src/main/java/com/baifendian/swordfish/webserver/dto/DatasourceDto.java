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

import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

/**
 * 数据源返回DTO
 */
public class DatasourceDto {
  private String name;
  private DbType type;
  private String desc;
  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String parameter;
  private Date createTime;
  private Date modifyTime;
  private String projectName;
  private String owner;

  public DatasourceDto() {
  }

  public DatasourceDto(DataSource dataSource) {
    if (dataSource != null) {
      this.name = dataSource.getName();
      this.type = dataSource.getType();
      this.desc = dataSource.getDesc();
      this.parameter = dataSource.getParameter();
      this.createTime = dataSource.getCreateTime();
      this.modifyTime = dataSource.getModifyTime();
      this.projectName = dataSource.getProjectName();
      this.owner = dataSource.getOwnerName();
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DbType getType() {
    return type;
  }

  public void setType(DbType type) {
    this.type = type;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
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

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }
}
