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

import com.baifendian.swordfish.dao.model.Project;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class ProjectSqlProvider {

  public String insert(Map<String, Object> parameter) {
    return new SQL() {{
      INSERT_INTO("project");
      VALUES("name", "#{project.name}");
      VALUES("`desc`", "#{project.desc}");
      VALUES("create_time", "#{project.createTime}");
      VALUES("modify_time", "#{project.modifyTime}");
      VALUES("owner", "#{project.owenrId}");
    }}.toString();
  }

  public String updateById(final Project project) {
    return new SQL() {{
      UPDATE("project");
      SET("`desc`=#{project.desc}");
      SET("modify_time=#{project.modifyTime}");
      WHERE("id = #{project.id}");
    }}.toString();
  }

  public String deleteById(final int id) {
    return new SQL() {{
      DELETE_FROM("project");
      WHERE("id = #{id}");
    }}.toString();
  }

  public String queryByName(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("p.*,p.owner as owner_id");
      SELECT("u.name as owner");
      FROM("project p");
      JOIN("user u on p.owner = u.id");
      WHERE("p.name = #{name}");
    }}.toString();
  }

  public String queryProjectByUser(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("p.*,p.owner as owner_id");
        SELECT("u.name as owner");
        FROM("project p");
        JOIN("project_user p_u on p_u.project_id = p.id");
        LEFT_OUTER_JOIN("user u on p.owner = u.id");
        WHERE("p_u.user_id = #{userId} or p.owner = #{userId}");
      }
    }.toString();
  }

}
