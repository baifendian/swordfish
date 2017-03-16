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

import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.enums.FlowType;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.model.ExecutionNode;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * workflow 执行的节点信息操作 <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月30日
 */
public class ExecutionNodeMapperProvider {

  public static final String TABLE_NAME = "execution_nodes";

  List<Integer> flowTypes = new ArrayList<>();

  public ExecutionNodeMapperProvider() {
    flowTypes.add(FlowType.LONG.getType());
    flowTypes.add(FlowType.SHORT.getType());
    flowTypes.add(FlowType.ETL.getType());
  }

  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);
        VALUES("exec_id", "#{executionNode.execId}");
        VALUES("flow_id", "#{executionNode.flowId}");
        VALUES("node_id", "#{executionNode.nodeId}");
        VALUES("apps_id", "#{executionNode.appsId}");
        VALUES("status", EnumFieldUtil.genFieldStr("executionNode.status", FlowStatus.class));
        VALUES("start_time", "#{executionNode.startTime}");
        VALUES("end_time", "#{executionNode.endTime}");
        VALUES("attempt", "#{executionNode.attempt}");
        VALUES("job_id", "#{executionNode.jobId}");
      }
    }.toString();
  }

  public String update(ExecutionNode executionNode) {
    return new SQL() {
      {
        UPDATE(TABLE_NAME);
        if (executionNode.getEndTime() != null) {
          SET("end_time = #{executionNode.endTime}");
        }
        if (executionNode.getAppsId() != null) {
          SET("apps_id = #{executionNode.appsId}");
        }
        if (executionNode.getStatus() != null) {
          SET("status = " + EnumFieldUtil.genFieldStr("executionNode.status", FlowStatus.class));
        }
        WHERE("id = #{executionNode.id}");
      }
    }.toString();
  }

  public String selectExecNodeLastAttempt(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("a.*");
        FROM(TABLE_NAME + " as a");
        //INNER_JOIN("(SELECT exec_id, node_id, MAX(attempt) attempt FROM "+TABLE_NAME+" WHERE exec_id = #{execId} AND node_id=#{nodeId} group by exec_id, node_id) as b on a.exec_id = b.exec_id and a.node_id = b.node_id and a.attempt = b.attempt");
        INNER_JOIN("(SELECT exec_id, node_id, MAX(id) id FROM " + TABLE_NAME + " WHERE exec_id = #{execId} AND node_id=#{nodeId} group by exec_id, node_id) as b on a.exec_id = b.exec_id and a.node_id = b.node_id and a.id = b.id");
      }
    }.toString();
  }

  public String selectByNodeId(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
        WHERE("node_id = #{nodeId}");
      }
    }.toString();
  }

  public String selectByNodeIdAndAttempt(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
        WHERE("node_id = #{nodeId}");
        WHERE("attempt = #{attempt}");
      }
    }.toString();
  }

  public String selectByFlowId(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
        WHERE("flow_id = #{flowId}");
      }
    }.toString();
  }

  public String selectStatusByFlowId(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("flow_id, node_id, status, attempt");
        FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
        WHERE("flow_id = #{flowId}");
      }
    }.toString();
  }

  public String deleteByExecId(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
      }
    }.toString();
  }

  public String selectNodeStatus(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("a.status, count(a.status) as num");
        FROM("execution_nodes as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id");
        WHERE("b.project_id = #{projectId}");
        WHERE("a.start_time > #{queryDate}");
        WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
        GROUP_BY("a.status");
      }
    }.toString();
  }

  public String selectUserNodeStatus(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("c.status, count(c.status) as num");
        FROM("execution_flows as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
        INNER_JOIN("execution_nodes as c on b.id = c.flow_id");
        WHERE("a.submit_user = #{userId}");
        WHERE("c.start_time > #{queryDate}");
        WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
        GROUP_BY("c.status");
      }
    }.toString();
  }

  public String selectDayNodeStatus(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("a.status, count(a.status) as num, DATE_FORMAT(a. start_time,'%Y-%m-%d')as day");
        FROM("execution_nodes as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id");
        WHERE("b.project_id = #{projectId}");
        WHERE("a.start_time > #{startDate}");
        WHERE("a.start_time < #{endDate}");
        WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
        GROUP_BY("a.status, day");
      }
    }.toString();
  }

  public String selectHourNodeStatus(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("a.status, count(a.status) as num, DATE_FORMAT(a. start_time,'%k')as hour");
        FROM("execution_nodes as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id");
        WHERE("b.project_id = #{projectId}");
        WHERE("a.start_time > #{startDate}");
        WHERE("a.start_time < #{endDate}");
        WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
        GROUP_BY("a.status, hour");
      }
    }.toString();
  }

  public String selectNodeTopTimes(Map<String, Object> parameter) {
    String sqlTemp = new SQL() {
      {
        SELECT("a.exec_id, a.status, a.start_time, a.end_time");
        SELECT("IFNULL(UNIX_TIMESTAMP(a.end_time)-UNIX_TIMESTAMP(a.start_time), " + "UNIX_TIMESTAMP()-UNIX_TIMESTAMP(a.start_time)) as duration");
        SELECT("b.id as flow_id, b.name as flow_name, b.type as flow_type");
        SELECT("c.id as node_id, c.name as node_name, c.type as node_type");
        SELECT("e.id as submit_user, e.name as submit_user_name");
        FROM("execution_nodes as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
        INNER_JOIN("flows_nodes as c on a.node_id = c.id");
        INNER_JOIN("execution_flows as d on a.exec_id = d.id");
        INNER_JOIN("user as e on d.submit_user = e.id");
        WHERE("a.start_time > #{startDate}");
        WHERE("a.start_time < #{endDate}");
        WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
        ORDER_BY("duration desc");

      }
    }.toString();

    String sql = String.format("%s LIMIT 0, #{num}", sqlTemp);
    return sql;
  }

  public String selectNodeErrorNum(Map<String, Object> parameter) {
    String sqlTemp = new SQL() {
      {
        SELECT("count(a.node_id) as num");
        SELECT("b.id as flow_id, b.name as flow_name, b.type as flow_type");
        SELECT("c.id as node_id, c.name as node_name, c.type as node_type");
        SELECT("e.id as submit_user, e.name as submit_user_name");
        FROM("execution_nodes as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
        INNER_JOIN("flows_nodes as c on a.node_id = c.id");
        INNER_JOIN("execution_flows as d on a.exec_id = d.id");
        INNER_JOIN("user as e on d.submit_user = e.id");
        WHERE("a.start_time > #{startDate}");
        WHERE("a.start_time < #{endDate}");
        WHERE("a.status = " + FlowStatus.FAILED.getType());
        WHERE("b.type in (" + StringUtils.join(flowTypes, ",") + ")");
        GROUP_BY("a.node_id");
        ORDER_BY("num desc");
      }
    }.toString();

    String sql = String.format("%s LIMIT 0, #{num}", sqlTemp);
    return sql;
  }
}
