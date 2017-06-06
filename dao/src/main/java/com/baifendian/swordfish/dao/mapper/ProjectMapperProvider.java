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

public class ProjectMapperProvider {

  private static final String TABLE_NAME = "project";

  /**
   * 插入项目信息
   *
   * @param parameter
   * @return
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {{
      INSERT_INTO(TABLE_NAME);

      VALUES("`name`", "#{newProject.name,jdbcType=VARCHAR}");
      VALUES("`desc`", "#{newProject.desc}");
      VALUES("`create_time`", "#{newProject.createTime}");
      VALUES("`modify_time`", "#{newProject.modifyTime}");
      VALUES("`owner`", "#{newProject.ownerId}");
    }}.toString();
  }

  /**
   * 更新项目信息
   *
   * @param parameter
   * @return
   */
  public String updateById(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_NAME);

      SET("`desc`=#{project.desc}");
      SET("`modify_time`=#{project.modifyTime}");

      WHERE("`id` = #{project.id}");
    }}.toString();
  }

  /**
   * 删除项目信息
   *
   * @param parameter
   * @return
   */
  public String deleteById(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM(TABLE_NAME);

      WHERE("`id` = #{id}");
    }}.toString();
  }

  /**
   * 查询所有的项目列表
   *
   * @param parameter
   * @return
   */
  public String queryAllProject(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("p.owner as owner_id");
      SELECT("u.name as owner");
      SELECT("p.*");

      FROM(TABLE_NAME + " p");

      LEFT_OUTER_JOIN("user u on p.owner = u.id");
    }}.toString();
  }

  /**
   * 根据用户 id 进行查询
   *
   * @param parameter
   * @return
   */
  public String queryProjectByUser(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("p.owner as owner_id");
        SELECT("u.name as owner");
        SELECT("p.*");

        FROM(TABLE_NAME + " p");

        LEFT_OUTER_JOIN("project_user p_u on p.id = p_u.project_id");
        JOIN("user u on p.owner = u.id");

        WHERE("p_u.user_id = #{userId} or p.owner = #{userId}");
      }
    }.toString();
  }

  /**
   * 根据用户名称查询
   *
   * @param parameter
   * @return
   */
  public String queryByName(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("p.owner as owner_id");
      SELECT("u.name as owner");
      SELECT("p.*");

      FROM(TABLE_NAME + " p");

      JOIN("user u on p.owner = u.id");

      WHERE("p.name = #{name}");
    }}.toString();
  }
}
