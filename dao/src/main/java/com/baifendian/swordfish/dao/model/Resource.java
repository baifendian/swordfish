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

import com.baifendian.swordfish.dao.enums.ResourcePubStatus;
import com.baifendian.swordfish.dao.enums.ResourceType;

import java.sql.Timestamp;

/**
 * 资源实体 <p>
 *
 * @author : dsfan
 * @date : 2016年8月24日
 */
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
   * 资源类型
   */
  private ResourceType type;

  /**
   * 项目 id
   */
  private int projectId;

  /**
   * 项目名
   */
  private String projectName;

  /**
   * 组织 id
   */
  private Integer orgId;

  /**
   * 组织名
   */
  private String orgName;

  /**
   * owner 的 id
   */
  private int ownerId;

  /**
   * 资源所有者
   */
  private String ownerName;

  /**
   * 描述信息
   */
  private String desc;

  /**
   * 最后发布人 id
   */
  private Integer lastPublishBy;

  /**
   * 最后发布人 name
   */
  private String lastPublishByName;

  /**
   * 创建时间
   */
  private Timestamp createTime;

  /**
   * 修改时间
   */
  private Timestamp modifyTime;

  /**
   * 发布时间
   */
  private Timestamp publishTime;

  /**
   * 发布状态 {@link ResourcePubStatus}
   */
  private ResourcePubStatus pubStatus;

  /**
   * getter method
   *
   * @return the id
   * @see Resource#id
   */
  public int getId() {
    return id;
  }

  /**
   * setter method
   *
   * @param id the id to set
   * @see Resource#id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * getter method
   *
   * @return the name
   * @see Resource#name
   */
  public String getName() {
    return name;
  }

  /**
   * setter method
   *
   * @param name the name to set
   * @see Resource#name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * getter method
   *
   * @return the type
   * @see Resource#type
   */
  public ResourceType getType() {
    return type;
  }

  /**
   * setter method
   *
   * @param type the type to set
   * @see Resource#type
   */
  public void setType(ResourceType type) {
    this.type = type;
  }

  /**
   * getter method
   *
   * @return the projectId
   * @see Resource#projectId
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * setter method
   *
   * @param projectId the projectId to set
   * @see Resource#projectId
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  /**
   * getter method
   *
   * @return the projectName
   * @see Resource#projectName
   */
  public String getProjectName() {
    return projectName;
  }

  /**
   * setter method
   *
   * @param projectName the projectName to set
   * @see Resource#projectName
   */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * getter method
   *
   * @return the orgId
   * @see Resource#orgId
   */
  public Integer getOrgId() {
    return orgId;
  }

  /**
   * setter method
   *
   * @param orgId the orgId to set
   * @see Resource#orgId
   */
  public void setOrgId(Integer orgId) {
    this.orgId = orgId;
  }

  /**
   * getter method
   *
   * @return the orgName
   * @see Resource#orgName
   */
  public String getOrgName() {
    return orgName;
  }

  /**
   * setter method
   *
   * @param orgName the orgName to set
   * @see Resource#orgName
   */
  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  /**
   * getter method
   *
   * @return the ownerId
   * @see Resource#ownerId
   */
  public int getOwnerId() {
    return ownerId;
  }

  /**
   * setter method
   *
   * @param ownerId the ownerId to set
   * @see Resource#ownerId
   */
  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  /**
   * getter method
   *
   * @return the ownerName
   * @see Resource#ownerName
   */
  public String getOwnerName() {
    return ownerName;
  }

  /**
   * setter method
   *
   * @param ownerName the ownerName to set
   * @see Resource#ownerName
   */
  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  /**
   * getter method
   *
   * @return the desc
   * @see Resource#desc
   */
  public String getDesc() {
    return desc;
  }

  /**
   * setter method
   *
   * @param desc the desc to set
   * @see Resource#desc
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }

  /**
   * getter method
   *
   * @return the createTime
   * @see Resource#createTime
   */
  public Timestamp getCreateTime() {
    return createTime;
  }

  /**
   * setter method
   *
   * @param createTime the createTime to set
   * @see Resource#createTime
   */
  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime;
  }

  /**
   * getter method
   *
   * @return the modifyTime
   * @see Resource#modifyTime
   */
  public Timestamp getModifyTime() {
    return modifyTime;
  }

  /**
   * setter method
   *
   * @param modifyTime the modifyTime to set
   * @see Resource#modifyTime
   */
  public void setModifyTime(Timestamp modifyTime) {
    this.modifyTime = modifyTime;
  }

  public Timestamp getPublishTime() {
    return publishTime;
  }

  public void setPublishTime(Timestamp publishTime) {
    this.publishTime = publishTime;
  }

  /**
   * getter method
   *
   * @return the pubStatus
   * @see Resource#pubStatus
   */
  public ResourcePubStatus getPubStatus() {
    return pubStatus;
  }

  /**
   * setter method
   *
   * @param pubStatus the pubStatus to set
   * @see Resource#pubStatus
   */
  public void setPubStatus(ResourcePubStatus pubStatus) {
    this.pubStatus = pubStatus;
  }

  /**
   * getter method
   *
   * @return the lastPublishBy
   * @see Resource#lastPublishBy
   */
  public Integer getLastPublishBy() {
    return lastPublishBy;
  }

  /**
   * setter method
   *
   * @param lastPublishBy the lastPublishBy to set
   * @see Resource#lastPublishBy
   */
  public void setLastPublishBy(Integer lastPublishBy) {
    this.lastPublishBy = lastPublishBy;
  }

  /**
   * getter method
   *
   * @return the lastPublishByName
   * @see Resource#lastPublishByName
   */
  public String getLastPublishByName() {
    return lastPublishByName;
  }

  /**
   * setter method
   *
   * @param lastPublishByName the lastPublishByName to set
   * @see Resource#lastPublishByName
   */
  public void setLastPublishByName(String lastPublishByName) {
    this.lastPublishByName = lastPublishByName;
  }

}
