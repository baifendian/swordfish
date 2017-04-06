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

import com.baifendian.swordfish.dao.enums.FlowType;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.model.ProjectFlow;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProjectFlowMapperSqlProvider {

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

  public String queryByProjectAndName(Map<String, Object> parameter){
    return new SQL() {
      {
        SELECT("p_f.*,p_f.owner as owner_id");
        SELECT("u.name as owner_name,p.name as project_name");
        FROM("project_flows p_f");
        JOIN("user u on p_f.owner = u.id");
        JOIN("project p on p_f.project_id = p.id");
      }
    }.toString();
  }
  /**
   * 通过 id 更新(用于重命名) <p>
   *
   * @return sql 语句
   */
  public String updateById(Map<String, Object> parameter) {
    ProjectFlow projectFlow = (ProjectFlow) parameter.get("flow");
    return new SQL() {
      {
        UPDATE(TABLE_NAME);
        if (StringUtils.isNotEmpty(projectFlow.getName())) {
          SET("name = #{flow.name}");
        }
        if (projectFlow.getOwnerId() != 0) {
          SET("owner = #{flow.ownerId}");
        }
        WHERE("id = #{flow.id}");
      }
    }.toString();
  }

  /**
   * 查询一个记录 <p>
   *
   * @return sql 语句
   */
  public String query(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("f.*");
        SELECT("p.name as project_name");
        FROM(TABLE_NAME + " as f");
        INNER_JOIN("project as p on p.id = f.project_id");
        WHERE("f.id = #{id}");
      }
    }.toString();
  }

  public String queryByName(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("p_f.*,p_f.owner as owner_id");
        SELECT("p.name as project_name");
        SELECT("u.name as owner");
        FROM("project_flows p_f");
        JOIN("project p on p_f.project_id = p.id");
        JOIN("user u on p_f.owner = u.id");
        WHERE("p_f.project_id = #{projectId}");
        WHERE("p_f.name = #{name}");
      }
    }.toString();
  }

  public String findByProjectNameAndName(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("p_f.*,p_f.owner as owner_id");
        SELECT("p.name as project_name");
        SELECT("u.name as owner");
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
   * 查询多个记录 <p>
   *
   * @return sql 语句
   */
  public String findByIds(Set<Integer> flowIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select a.*, b.name as project_name, c.name as owner_name, s.schedule_status ");
    sb.append("from ");
    sb.append(TABLE_NAME);
    sb.append(" as a ");
    sb.append("inner join schedules as s on a.id = s.flow_id ");
    sb.append("inner join project as b on a.project_id = b.id ");
    sb.append("inner join user as c on a.owner_id = c.id ");

    if (flowIds != null && flowIds.size() > 0) {
      StringUtils.join(flowIds, ",");
      sb.append("where a.id in (");
      sb.append(StringUtils.join(flowIds, ","));
      sb.append(") ");
    }

    return sb.toString();
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

  /**
   * 删除某个workflow <p>
   *
   * @return sql 语句
   */
  public String deleteByProjectAndName(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("project_id = #{projectId}");
        WHERE("name = #{name}");
      }
    }.toString();
  }

  public String queryFlowNum(int tenantId, List<FlowType> flowTypes) {

    StringBuilder sb = new StringBuilder();
    sb.append("select count(0) ");
    sb.append("from " + TABLE_NAME + " as a ");
    sb.append("inner join project as b on a.project_id = b.id ");
    sb.append("inner join tenant as c on b.tenant_id = c.id and c.id = #{tenantId} ");

    if (flowTypes != null && flowTypes.size() > 0) {
      sb.append("where a.type in (-1 ");
      for (FlowType flowType : flowTypes) {
        sb.append(",");
        sb.append(flowType.getType());
      }
      sb.append(") ");
    }

    return sb.toString();
  }

  public String queryFlowNumByProjectId(int projectId, List<FlowType> flowTypes) {

    StringBuilder sb = new StringBuilder();
    sb.append("select count(0) ");
    sb.append("from " + TABLE_NAME + " as a ");
    sb.append("inner join project as b on a.project_id = b.id and b.id = #{projectId} ");

    if (flowTypes != null && flowTypes.size() > 0) {
      sb.append("where a.type in (-1 ");
      for (FlowType flowType : flowTypes) {
        sb.append(",");
        sb.append(flowType.getType());
      }
      sb.append(") ");
    }

    return sb.toString();
  }

  /**
   * 查询某个项目下的特定类型的所有workflow <p>
   *
   * @return sql 语句
   */
  public String queryFlowsByProjectId(int projectId, FlowType flowType) {

    StringBuilder sb = new StringBuilder();
    sb.append("select a.* ");
    sb.append("from " + TABLE_NAME + " as a ");
    sb.append("inner join project as b on a.project_id = b.id and b.id = #{projectId} ");

    if (flowType != null) {
      sb.append("where a.type = ");
      sb.append(flowType.getType());
    }

    return sb.toString();
  }

  /**
   * 删除某个项目下的工作流类型
   */
  public String deleteByProjectId(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("project_id = #{projectId}");
        WHERE("type = " + EnumFieldUtil.genFieldStr("flowType", FlowType.class));
      }
    }.toString();
  }

  public String updateByName(Map<String, Object> parameter) {
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
        WHERE("project_id = #{flow.projectId}");
        WHERE("name = #{flow.name}");
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
        if (!StringUtils.isEmpty(queue)){
          SET("`queue`=#{queue}");
        }
        if (!StringUtils.isEmpty(desc)){
          SET("proxy_user=#{proxyUser}");
        }
        WHERE("project_id = #{projectId}");
      }
    }.toString();
  }
}
