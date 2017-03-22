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

import com.baifendian.swordfish.dao.utils.BFDDateUtils;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;

/**
 * 项目实体
 */
public class Project {
  private Integer id;
  private String name;
  private String desc;
  private Date createTime;
  private Date modifyTime;
  private Integer ownerId;
  private String ownerName;

  public Project(Integer id, String name, String desc, Date createTime, Date modifyTime, Integer ownerId, String ownerName) {
    this.id = id;
    this.name = name;
    this.desc = desc;
    this.createTime = createTime;
    this.modifyTime = modifyTime;
    this.ownerId = ownerId;
    this.ownerName = ownerName;
  }

  public Project() {
  }

  public Project(ProjectBuilder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.desc = builder.desc;
    this.createTime = builder.createTime;
    this.modifyTime = builder.modifyTime;
    this.ownerId = builder.ownerId;
    this.ownerName = builder.ownerName;
  }

  static public class ProjectBuilder {
    private Integer id;
    private String name;
    private String desc;
    private Date createTime;
    private Date modifyTime;
    private Integer ownerId;
    private String ownerName;

    public Project build() {
      return new Project(this);
    }

    public ProjectBuilder id(int id) {
      this.id = id;
      return this;
    }

    public ProjectBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ProjectBuilder desc(String desc) {
      this.desc = desc;
      return this;
    }

    public ProjectBuilder createTime(Date createTime) {
      this.createTime = createTime;
      return this;
    }

    public ProjectBuilder createTime() {
      this.createTime = new Date();
      return this;
    }

    public ProjectBuilder modifyTime(Date modifyTime) {
      this.modifyTime = modifyTime;
      return this;
    }

    public ProjectBuilder ownerId(int ownerId) {
      this.ownerId = ownerId;
      return this;
    }

    public ProjectBuilder ownerName(String ownerName) {
      this.ownerName = ownerName;
      return this;
    }
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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public Integer getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Integer ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Project))
      return false;
    Project project = (Project) o;
    if (ObjectUtils.notEqual(this.id, project.getId())) {
      return false;
    }
    if (ObjectUtils.notEqual(this.name, project.getName())) {
      return false;
    }
    if (ObjectUtils.notEqual(this.desc, project.getDesc())) {
      return false;
    }
    if (ObjectUtils.notEqual(this.ownerId, project.getOwnerId())) {
      return false;
    }
    return true;
  }
}
