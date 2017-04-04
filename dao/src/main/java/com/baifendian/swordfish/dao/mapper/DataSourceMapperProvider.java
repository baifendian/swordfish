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

public class DataSourceMapperProvider {

  public static final String TABLE_NAME = "`datasource`";

  /**
   * 插入数据源
   *
   * @param parameter
   * @return
   */
  public String insert(Map<String, Object> parameter) {
    DataSource dataSource = (DataSource) parameter.get("dataSource");
    int type = dataSource.getType().getType();

    return new SQL() {{
      INSERT_INTO(TABLE_NAME);

      VALUES("`name`", "#{dataSource.name}");
      VALUES("`desc`", "#{dataSource.desc}");
      VALUES("`type`", "" + type);
      VALUES("`owner`", "#{dataSource.ownerId}");
      VALUES("`project_id`", "#{dataSource.projectId}");
      VALUES("`parameter`", "#{dataSource.parameter}");
      VALUES("`create_time`", "#{dataSource.createTime}");
      VALUES("`modify_time`", "#{dataSource.modifyTime}");
    }}.toString();
  }

  /**
   * 更新数据源
   *
   * @param parameter
   * @return
   */
  public String update(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_NAME);

      SET("`desc` = #{dataSource.desc}");
      SET("`owner` = #{dataSource.ownerId}");
      SET("`project_id` = #{dataSource.projectId}");
      SET("`parameter` = #{dataSource.parameter}");
      SET("`modify_time` = #{dataSource.modifyTime}");

      WHERE("`name` = #{dataSource.name}");
    }}.toString();
  }

  /**
   * 删除数据源
   *
   * @param parameter
   * @return
   */
  public String deleteByProjectAndName(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM(TABLE_NAME);

      WHERE("`project_id` = #{projectId} and name = #{name}");
    }}.toString();
  }

  /**
   * 得到某个项目下的所有数据源
   *
   * @param parameter
   * @return
   */
  public String getByProjectId(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("r.*, r.owner as owner_id");
      SELECT("u.name as owner,p.name as project_name");

      FROM(TABLE_NAME + " r");

      LEFT_OUTER_JOIN("user as u on u.id = r.owner");
      JOIN("project p on r.project_id = p.id");

      WHERE("r.project_id = #{projectId}");
    }}.toString();
  }

  /**
   * 得到某个数据源的信息
   *
   * @param parameter
   * @return
   */
  public String getByName(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("r.*, r.owner as owner_id");
      SELECT("u.name as owner, p.name as project_name");

      FROM(TABLE_NAME + " r");

      JOIN("user as u on u.id = r.owner");
      JOIN("project p on r.project_id = p.id");

      WHERE("r.project_id = #{projectId} and r.name = #{name}");
    }}.toString();
  }

  /**
   * 根据 projectName 和 datasource name 查询
   *
   * @param parameter
   * @return
   */
  public String getByProjectNameAndName(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("r.*, r.owner as owner_id");
      SELECT("u.name as owner, p.name as project_name");

      FROM(TABLE_NAME + " r");

      JOIN("user as u on u.id = r.owner");
      JOIN("project p on r.project_id = p.id");

      WHERE("p.name = #{projectName} and r.name = #{name}");
    }}.toString();
  }
}
