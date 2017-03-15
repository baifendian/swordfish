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

package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.dao.mysql.enums.ResourcePubStatus;
import com.baifendian.swordfish.dao.mysql.enums.ResourceType;

import java.sql.Timestamp;

/**
 * 资源实体
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月24日
 */
public class Resource {
    /** 资源id */
    private int id;

    /** 资源名称 */
    private String name;

    /** 资源类型 */
    private ResourceType type;

    /** 项目 id */
    private int projectId;

    /** 项目名 */
    private String projectName;

    /** 组织 id */
    private Integer orgId;

    /** 组织名 */
    private String orgName;

    /** owner 的 id */
    private int ownerId;

    /** 资源所有者 */
    private String ownerName;

    /** 描述信息 */
    private String desc;

    /** 最后发布人 id */
    private Integer lastPublishBy;

    /** 最后发布人 name */
    private String lastPublishByName;

    /** 创建时间 */
    private Timestamp createTime;

    /** 修改时间 */
    private Timestamp modifyTime;

    /** 发布时间 */
    private Timestamp publishTime;

    /** 发布状态 {@link ResourcePubStatus} */
    private ResourcePubStatus pubStatus;

    /**
     * getter method
     * 
     * @see Resource#id
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * setter method
     * 
     * @see Resource#id
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter method
     * 
     * @see Resource#name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * setter method
     * 
     * @see Resource#name
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter method
     * 
     * @see Resource#type
     * @return the type
     */
    public ResourceType getType() {
        return type;
    }

    /**
     * setter method
     * 
     * @see Resource#type
     * @param type
     *            the type to set
     */
    public void setType(ResourceType type) {
        this.type = type;
    }

    /**
     * getter method
     * 
     * @see Resource#projectId
     * @return the projectId
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * setter method
     * 
     * @see Resource#projectId
     * @param projectId
     *            the projectId to set
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * getter method
     * 
     * @see Resource#projectName
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * setter method
     * 
     * @see Resource#projectName
     * @param projectName
     *            the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * getter method
     * 
     * @see Resource#orgId
     * @return the orgId
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * setter method
     * 
     * @see Resource#orgId
     * @param orgId
     *            the orgId to set
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * getter method
     * 
     * @see Resource#orgName
     * @return the orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * setter method
     * 
     * @see Resource#orgName
     * @param orgName
     *            the orgName to set
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * getter method
     * 
     * @see Resource#ownerId
     * @return the ownerId
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * setter method
     * 
     * @see Resource#ownerId
     * @param ownerId
     *            the ownerId to set
     */
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * getter method
     * 
     * @see Resource#ownerName
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * setter method
     * 
     * @see Resource#ownerName
     * @param ownerName
     *            the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * getter method
     * 
     * @see Resource#desc
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * setter method
     * 
     * @see Resource#desc
     * @param desc
     *            the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * getter method
     * 
     * @see Resource#createTime
     * @return the createTime
     */
    public Timestamp getCreateTime() {
        return createTime;
    }

    /**
     * setter method
     * 
     * @see Resource#createTime
     * @param createTime
     *            the createTime to set
     */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    /**
     * getter method
     * 
     * @see Resource#modifyTime
     * @return the modifyTime
     */
    public Timestamp getModifyTime() {
        return modifyTime;
    }

    /**
     * setter method
     * 
     * @see Resource#modifyTime
     * @param modifyTime
     *            the modifyTime to set
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
     * @see Resource#pubStatus
     * @return the pubStatus
     */
    public ResourcePubStatus getPubStatus() {
        return pubStatus;
    }

    /**
     * setter method
     * 
     * @see Resource#pubStatus
     * @param pubStatus
     *            the pubStatus to set
     */
    public void setPubStatus(ResourcePubStatus pubStatus) {
        this.pubStatus = pubStatus;
    }

    /**
     * getter method
     * 
     * @see Resource#lastPublishBy
     * @return the lastPublishBy
     */
    public Integer getLastPublishBy() {
        return lastPublishBy;
    }

    /**
     * setter method
     * 
     * @see Resource#lastPublishBy
     * @param lastPublishBy
     *            the lastPublishBy to set
     */
    public void setLastPublishBy(Integer lastPublishBy) {
        this.lastPublishBy = lastPublishBy;
    }

    /**
     * getter method
     * 
     * @see Resource#lastPublishByName
     * @return the lastPublishByName
     */
    public String getLastPublishByName() {
        return lastPublishByName;
    }

    /**
     * setter method
     * 
     * @see Resource#lastPublishByName
     * @param lastPublishByName
     *            the lastPublishByName to set
     */
    public void setLastPublishByName(String lastPublishByName) {
        this.lastPublishByName = lastPublishByName;
    }

}
