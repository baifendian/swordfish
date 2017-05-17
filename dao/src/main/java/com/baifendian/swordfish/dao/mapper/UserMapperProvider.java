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
package com.baifendian.swordfish.dao.mapper;

import com.baifendian.swordfish.dao.enums.UserRoleType;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.model.User;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class UserMapperProvider {

  private static final String TABLE_NAME = "user";

  /**
   * @param parameter
   * @return
   */
  public String queryByName(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");

        FROM(TABLE_NAME);

        WHERE("name = #{name}");
      }
    }.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String queryByEmail(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");

        FROM(TABLE_NAME);

        WHERE("`email` = #{email}");
      }
    }.toString();
  }

  /**
   * 校验用户信息
   *
   * @param parameter
   * @return
   */
  public String queryForCheck(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");

        FROM(TABLE_NAME);

        if (parameter.get("name") != null) {
          WHERE("`name` = #{name}");
        }
        if (parameter.get("email") != null) {
          WHERE("`email` = #{email}");
        }

        WHERE("`password` = #{password}");
      }
    }.toString();
  }

  /**
   * 根据用户 id 查询 sql
   *
   * @param parameter
   * @return
   */
  public String queryById(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");

        FROM(TABLE_NAME);

        WHERE("`id` = #{userId}");
      }
    }.toString();
  }

  /**
   * 插入用户信息
   *
   * @param parameter
   * @return
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);

        VALUES("`name`", "#{user.name}");
        VALUES("`email`", "#{user.email}");
        VALUES("`desc`", "#{user.desc}");
        VALUES("`phone`", "#{user.phone}");
        VALUES("`password`", "#{user.password}");
        VALUES("`role`", EnumFieldUtil.genFieldStr("user.role", UserRoleType.class));
        VALUES("`proxy_users`", "#{user.proxyUsers}");
        VALUES("`create_time`", "#{user.createTime}");
        VALUES("`modify_time`", "#{user.modifyTime}");
      }
    }.toString();
  }

  /**
   * 更新用户信息
   *
   * @param parameter
   * @return
   */
  public String update(Map<String, Object> parameter) {
    return new SQL() {
      {
        UPDATE(TABLE_NAME);

        SET("`email`=#{user.email}");
        SET("`desc`=#{user.desc}");
        SET("`phone`=#{user.phone}");
        SET("`password`=#{user.password}");
//        if (user.getRole() != null) {
//          SET("`role`=" + EnumFieldUtil.genFieldStr("user.role", UserRoleType.class));
//        }
        SET("`proxy_users`=#{user.proxyUsers}");
        SET("`modify_time`=#{user.modifyTime}");

        WHERE("`name`=#{user.name}");
      }
    }.toString();
  }

  /**
   * 删除用户
   *
   * @param name
   * @return
   */
  public String delete(String name) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);

        WHERE("`name`=#{name}");
      }
    }.toString();
  }

  /**
   * 查询所有用户信息
   *
   * @return
   */
  public String queryAllUsers() {
    return new SQL() {
      {
        SELECT("*");

        FROM(TABLE_NAME);
      }
    }.toString();
  }
}
