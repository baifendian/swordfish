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
 * 函数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月25日
 */
public class Function {
    /** 函数 id */
    private int id;

    /** 函数名称 */
    private String name;

    /** 函数类型 */
    private FunctionType type;

    /** 项目 id */
    private int projectId;

    /** owner 的 id */
    private int ownerId;

    /** 资源所有者 */
    private String ownerName;

    /** 描述信息 */
    private String desc;

    /** 创建时间 */
    private Timestamp createTime;

    /** 修改时间 */
    private Timestamp modifyTime;

    /** 类名 */
    private String className;

    /** 函数命令 */
    private String command;

    /** 函数用途 */
    private String feature;

    /** 资源列表 */
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
     * @see Function#id
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * setter method
     * 
     * @see Function#id
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter method
     * 
     * @see Function#name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * setter method
     * 
     * @see Function#name
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getter method
     * 
     * @see Function#type
     * @return the type
     */
    public FunctionType getType() {
        return type;
    }

    /**
     * setter method
     * 
     * @see Function#type
     * @param type
     *            the type to set
     */
    public void setType(FunctionType type) {
        this.type = type;
    }

    /**
     * getter method
     * 
     * @see Function#projectId
     * @return the projectId
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * setter method
     * 
     * @see Function#projectId
     * @param projectId
     *            the projectId to set
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * getter method
     * 
     * @see Function#ownerId
     * @return the ownerId
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * setter method
     * 
     * @see Function#ownerId
     * @param ownerId
     *            the ownerId to set
     */
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * getter method
     * 
     * @see Function#ownerName
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * setter method
     * 
     * @see Function#ownerName
     * @param ownerName
     *            the ownerName to set
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * getter method
     * 
     * @see Function#desc
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * setter method
     * 
     * @see Function#desc
     * @param desc
     *            the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * getter method
     * 
     * @see Function#createTime
     * @return the createTime
     */
    public Timestamp getCreateTime() {
        return createTime;
    }

    /**
     * setter method
     * 
     * @see Function#createTime
     * @param createTime
     *            the createTime to set
     */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    /**
     * getter method
     * 
     * @see Function#modifyTime
     * @return the modifyTime
     */
    public Timestamp getModifyTime() {
        return modifyTime;
    }

    /**
     * setter method
     * 
     * @see Function#modifyTime
     * @param modifyTime
     *            the modifyTime to set
     */
    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * getter method
     * 
     * @see Function#className
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * setter method
     * 
     * @see Function#className
     * @param className
     *            the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * getter method
     * 
     * @see Function#command
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * setter method
     * 
     * @see Function#command
     * @param command
     *            the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * getter method
     * 
     * @see Function#feature
     * @return the feature
     */
    public String getFeature() {
        return feature;
    }

    /**
     * setter method
     * 
     * @see Function#feature
     * @param feature
     *            the feature to set
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
        if(resourceList == null){
            resourceList = Arrays.asList(resources.split(","));
        }
        return resourceList;
    }

}
