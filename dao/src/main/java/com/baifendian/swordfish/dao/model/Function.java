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

import com.baifendian.swordfish.dao.enums.FunctionType;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * 函数 <p>
 *
 * @author : dsfan
 * @date : 2016年8月25日
 */
public class Function {
  /**
   * 函数 id
   */
  private int id;

  /**
   * 函数名称
   */
  private String name;

  /**
   * 函数类型
   */
  private FunctionType type;

  /**
   * 项目 id
   */
  private int projectId;

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
   * 创建时间
   */
  private Timestamp createTime;

  /**
   * 修改时间
   */
  private Timestamp modifyTime;

  /**
   * 类名
   */
  private String className;

  /**
   * 函数命令
   */
  private String command;

  /**
   * 函数用途
   */
  private String feature;

  /**
   * 资源列表
   */
  private String resources;

  private List<String> resourceList;

  @Override
  public String toString() {
    return "Function [id=" + id + ", name=" + name + ", type=" + type + ", projectId=" + projectId + ", ownerId=" + ownerId + ", ownerName=" + ownerName + ", desc=" + desc
            + ", createTime=" + createTime + ", modifyTime=" + modifyTime + ", className=" + className + ", command=" + command + ", feature=" + feature + ", resources="
            + resources + "]";
  }

  /**
   * getter method
   *
   * @return the id
   * @see Function#id
   */
  public int getId() {
    return id;
  }

  /**
   * setter method
   *
   * @param id the id to set
   * @see Function#id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * getter method
   *
   * @return the name
   * @see Function#name
   */
  public String getName() {
    return name;
  }

  /**
   * setter method
   *
   * @param name the name to set
   * @see Function#name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * getter method
   *
   * @return the type
   * @see Function#type
   */
  public FunctionType getType() {
    return type;
  }

  /**
   * setter method
   *
   * @param type the type to set
   * @see Function#type
   */
  public void setType(FunctionType type) {
    this.type = type;
  }

  /**
   * getter method
   *
   * @return the projectId
   * @see Function#projectId
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * setter method
   *
   * @param projectId the projectId to set
   * @see Function#projectId
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  /**
   * getter method
   *
   * @return the ownerId
   * @see Function#ownerId
   */
  public int getOwnerId() {
    return ownerId;
  }

  /**
   * setter method
   *
   * @param ownerId the ownerId to set
   * @see Function#ownerId
   */
  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  /**
   * getter method
   *
   * @return the ownerName
   * @see Function#ownerName
   */
  public String getOwnerName() {
    return ownerName;
  }

  /**
   * setter method
   *
   * @param ownerName the ownerName to set
   * @see Function#ownerName
   */
  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  /**
   * getter method
   *
   * @return the desc
   * @see Function#desc
   */
  public String getDesc() {
    return desc;
  }

  /**
   * setter method
   *
   * @param desc the desc to set
   * @see Function#desc
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }

  /**
   * getter method
   *
   * @return the createTime
   * @see Function#createTime
   */
  public Timestamp getCreateTime() {
    return createTime;
  }

  /**
   * setter method
   *
   * @param createTime the createTime to set
   * @see Function#createTime
   */
  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime;
  }

  /**
   * getter method
   *
   * @return the modifyTime
   * @see Function#modifyTime
   */
  public Timestamp getModifyTime() {
    return modifyTime;
  }

  /**
   * setter method
   *
   * @param modifyTime the modifyTime to set
   * @see Function#modifyTime
   */
  public void setModifyTime(Timestamp modifyTime) {
    this.modifyTime = modifyTime;
  }

  /**
   * getter method
   *
   * @return the className
   * @see Function#className
   */
  public String getClassName() {
    return className;
  }

  /**
   * setter method
   *
   * @param className the className to set
   * @see Function#className
   */
  public void setClassName(String className) {
    this.className = className;
  }

  /**
   * getter method
   *
   * @return the command
   * @see Function#command
   */
  public String getCommand() {
    return command;
  }

  /**
   * setter method
   *
   * @param command the command to set
   * @see Function#command
   */
  public void setCommand(String command) {
    this.command = command;
  }

  /**
   * getter method
   *
   * @return the feature
   * @see Function#feature
   */
  public String getFeature() {
    return feature;
  }

  /**
   * setter method
   *
   * @param feature the feature to set
   * @see Function#feature
   */
  public void setFeature(String feature) {
    this.feature = feature;
  }

  public String getResources() {
    return resources;
  }

  public void setResources(String resources) {
    this.resources = resources;
  }

  public List<String> getResourceList() {
    if (resourceList == null) {
      resourceList = Arrays.asList(resources.split(","));
    }
    return resourceList;
  }

}
