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

import com.baifendian.swordfish.dao.model.User;

import java.util.Date;
import java.util.List;

/**
 * 用于User返回的DTO
 */
public class UserDto {
  private String name;
  private String email;
  private String desc;
  private String phone;
  private List<String> proxyUsers;
  private Date createTime;
  private Date modifyTime;

  public UserDto() {
  }

  public UserDto(User user){
    this.name = user.getName();
    this.email = user.getEmail();
    this.desc = user.getDesc();
    this.phone = user.getPhone();
    this.proxyUsers = user.getProxyUserList();
    this.createTime = user.getCreateTime();
    this.modifyTime = user.getModifyTime();
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

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public List<String> getProxyUsers() {
    return proxyUsers;
  }

  public void setProxyUsers(List<String> proxyUsers) {
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
}
