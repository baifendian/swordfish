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

public class ResourceMapperProvider {

  private final String TABLE_NAME = "resources";

  /**
   * 插入 <p>
   *
   * @return sql 语句
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);

        VALUES("`name`", "#{resource.name,jdbcType=VARCHAR}");
        VALUES("`origin_filename`", "#{resource.originFilename}");
        VALUES("`desc`", "#{resource.desc}");
        VALUES("`owner`", "#{resource.ownerId}");
        VALUES("`project_id`", "#{resource.projectId}");
        VALUES("`create_time`", "#{resource.createTime}");
        VALUES("`modify_time`", "#{resource.modifyTime}");
      }
    }.toString();
  }

  /**
   * 更具项目和资源名称进行查询
   *
   * @param parameter
   * @return
   */
  public String queryByProjectAndResName(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("r.owner as owner_id");
      SELECT("u.name as owner, p.name as project_name");
      SELECT("r.*");

      FROM(TABLE_NAME + " r");

      JOIN("user as u on u.id = r.owner");
      JOIN("project p on r.project_id = p.id");

      WHERE("p.name = #{projectName} and r.name = #{name}");
    }}.toString();
  }

  /**
   * 查询资源详情
   *
   * @param parameter
   * @return
   */
  public String queryResource(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("r.*");
        SELECT("u.name as owner_name");
        SELECT("p.name as project_name");
        FROM(TABLE_NAME+" as r");
        JOIN("user as u on r.owner = u.id");
        JOIN("project as p on r.project_id = p.id");
        WHERE("r.project_id = #{projectId}");
        WHERE("r.name = #{name}");
      }
    }.toString();
  }

  /**
   * 更新资源
   *
   * @param parameter
   * @return
   */
  public String update(Map<String, Object> parameter) {
    return new SQL() {{
      UPDATE(TABLE_NAME);

      SET("`origin_filename` = #{resource.originFilename}");
      SET("`desc` = #{resource.desc}");
      SET("`owner` = #{resource.ownerId}");
      SET("`origin_filename` = #{resource.originFilename}");
      SET("`modify_time` = #{resource.modifyTime}");

      WHERE("`id` = #{resource.id}");
    }}.toString();
  }

  /**
   * 根据资源 id 进行删除
   *
   * @param parameter
   * @return
   */
  public String delete(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);

        WHERE("`id` = #{resourceId}");
      }
    }.toString();
  }

  /**
   * 删除资源
   *
   * @param parameter
   * @return
   */
  public String deleteResource(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);

        WHERE("`project_id` = #{project_id}");
        WHERE("`name` = #{name}");
      }
    }.toString();
  }

  /**
   * 查询资源详情
   *
   * @param parameter
   * @return
   */
  public String queryResourceDetail(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("u.name as owner_name, p.name as project_name");
      SELECT("r.*");

      FROM(TABLE_NAME + " r");

      JOIN("user as u on u.id = r.owner");
      JOIN("project p on r.project_id = p.id");

      WHERE("r.project_id = #{projectId} and r.name = #{name}");
    }}.toString();
  }

  /**
   * 查询项目下资源列表
   *
   * @param parameter
   * @return
   */
  public String queryResourceDetails(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("u.name as owner_name, p.name as project_name");
      SELECT("r.*");

      FROM(TABLE_NAME + " r");

      JOIN("user as u on u.id = r.owner");
      JOIN("project p on r.project_id = p.id");

      WHERE("p.project_id = #{projectId}");
    }}.toString();
  }

  /**
   * 查询项目的工作流数
   * @param parameter
   * @return
   */
  public String countProjectRes(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("count(0)");

      FROM(TABLE_NAME);

      WHERE("project_id = #{projectId}");
    }}.toString();
  }
}
