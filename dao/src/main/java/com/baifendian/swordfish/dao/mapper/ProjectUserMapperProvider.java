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

public class ProjectUserMapperProvider {

  private static final String TABLE_NAME = "project_user";

  /**
   * 查询项目的用户信息
   *
   * @param parameter
   * @return
   */
  public String query(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      SELECT("u.name as user_name, p.name as project_name");

      FROM(TABLE_NAME + " p_u");

      JOIN("user u on p_u.user_id = u.id");
      JOIN("project p on p_u.project_id = p.id");

      WHERE("`project_id` = #{projectId}");
      WHERE("`user_id` = #{userId}");
    }}.toString();
  }

  /**
   * 插入新的记录
   *
   * @param parameter
   * @return
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {{
      INSERT_INTO(TABLE_NAME);

      VALUES("`project_id`", "#{projectUser.projectId}");
      VALUES("`user_id`", "#{projectUser.userId}");
      VALUES("`perm`", "#{projectUser.perm}");
      VALUES("`create_time`", "#{projectUser.createTime}");
      VALUES("`modify_time`", "#{projectUser.modifyTime}");
    }}.toString();
  }

  /**
   * 查询一个项目下的所有用户
   *
   * @param parameter
   * @return
   */
  public String queryByProject(Map<String, Object> parameter) {
    String sql = new SQL() {{
      SELECT("p_u.*");
      SELECT("u.name as user_name, p.name as project_name, p.owner as owner_id");

      FROM(TABLE_NAME + " p_u");

      JOIN("user u on p_u.user_id = u.id");
      JOIN("project p on p_u.project_id = p.id");

      WHERE("p_u.project_id = #{projectId}");
    }}.toString();

    return new SQL() {{
      SELECT("t.*");
      SELECT("u.name as owner_name");

      FROM("("+sql+") t");

      JOIN("user u on t.owner_id = u.id");
    }}.toString();
  }

  /**
   * 更新用户信息
   *
   * @param parameter
   * @return
   */
  public String update(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_NAME);

      SET("`perm`=#{projectUser.perm}");
      SET("`modify_time`=#{projectUser.modifyTime}");

      WHERE("`project_id` = #{projectUser.projectId}");
      WHERE("`user_id` = #{projectUser.userId}");
    }}.toString();
  }

  /**
   * 删除一个项目下的用户
   *
   * @param parameter
   * @return
   */
  public String delete(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM(TABLE_NAME);

      WHERE("`project_id` = #{projectId}");
      WHERE("`user_id` = #{userId}");
    }}.toString();
  }

  /**
   * 查询项目下的所有用户
   *
   * @param parameter
   * @return
   */
  public String queryForUser(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("u.*");

      FROM(TABLE_NAME + " p_u");

      JOIN("user u on p_u.user_id = u.id");

      WHERE("p_u.project_id = #{projectId}");
    }}.toString();
  }

  /**
   * 根据用户名称进行查询
   *
   * @param parameter
   * @return
   */
  public String queryByUser(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      SELECT("u.name as user_name, p.name as project_name");

      FROM(TABLE_NAME + " p_u");

      JOIN("user u on p_u.user_id = u.id");
      JOIN("project p on p_u.project_id = p.id");

      WHERE("`user_id` = #{userId}");
    }}.toString();
  }
}
