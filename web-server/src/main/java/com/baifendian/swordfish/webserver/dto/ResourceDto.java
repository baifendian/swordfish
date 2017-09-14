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

import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.model.Resource;
import java.util.Date;

/**
 * 资源返回DTO.
 */
public class ResourceDto {
  private String name;
  private String desc;
  private Date createTime;
  private Date modifyTime;
  private String projectName;
  private String owner;
  private String suffix;
  private String originFilename;

  public ResourceDto() {
  }

  public ResourceDto(Resource resource) {
    if (resource != null){
      this.name = resource.getName();
      this.desc = resource.getDesc();
      this.createTime = resource.getCreateTime();
      this.modifyTime = resource.getModifyTime();
      this.projectName = resource.getProjectName();
      this.owner = resource.getOwner();
      this.suffix = CommonUtil.fileSuffix(resource.getOriginFilename());
      this.originFilename = resource.getOriginFilename();
    }

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

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public String getOriginFilename() {
    return originFilename;
  }

  public void setOriginFilename(String originFilename) {
    this.originFilename = originFilename;
  }
}
