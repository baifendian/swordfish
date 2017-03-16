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

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * @author : liujin
 * @date : 2017-03-10 16:01
 */
public class MasterServerMapperSQL {
  public String query() {
    return new SQL() {{
      SELECT("*");
      FROM("master_server");
    }}.toString();
  }

  public String insert(Map<String, Object> parameter) {
    return new SQL() {{
      INSERT_INTO("master_server");
      VALUES("host", "#{masterServer.host}");
      VALUES("port", "#{masterServer.port}");
      VALUES("update_time", "#{masterServer.updateTime}");
    }}.toString();
  }

  public String update(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE("master_server");
      SET("update_time=#{masterServer.updateTime}");
      WHERE("host=#{masterServer.host}");
      WHERE("port=#{masterServer.port}");
    }}.toString();
  }

  public String delete() {
    return new SQL() {{
      DELETE_FROM("master_server");
    }}.toString();
  }

}
