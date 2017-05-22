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

import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecutionFlowMapperProvider {

  private static Logger logger = LoggerFactory.getLogger(ExecutionFlowMapperProvider.class);

  private static final String TABLE_NAME = "execution_flows";

  public ExecutionFlowMapperProvider() {
  }

  /**
   * @param parameter
   * @return
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);

        VALUES("flow_id", "#{executionFlow.flowId}");
        VALUES("worker", "#{executionFlow.worker}");
        VALUES("status", EnumFieldUtil.genFieldStr("executionFlow.status", FlowStatus.class));
        VALUES("submit_user", "#{executionFlow.submitUserId}");
        VALUES("submit_time", "#{executionFlow.submitTime}");
        VALUES("proxy_user", "#{executionFlow.proxyUser}");
        VALUES("schedule_time", "#{executionFlow.scheduleTime}");
        VALUES("start_time", "#{executionFlow.startTime}");
        VALUES("end_time", "#{executionFlow.endTime}");
        VALUES("workflow_data", "#{executionFlow.workflowData}");
        VALUES("user_defined_params", "#{executionFlow.userDefinedParams}");
        VALUES("type", EnumFieldUtil.genFieldStr("executionFlow.type", ExecType.class));
        VALUES("max_try_times", "#{executionFlow.maxTryTimes}");
        VALUES("notify_type", EnumFieldUtil.genFieldStr("executionFlow.notifyType", NotifyType.class));
        VALUES("notify_mails", "#{executionFlow.notifyMails}");
        VALUES("timeout", "#{executionFlow.timeout}");
        VALUES("queue", "#{executionFlow.queue}");
        VALUES("extras", "#{executionFlow.extras}");
      }
    }.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String update(Map<String, Object> parameter) {
    ExecutionFlow executionFlow = (ExecutionFlow) parameter.get("executionFlow");
    return new SQL() {
      {
        UPDATE(TABLE_NAME);

        if (executionFlow.getStatus() != null) {
          SET("status = " + EnumFieldUtil.genFieldStr("executionFlow.status", FlowStatus.class));
        }

        if (executionFlow.getStartTime() != null) {
          SET("start_time = #{executionFlow.startTime}");
        }

        if (executionFlow.getEndTime() != null) {
          SET("end_time = #{executionFlow.endTime}");
        }

        if (executionFlow.getWorker() != null) {
          SET("worker = #{executionFlow.worker}");
        }

        if (executionFlow.getMaxTryTimes() != null) {
          SET("max_try_times = #{executionFlow.maxTryTimes}");
        }

        if (executionFlow.getTimeout() != null) {
          SET("timeout = #{executionFlow.timeout}");
        }

        WHERE("id = #{executionFlow.id}");
      }
    }.toString();
  }

  /**
   * 查询所有没有完成的任务
   *
   * @return
   */
  public String selectAllNoFinishFlow() {
    return new SQL() {
      {
        SELECT("id, flow_id, worker, status");

        FROM(TABLE_NAME);

        WHERE("status <=" + FlowStatus.RUNNING.ordinal());
      }
    }.toString();
  }

  /**
   * 查询某台 executor server 上没有完成的任务
   *
   * @param paramter
   * @return
   */
  public String selectNoFinishFlow(Map<String, Object> paramter) {
    return new SQL() {
      {
        SELECT("id, flow_id, worker");

        FROM(TABLE_NAME);

        WHERE("status <=" + FlowStatus.RUNNING.ordinal());
        WHERE("worker = #{worker}");
      }
    }.toString();
  }

  /**
   * 根据执行 id 查询执行的工作流信息
   *
   * @param parameter
   * @return
   */
  public String selectByExecId(Map<String, Object> parameter) {
    String sql = new SQL() {
      {
        SELECT("b.name as flow_name");
        SELECT("b.project_id as project_id");
        SELECT("b.owner as owner_id");
        SELECT("c.name as project_name");
        SELECT("u.name as submit_user_name");
        SELECT("a.*");

        FROM(TABLE_NAME + " a");

        INNER_JOIN("project_flows b on a.flow_id = b.id");
        INNER_JOIN("project c on b.project_id = c.id");
        INNER_JOIN("user u on a.submit_user = u.id");

        WHERE("a.id = #{execId}");
      }
    }.toString();

    String resultSql = new SQL() {
      {
        SELECT("u.name as owner_name");
        SELECT("t.*");

        FROM("(" + sql + ") t");

        JOIN("user u on t.owner_id = u.id");
      }
    }.toString();

    return resultSql;
  }

  /**
   * 查询节点数
   *
   * @param parameter
   */
  public String selectNodeSizeByExecId(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("count(0)");

        FROM(TABLE_NAME);

        WHERE("id = #{execId}");
      }
    }.toString();
  }

  /**
   * 删除结点
   *
   * @param parameter
   * @return
   */
  public String deleteExecutionNodes(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);

        WHERE("id = #{execId}");
      }
    }.toString();
  }

  /**
   * 查询一定时间范围的工作流信息
   *
   * @param parameter
   * @return
   */
  public String selectByFlowIdAndTimes(Map<String, Object> parameter) {
    StringBuilder sb = new StringBuilder();
    String inExpr = "(" + ExecType.DIRECT.ordinal() + "," + ExecType.COMPLEMENT_DATA.ordinal() + ")";

    sb.append("SELECT id, flow_id, worker, type, status, schedule_time FROM execution_flows WHERE flow_id = #{flowId} AND type IN " + inExpr + " AND ");
    sb.append("schedule_time = (SELECT MIN(schedule_time) FROM execution_flows WHERE flow_id = #{flowId} AND type IN" + inExpr
        + " AND schedule_time >= #{startDate} AND schedule_time < #{endDate})");

    return sb.toString();
  }

  /**
   * 查询一定时间范围内, 特定状态的工作流信息
   *
   * @param parameter
   * @return
   */
  public String selectByFlowIdAndTimesAndStatusLimit(Map<String, Object> parameter) {
    List<FlowStatus> flowStatuses = (List<FlowStatus>) parameter.get("status");

    List<String> workflowList = (List<String>) parameter.get("workflowList");

    List<String> workflowList2 = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(workflowList)) {
      for (String workflow : workflowList) {
        workflowList2.add("\"" + workflow + "\"");
      }
    }

    List<String> flowStatusStrList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(flowStatuses)) {
      for (FlowStatus status : flowStatuses) {
        flowStatusStrList.add(String.valueOf(status.ordinal()));
      }
    }

    String where = String.join(",", flowStatusStrList);

    String sql = new SQL() {
      {
        SELECT("p_f.name as flow_name");
        SELECT("p.name as project_name");
        SELECT("u.name as owner");
        SELECT("e_f.*");

        FROM(TABLE_NAME + " e_f");

        JOIN("project_flows p_f on e_f.flow_id = p_f.id");
        JOIN("project p on p_f.project_id = p.id");
        JOIN("user u on p_f.owner = u.id");

        WHERE("p.name = #{projectName}");

        if (CollectionUtils.isNotEmpty(workflowList)) {
          WHERE("p_f.name in (" + String.join(",", workflowList2) + ")");
        }

        WHERE("schedule_time >= #{startDate}");
        WHERE("schedule_time <= #{endDate}");

        if (CollectionUtils.isNotEmpty(flowStatuses)) {
          WHERE("`status` in (" + where + ") ");
        }
      }
    }.toString();

    String sql2 = new SQL() {
      {
        SELECT("u.name as submit_user_name");
        SELECT("e_f.*");

        FROM("(" + sql + ") e_f");

        JOIN("user u on e_f.submit_user = u.id");
      }
    }.toString() + " order by schedule_time DESC limit #{start},#{limit}";

    return sql2;
  }

  /**
   * 根据时间和状态进行汇总查询
   *
   * @param parameter
   * @return
   */
  public String sumByFlowIdAndTimesAndStatus(Map<String, Object> parameter) {
    List<FlowStatus> flowStatuses = (List<FlowStatus>) parameter.get("status");
    List<String> workflowList = (List<String>) parameter.get("workflowList");
    List<String> workflowList2 = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(workflowList)) {
      for (String workflow : workflowList) {
        workflowList2.add("\"" + workflow + "\"");
      }
    }

    List<String> flowStatusStrList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(flowStatuses)) {
      for (FlowStatus status : flowStatuses) {
        flowStatusStrList.add(String.valueOf(status.ordinal()));
      }
    }

    String where = String.join(",", flowStatusStrList);

    return new SQL() {
      {
        SELECT("count(0)");

        FROM(TABLE_NAME + " e_f");

        JOIN("project_flows p_f on e_f.flow_id = p_f.id");
        JOIN("project p on p_f.project_id = p.id");

        WHERE("p.name = #{projectName}");

        if (CollectionUtils.isNotEmpty(workflowList)) {
          WHERE("p_f.name in (" + String.join(",", workflowList2) + ")");
        }

        WHERE("schedule_time >= #{startDate}");
        WHERE("schedule_time < #{endDate}");

        if (CollectionUtils.isNotEmpty(flowStatuses)) {
          WHERE("`status` in (" + where + ") ");
        }
      }
    }.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String selectByFlowIdAndTime(Map<String, Object> parameter) {
    StringBuilder sb = new StringBuilder();
    String inExpr = "(" + ExecType.DIRECT.ordinal() + "," + ExecType.COMPLEMENT_DATA.ordinal() + ")";

    sb.append("SELECT id, flow_id, worker, type, status, schedule_time FROM execution_flows WHERE flow_id = #{flowId} AND type IN " + inExpr + " AND ");
    sb.append("schedule_time = #{scheduleTime}");

    return sb.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String selectStateByProject(Map<String, Object> parameter) {
    String sql = new SQL() {
      {
        SELECT("str_to_date(DATE_FORMAT(e_f.schedule_time,'%Y%m%d'),'%Y%m%d') as day,\n" +
            "SUM(case e_f.status when 0 then 1 else 0 end) as INIT,\n" +
            "SUM(case e_f.status when 1 then 1 else 0 end) as WAITING_DEP,\n" +
            "SUM(case e_f.status when 2 then 1 else 0 end) as WAITING_RES,\n" +
            "SUM(case e_f.status when 3 then 1 else 0 end) as RUNNING,\n" +
            "SUM(case e_f.status when 4 then 1 else 0 end) as SUCCESS,\n" +
            "SUM(case e_f.status when 5 then 1 else 0 end) as `KILL`,\n" +
            "SUM(case e_f.status when 6 then 1 else 0 end) as `FAILED`,\n" +
            "SUM(case e_f.status when 7 then 1 else 0 end) as `DEP_FAILED`");

        FROM(TABLE_NAME + " e_f");

        JOIN("project_flows p_f on e_f.flow_id = p_f.id");
        WHERE("e_f.schedule_time >= #{startDate} AND e_f.schedule_time <= #{endDate}");
        WHERE("p_f.project_id = #{projectId}");

        GROUP_BY("day");
      }
    }.toString();
    return sql;
  }

  /**
   * @param parameter
   * @return
   */
  public String selectStateHourByProject(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("CONVERT(DATE_FORMAT(e_f.schedule_time,'%H'),SIGNED) as hour,\n" +
            "SUM(case e_f.status when 0 then 1 else 0 end) as INIT,\n" +
            "SUM(case e_f.status when 1 then 1 else 0 end) as WAITING_DEP,\n" +
            "SUM(case e_f.status when 2 then 1 else 0 end) as WAITING_RES,\n" +
            "SUM(case e_f.status when 3 then 1 else 0 end) as RUNNING,\n" +
            "SUM(case e_f.status when 4 then 1 else 0 end) as SUCCESS,\n" +
            "SUM(case e_f.status when 5 then 1 else 0 end) as `KILL`,\n" +
            "SUM(case e_f.status when 6 then 1 else 0 end) as `FAILED`,\n" +
            "SUM(case e_f.status when 7 then 1 else 0 end) as `DEP_FAILED`");

        FROM(TABLE_NAME + " e_f");

        JOIN("project_flows p_f on e_f.flow_id = p_f.id");

        WHERE("str_to_date(DATE_FORMAT(e_f.schedule_time,'%Y%m%d'),'%Y%m%d') = #{day}");
        WHERE("p_f.project_id = #{projectId}");

        GROUP_BY("hour");
      }
    }.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String selectDurationsByProject(Map<String, Object> parameter) {
    String sql1 = new SQL() {
      {
        SELECT("timestampdiff(SECOND,start_time,end_time) as duration");
        SELECT("p_f.name as flow_name");
        SELECT("u.name as owner_name");
        SELECT("e_f.*");

        FROM(TABLE_NAME + " e_f");

        JOIN("project_flows p_f on e_f.flow_id = p_f.id");
        JOIN("user u on p_f.owner = u.id");

        WHERE("str_to_date(DATE_FORMAT(e_f.schedule_time,'%Y%m%d'),'%Y%m%d') = #{date}");
        WHERE("p_f.project_id = #{projectId}");

        ORDER_BY("duration DESC");
      }
    }.toString() + " limit #{top}";

    return new SQL() {{
      SELECT("s1.*");
      SELECT("u.name as proxyUser");

      FROM("(" + sql1 + ") s1");

      LEFT_OUTER_JOIN("user u on s1.proxy_user = u.id");
    }}.toString();
  }

  /**
   * @param parameter
   * @return
   */
  public String selectErrorsByProject(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("count(0) as times");
        SELECT("p_f.name as flow_name");
        SELECT("u.name as owner_name");
        SELECT("p.name as projectName");
        SELECT("p_f.proxy_user");

        FROM(TABLE_NAME + " e_f");

        JOIN("project_flows p_f on e_f.flow_id = p_f.id");
        JOIN("user u on p_f.owner = u.id");
        JOIN("project p on p_f.project_id = p.id");

        WHERE("str_to_date(DATE_FORMAT(e_f.schedule_time,'%Y%m%d'),'%Y%m%d') = #{date}");
        WHERE("e_f.status in (" + FlowStatus.FAILED.ordinal() + "," + FlowStatus.DEP_FAILED.ordinal() + ")");
        WHERE("p.id = #{projectId}");

        GROUP_BY("e_f.flow_id");
        ORDER_BY("times DESC");
      }
    }.toString() + " limit #{top}";
  }

  /**
   * @param parameter
   * @return
   */
  public String selectPreDate(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");

        FROM(TABLE_NAME);

        WHERE("schedule_time <= #{date}");
        WHERE("flow_id = #{flowId}");

        ORDER_BY("schedule_time DESC");
      }
    }.toString() + " limit 1";
  }

  /**
   * 根据flowId和ScheduleTime查询一个ExecutionFlow
   *
   * @param parameter
   * @return
   */
  public String selectExecutionFlowByScheduleTime(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      FROM(TABLE_NAME);
      WHERE("flow_id = #{flowId}");
      WHERE("schedule_time = #{scheduleTime}");
    }}.toString();
  }
}
