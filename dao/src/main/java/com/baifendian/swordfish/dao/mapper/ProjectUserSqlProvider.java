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

public class ProjectUserSqlProvider {

  public String insert(Map<String, Object> parameter) {
    return new SQL() {{
      INSERT_INTO("project_user");
      VALUES("project_id", "#{projectUser.projectId}");
      VALUES("user_id", "#{projectUser.userId}");
      VALUES("perm", "#{projectUser.perm}");
      VALUES("`create_time`", "#{projectUser.createTime}");
      VALUES("`modify_time`", "#{projectUser.modifyTime}");
    }}.toString();
  }

  public String queryByProject(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("p_u.*");
      SELECT("u.name as user_name,p.name as project_name");
      FROM("project_user p_u");
      JOIN("user u on p_u.user_id = u.id");
      JOIN("project p on p_u.project_id = p.id");
      WHERE("p_u.project_id = #{projectId}");
    }}.toString();
  }

  public String queryForUser(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("u.*");
      FROM("project_user p_u");
      JOIN("user u on p_u.user_id = u.id");
      WHERE("p_u.project_id = #{projectId}");
    }}.toString();
  }

  public String delete(Map<String, Object> parameter) {
    return new SQL() {{
      DELETE_FROM("project_user");
      WHERE("user_id = #{userId}");
      WHERE("project_id = #{projectId}");
    }}.toString();
  }

  public String query(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      SELECT("u.name as user_name,p.name as project_name");
      FROM("project_user p_u");
      JOIN("user u on p_u.user_id = u.id");
      JOIN("project p on p_u.project_id = p.id");
      WHERE("project_id = #{projectId}");
      WHERE("user_id = #{userId}");
    }}.toString();
  }

}
