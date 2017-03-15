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

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.dao.mysql.enums.UserRoleType;
import com.baifendian.swordfish.dao.mysql.enums.UserStatusType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang.ObjectUtils;

import java.util.Date;


/**
 * Created by caojingwei on 16/7/18.
 */
public class User {
    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户创建时间
     */
    @JsonFormat(pattern = Constants.BASE_DATETIME_FORMAT)
    private Date createTime;

    /**
     * 用户修改时间
     */
    @JsonFormat(pattern = Constants.BASE_DATETIME_FORMAT)
    private Date modifyTime;

    private Integer tenantId;

    private String tenantName;

    @JsonFormat(pattern = Constants.BASE_DATETIME_FORMAT)
    private Date joinTime;

    private UserRoleType roleType;

    /**
     * 用户账号状态
     */
    private UserStatusType status;

    public User() {

    }

    public User(UserBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.password = builder.password;
        this.email = builder.email;
        this.phone = builder.phone;
        this.createTime = builder.createTime;
        this.modifyTime = builder.modifyTime;
        this.tenantId = builder.tenantId;
        this.joinTime = builder.joinTime;
        this.roleType = builder.roleType;
        this.status = builder.status;
    }

    public User(Integer id, String name, String password, String email, String phone, Date createTime, Date modifyTime
            , Integer tenantId, String tenantName,
                Date joinTime, UserRoleType roleType, UserStatusType status) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.joinTime = joinTime;
        this.roleType = roleType;
        this.status = status;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", password=" + password + ", email=" + email + ", phone=" + phone + ", createTime=" + createTime + ", modifyTime="
               + modifyTime + ", tenantId=" + tenantId + ", tenantName=" + tenantName + ", joinTime=" + joinTime + ", roleType=" + roleType + ", status=" + status
               + "]";
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public UserRoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(UserRoleType roleType) {
        this.roleType = roleType;
    }

    public UserStatusType getStatus() {
        return status;
    }

    public void setStatus(UserStatusType status) {
        this.status = status;
    }

    public static class UserBuilder {
        private Integer id;

        private String name;

        private String password;

        private String email;

        private String phone;

        private Date createTime;

        private Date modifyTime;

        private Integer tenantId;

        private Date joinTime;

        private UserRoleType roleType;

        private UserStatusType status;

        public UserBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public UserBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder setCreateTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public UserBuilder setModifyTime(Date modifyTime) {
            this.modifyTime = modifyTime;
            return this;
        }

        public void setTenantId(Integer tenantId) {
            this.tenantId = tenantId;
        }

        public void setJoinTime(Date joinTime) {
            this.joinTime = joinTime;
        }

        public void setRoleType(UserRoleType roleType) {
            this.roleType = roleType;
        }

        public UserBuilder setStatus(UserStatusType status) {
            this.status = status;
            return this;
        }
    }

    public boolean equals(User user){
        if (ObjectUtils.notEqual(this.id,user.getId())){
            return false;
        }
        if (ObjectUtils.notEqual(this.name,user.getName())){
            return false;
        }
        if (ObjectUtils.notEqual(this.password,user.getPassword())){
            return false;
        }
        if (ObjectUtils.notEqual(this.email,user.getEmail())){
            return false;
        }
        if (ObjectUtils.notEqual(this.phone,user.getPhone())){
            return false;
        }
        if (ObjectUtils.notEqual(this.tenantId,user.tenantId)){
            return false;
        }
        if (ObjectUtils.notEqual(this.status,user.getStatus())){
            return false;
        }
        if (this.roleType != user.getRoleType()){
            return false;
        }
        return true;
    }

    public boolean isSupperUser(){
        return roleType.equals(UserRoleType.SUPER_USER);
    }
}
