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

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class SessionMapperProvider {

  /**
   * @param parameter
   * @return
   */
  public String queryById(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      FROM("session");
      WHERE("id = #{sessionId}");
    }}.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String queryByUserIdAndIp(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      FROM("session");
      WHERE("user_id = #{userId}");
      WHERE("ip = #{ip}");
    }}.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {{
      INSERT_INTO("session");
      VALUES("id", "#{session.id}");
      VALUES("user_id", "#{session.userId}");
      VALUES("ip", "#{session.ip}");
      VALUES("last_login_time", "#{session.lastLoginTime}");
    }}.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String deleteById(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM("session");
      WHERE("id = #{sessionId}");
    }}.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String deleteByExpireTime(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM("session");
      WHERE("last_login_time <= #{expireTime}");
    }}.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String update(Map<String, Object> parameter) {
    return new SQL() {
      {
        UPDATE("session");
        SET("last_login_time=#{loginTime}");
        WHERE("id = #{sessionId}");
      }
    }.toString();
  }
}
