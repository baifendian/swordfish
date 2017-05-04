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

public class MasterServerMapperProvider {

  public static final String TABLE_NAME = "master_server";

  /**
   * 查询 master 的所有信息
   *
   * @return
   */
  public String query() {
    return new SQL() {{
      SELECT("*");

      FROM(TABLE_NAME);
    }}.toString();
  }

  /**
   * 插入到 table 中
   *
   * @param parameter
   * @return
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {{
      INSERT_INTO(TABLE_NAME);

      VALUES("host", "#{masterServer.host}");
      VALUES("port", "#{masterServer.port}");
      VALUES("create_time", "#{masterServer.createTime}");
      VALUES("modify_time", "#{masterServer.modifyTime}");
    }}.toString();
  }

  /**
   * 更新信息
   *
   * @param parameter
   * @return
   */
  public String update(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_NAME);

      SET("modify_time = #{masterServer.modifyTime}");

      WHERE("host = #{masterServer.host}");
      WHERE("port = #{masterServer.port}");
    }}.toString();
  }

  /**
   * 删除记录
   *
   * @return
   */
  public String delete() {
    return new SQL() {{
      DELETE_FROM(TABLE_NAME);
    }}.toString();
  }

  /**
   * 删除指定的 host, port 信息
   *
   * @param parameter
   * @return
   */
  public String deleteByHostPort(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM(TABLE_NAME);

      WHERE("host = #{host}");
      WHERE("port = #{port}");
    }}.toString();
  }
}
