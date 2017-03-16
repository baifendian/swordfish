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

import com.baifendian.swordfish.dao.enums.UserRoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * author: smile8 date:   2017/3/16 desc:   用户信息
 */
public class User {
  /**
   * 用户 ID
   */
  @JsonIgnore
  private Integer id;

  /**
   * 用户名
   */
  private String name;

  /**
   * 用户邮箱
   */
  private String email;

  /**
   * 用户描述
   */
  private String desc;

  /**
   * 用户密码
   */
  @JsonIgnore
  private String password;

  /**
   * 用户手机号
   */
  private String phone;

  /**
   * 角色
   */
  @JsonIgnore
  private UserRoleType role;

  /**
   * 代理用户
   */
  private String proxyUsers;

  /**
   * 用户创建时间
   */
  private Date createTime;

  /**
   * 用户修改时间
   */
  private Date modifyTime;

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public UserRoleType getRole() {
    return role;
  }

  public void setRole(UserRoleType role) {
    this.role = role;
  }

  public String getProxyUsers() {
    return proxyUsers;
  }

  public void setProxyUsers(String proxyUsers) {
    this.proxyUsers = proxyUsers;
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

  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", desc='" + desc + '\'' +
            ", password='" + password + '\'' +
            ", phone='" + phone + '\'' +
            ", role=" + role +
            ", proxyUsers='" + proxyUsers + '\'' +
            ", createTime=" + createTime +
            ", modifyTime=" + modifyTime +
            '}';
  }
}
