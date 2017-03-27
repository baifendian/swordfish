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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProjectSqlProvider {

  private static Logger logger = LoggerFactory.getLogger(ProjectSqlProvider.class.getName());

  public String insert(Map<String, Object> parameter) {
    Project project = (Project) parameter.get("newProject");
    logger.debug(project.getName());
    logger.debug(project.getDesc());
    logger.debug(String.valueOf(project.getOwnerId()));
    logger.debug(String.valueOf(project.getCreateTime()));
    logger.debug(String.valueOf(project.getModifyTime()));
    return new SQL() {{
      INSERT_INTO("project");
      VALUES("`name`", "#{newProject.name,jdbcType=VARCHAR}");
      VALUES("`desc`", "#{newProject.desc,jdbcType=VARCHAR}");
      VALUES("create_time", "#{newProject.createTime}");
      VALUES("modify_time", "#{newProject.modifyTime}");
      VALUES("owner", "#{newProject.ownerId}");
    }}.toString();
  }

  public String updateById(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE("project");
      SET("`desc`=#{project.desc}");
      SET("modify_time=#{project.modifyTime}");
      WHERE("id = #{project.id}");
    }}.toString();
  }

  public String deleteById(Map<String, Object> parameter) {
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
        LEFT_OUTER_JOIN("project_user p_u on p.id = p_u.project_id");
        JOIN("user u on p.owner = u.id");
        WHERE("p_u.user_id = #{userId} or p.owner = #{userId}");
      }
    }.toString();
  }

  public String queryAllProject(Map<String, Object> parameter){
    return new SQL() {{
      SELECT("p.*,p.owner as owner_id");
      SELECT("u.name as owner");
      FROM("project p");
      LEFT_OUTER_JOIN("user u on p.owner = u.id");
    }}.toString();
  }
}
