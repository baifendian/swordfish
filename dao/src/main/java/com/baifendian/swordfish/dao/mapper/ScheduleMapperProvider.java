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

import com.baifendian.swordfish.dao.enums.*;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.model.Schedule;

import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * schedules 表操作SQL <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月25日
 */
public class ScheduleMapperProvider {
  public static final String DB_NAME = "schedules";

  public String insert(Map<String, Object> parameter) {
    return new SQL() {{
      INSERT_INTO(DB_NAME);
      VALUES("flow_id", "#{schedule.flowId}");
      VALUES("create_time", "#{schedule.createTime}");
      VALUES("modify_time", "#{schedule.modifyTime}");
      VALUES("last_modify_by", "#{schedule.lastModifyBy}");
      VALUES("pub_status", EnumFieldUtil.genFieldStr("schedule.pubStatus", PubStatus.class));
      VALUES("schedule_status", EnumFieldUtil.genFieldStr("schedule.scheduleStatus", ScheduleStatus.class));
      VALUES("start_date", "#{schedule.startDate}");
      VALUES("end_date", "#{schedule.endDate}");
      VALUES("schedule_type", EnumFieldUtil.genFieldStr("schedule.scheduleType", ScheduleType.class));
      VALUES("crontab_str", "#{schedule.crontabStr}");
      VALUES("next_submit_time", "#{schedule.nextSubmitTime}");
      VALUES("dep_workflows", "#{schedule.depWorkflows}");
      VALUES("failure_policy", EnumFieldUtil.genFieldStr("schedule.failurePolicy", FailurePolicyType.class));
      VALUES("dep_policy", EnumFieldUtil.genFieldStr("schedule.depPolicy", DepPolicyType.class));
      VALUES("max_try_times", "#{schedule.maxTryTimes}");
      VALUES("notify_type", EnumFieldUtil.genFieldStr("schedule.notifyType", NotifyType.class));
      VALUES("notify_emails", "#{schedule.notifyEmails}");
      VALUES("timeout", "#{schedule.timeout}");

    }}.toString();
  }

  public String update(Schedule schedule) {
    return new SQL() {
      {
        UPDATE(DB_NAME);
        SET("modify_time = #{schedule.modifyTime}");
        SET("last_modify_by = #{schedule.lastModifyBy}");
        if (schedule.getPubStatus() != null) {
          SET("pub_status = " + EnumFieldUtil.genFieldStr("schedule.pubStatus", PubStatus.class));
        }
        if (schedule.getScheduleStatus() != null) {
          SET("schedule_status = " + EnumFieldUtil.genFieldStr("schedule.scheduleStatus", ScheduleStatus.class));
        }
        if (schedule.getStartDate() != null) {
          SET("start_date = #{schedule.startDate}");
          SET("end_date = #{schedule.endDate}");
          SET("schedule_type = " + EnumFieldUtil.genFieldStr("schedule.scheduleType", ScheduleType.class));
          SET("crontab_str = #{schedule.crontabStr}");
        }
        if (schedule.getNotifyType() != null) {
          SET("notify_type = " + EnumFieldUtil.genFieldStr("schedule.notifyType", NotifyType.class));
        }
        if (schedule.getNotifyEmails() != null) {
          SET("notify_emails = #{schedule.notifyEmails}");
        }
        if (schedule.getMaxTryTimes() != null) {
          SET("max_try_times = #{schedule.maxTryTimes}");
        }
        if (schedule.getFailurePolicy() != null) {
          SET("failure_policy = " + EnumFieldUtil.genFieldStr("schedule.failurePolicy", FailurePolicyType.class));
        }
        if (schedule.getDepWorkflows() != null) {
          SET("dep_workflows = #{schedule.depWorkflows}");
        }
        if (schedule.getDepPolicy() != null) {
          SET("dep_policy = " + EnumFieldUtil.genFieldStr("schedule.depPolicy", DepPolicyType.class));
        }
        if (schedule.getTimeout() != null) {
          SET("timeout = #{schedule.timeout}");
        }
        WHERE("flow_id = #{schedule.flowId}");
      }
    }.toString();
  }

  public String selectByFlowId(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("*");
      FROM(DB_NAME);
      WHERE("flow_id = #{flowId}");
    }}.toString();
  }

  public String selectByProjectId(int projectId, PubStatus pubStatus, ScheduleStatus scheduleStatus) {
    return new SQL() {
      {
        SELECT("a.*");
        SELECT("b.name as flow_name");
        SELECT("b.type as flow_type");
        SELECT("b.publish_time");
        SELECT("c.id as owner_id");
        SELECT("c.name as owner_name");
        FROM("schedules as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
        INNER_JOIN("user as c on b.owner_id = c.id");
        if (pubStatus != null) {
          WHERE("pub_status = " + EnumFieldUtil.genFieldStr("pubStatus", PubStatus.class));
        }
        if (scheduleStatus != null) {
          WHERE("schedule_status = " + EnumFieldUtil.genFieldStr("scheduleStatus", ScheduleStatus.class));
        }
      }
    }.toString();
  }

  public String selectByFlowIds(Set<Integer> flowIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("select a.*, b.name as flow_name, b.type as flow_type, c.id as owner_id, c.name as owner_name, b.publish_time ");
    sb.append("from schedules as a ");
    sb.append("inner join project_flows as b on a.flow_id = b.id ");
    sb.append("inner join user as c on b.owner_id = c.id ");
    sb.append("where a.flow_id in ( 0");
    for (Integer flowId : flowIds) {
      sb.append(",");
      sb.append(flowId);
    }
    sb.append(") ");
    return sb.toString();
  }

  public String queryAll(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("flow_id,dep_workflows");
        FROM(DB_NAME);
        WHERE("pub_status = 1");
      }

    }.toString();
  }

  public String deleteByFlowId(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(DB_NAME);
        WHERE("flow_id = #{flowId}");
      }
    }.toString();
  }

  public String queryTaskTypeDis(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("c.type as node_type , count(c.type) as num");
        FROM("schedules as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
        INNER_JOIN("flows_nodes as c on a.flow_id = c.flow_id ");
        //WHERE("a.schedule_type is not null");
        WHERE("b.type =" + FlowType.SHORT.getType() + " or b.type = " + FlowType.LONG.getType());
        GROUP_BY("c.type");
      }
    }.toString();
  }

  public String queryFlowTypeDis(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("b.type as flow_type , count(b.type) as num");
        FROM("schedules as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
        GROUP_BY("b.type");
      }
    }.toString();
  }

  public String queryFlowEtlNum(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("count(0) as num");
        FROM("schedules as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
        //WHERE("a.schedule_type is not null");
        WHERE("b.type =" + FlowType.ETL.getType());
      }
    }.toString();
  }

  public String selectScheduleTypeDis(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("a.schedule_type, count(a.schedule_type) as num");
        FROM("schedules as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
        WHERE("a.schedule_type is not null");
        GROUP_BY("a.schedule_type");
      }
    }.toString();
  }

  public String selectScheduleTypeNull(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("count(0)");
        FROM("schedules as a");
        INNER_JOIN("project_flows as b on a.flow_id = b.id and b.project_id = #{projectId}");
        WHERE("a.schedule_type is null");
      }
    }.toString();
  }

  public String queryFlowNum(int tenantId, List<FlowType> flowTypes) {

    StringBuilder sb = new StringBuilder();
    sb.append("select count(0) ");
    sb.append("from project_flows as a ");
    sb.append("inner join schedules as d on a.id = d.flow_id ");
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

}
