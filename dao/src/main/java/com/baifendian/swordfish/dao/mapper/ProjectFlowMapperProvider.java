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

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class ProjectFlowMapperProvider {

  /**
   * 表名
   */
  public static final String TABLE_NAME = "project_flows";

  /**
   * 插入 <p>
   *
   * @return sql 语句
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);
        VALUES("name", "#{flow.name}");
        VALUES("`desc`", "#{flow.desc}");
        VALUES("project_id", "#{flow.projectId}");
        VALUES("modify_time", "#{flow.modifyTime}");
        VALUES("create_time", "#{flow.createTime}");
        VALUES("owner", "#{flow.ownerId}");
        VALUES("proxy_user", "#{flow.proxyUser}");
        VALUES("user_defined_params", "#{flow.userDefinedParams}");
        VALUES("extras", "#{flow.extras}");
        VALUES("queue", "#{flow.queue}");
      }
    }.toString();
  }

  /**
   * 通过 id 更新(用于重命名) <p>
   *
   * @return sql 语句
   */
  public String updateById(Map<String, Object> parameter) {
    return new SQL() {
      {
        UPDATE(TABLE_NAME);
        SET("`desc`=#{flow.desc}");
        SET("modify_time=#{flow.modifyTime}");
        SET("create_time=#{flow.createTime}");
        SET("owner=#{flow.ownerId}");
        SET("proxy_user=#{flow.proxyUser}");
        SET("user_defined_params=#{flow.userDefinedParams}");
        SET("extras=#{flow.extras}");
        SET("queue=#{flow.queue}");
        WHERE("id = #{flow.id}");
        WHERE("flag is null");
      }
    }.toString();
  }

  public String queryByName(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("p_f.*,p_f.owner as owner_id");
        SELECT("p.name as project_name");
        SELECT("u.name as owner_name");
        FROM("project_flows p_f");
        JOIN("project p on p_f.project_id = p.id");
        JOIN("user u on p_f.owner = u.id");
        WHERE("p_f.project_id = #{projectId}");
        WHERE("p_f.name = #{name}");
      }
    }.toString();
  }

  public String queryById(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("p_f.*,p_f.owner as owner_id");
        SELECT("p.name as project_name");
        SELECT("u.name as owner_name");
        FROM("project_flows p_f");
        JOIN("project p on p_f.project_id = p.id");
        JOIN("user u on p_f.owner = u.id");
        WHERE("p_f.id = #{id}");
      }
    }.toString();
  }

  public String findByProjectNameAndName(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("p_f.*,p_f.owner as owner_id");
        SELECT("p.name as project_name");
        SELECT("u.name as owner_name");
        FROM("project_flows p_f");
        JOIN("project p on p_f.project_id = p.id");
        JOIN("user u on p_f.owner = u.id");
        WHERE("p.name = #{projectName}");
        WHERE("p_f.name = #{name}");
      }
    }.toString();
  }

  public String findByProject(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("project_id = #{projectId}");
      }
    }.toString();
  }

  /**
   * 删除某个workflow <p>
   *
   * @return sql 语句
   */
  public String deleteById(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("id = #{id}");
      }
    }.toString();
  }

  public String queryFlowOwner(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("u.id, u.name, u.email");
        FROM(TABLE_NAME + " as f");
        INNER_JOIN("user as u on u.id = f.owner");
        WHERE("f.id = #{id}");
      }
    }.toString();
  }

  public String updateProjectConf(Map<String, Object> parameter) {
    String queue = parameter.get("queue").toString();
    String desc = parameter.get("proxyUser").toString();
    return new SQL() {
      {
        UPDATE(TABLE_NAME);
        if (!StringUtils.isEmpty(queue)) {
          SET("`queue`=#{queue}");
        }
        if (!StringUtils.isEmpty(desc)) {
          SET("proxy_user=#{proxyUser}");
        }
        WHERE("project_id = #{projectId}");
        WHERE("flag is NULL");
      }
    }.toString();
  }

  /**
   * 计算项目下的工作流数
   *
   * @param parameter
   * @return
   */
  public String countProjectFlows(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("count(0)");

        FROM(TABLE_NAME);

        WHERE("project_id = #{projectId}");
      }
    }.toString();
  }
}
