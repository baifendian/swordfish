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
package com.baifendian.swordfish.dao.enums;

/**
 * author: smile8 date:   2017/3/16 desc:   用户角色信息
 */
public enum UserRoleType {
  ADMIN_USER(0),
  GENERAL_USER(1);

  private int role;

  UserRoleType(int val) {
    this.role = val;
  }

  public Integer getType() {
    return role;
  }

  public static UserRoleType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }

    for (UserRoleType roleType : UserRoleType.values()) {
      if (roleType.getType().equals(type)) {
        return roleType;
      }
    }

    throw new IllegalArgumentException("Cannot convert " + type + " to " + UserRoleType.class.getSimpleName() + " .");
  }
}
