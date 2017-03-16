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

import com.baifendian.swordfish.dao.model.DataSource;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * @auth: ronghua.yu
 * @time: 16/8/9
 * @desc:
 */
public class DataSourceMapperProvider {
  public static final String DB_NAME = "`data_source`";

  public String insert(Map<String, Object> parameter) {
    DataSource dataSource = (DataSource) parameter.get("dataSource");
    int type = dataSource.getType().getType();

    return new SQL() {{
      INSERT_INTO(DB_NAME);
      VALUES("owner_id", "#{dataSource.ownerId}");
      VALUES("project_id", "#{dataSource.projectId}");
      VALUES("name", "#{dataSource.name}");
      VALUES("`desc`", "#{dataSource.desc}");
      VALUES("type", "" + type);
      //VALUES("db_id", "#{dataSource.dbId}");
      VALUES("params", "#{dataSource.params}");
      VALUES("create_time", "#{dataSource.createTime}");
      VALUES("modify_time", "#{dataSource.modifyTime}");
    }}.toString();
  }

  public String update(Map<String, Object> parameter) {
    DataSource dataSource = (DataSource) parameter.get("dataSource");
    int type = dataSource.getType().getType();

    return new SQL() {{
      UPDATE(DB_NAME);
      SET("owner_id = #{dataSource.ownerId}");
      SET("project_id = #{dataSource.projectId}");
      SET("name = #{dataSource.name}");
      SET("`desc` = #{dataSource.desc}");
      SET("type = " + type);
      //SET("db_id = #{dataSource.dbId}");
      SET("params = #{dataSource.params}");
      SET("modify_time = #{dataSource.modifyTime}");
      WHERE("id = #{dataSource.id}");
    }}.toString();
  }

  public String getById(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      FROM(DB_NAME);
      WHERE("id = #{id}");
    }}.toString();
  }

  public String deleteFromId(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM(DB_NAME);
      WHERE("id = #{sourceId}");
    }}.toString();
  }

  public String getByName(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      FROM(DB_NAME);
      WHERE("project_id = #{projectId} and name = #{name}");
    }}.toString();
  }

  public String getByProjectId(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("r.*");
      SELECT("u.name as owner_name");
      FROM("data_source r");
      LEFT_OUTER_JOIN("user as u on u.id = r.owner_id");
      WHERE("r.project_id = #{projectId}");
    }}.toString();
  }

  public String getByProjectIdAndType(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("r.*");
      SELECT("u.name as owner_name");
      FROM("data_source r");
      LEFT_OUTER_JOIN("user as u on u.id = r.owner_id");
      WHERE("r.project_id = #{projectId} and r.type = #{type}");
    }}.toString();
  }

  public String deleteByProjectId(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM(DB_NAME);
      WHERE("project_id = #{projectId}");
    }}.toString();
  }
}
