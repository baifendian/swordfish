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

import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.enums.ScheduleStatus;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class ScheduleMapperProvider {

  public static final String DB_NAME = "schedules";

  public String insert(Map<String, Object> parameter) {
    return new SQL() {{
      INSERT_INTO(DB_NAME);

      VALUES("`flow_id`", "#{schedule.flowId}");
      VALUES("`start_date`", "#{schedule.startDate}");
      VALUES("`end_date`", "#{schedule.endDate}");
      VALUES("`crontab`", "#{schedule.crontab}");
      VALUES("`dep_workflows`", "#{schedule.depWorkflowsStr}");
      VALUES("`dep_policy`", EnumFieldUtil.genFieldStr("schedule.depPolicy", DepPolicyType.class));
      VALUES("`failure_policy`", EnumFieldUtil.genFieldStr("schedule.failurePolicy", FailurePolicyType.class));
      VALUES("`max_try_times`", "#{schedule.maxTryTimes}");
      VALUES("`notify_type`", EnumFieldUtil.genFieldStr("schedule.notifyType", NotifyType.class));
      VALUES("`notify_mails`", "#{schedule.notifyMailsStr}");
      VALUES("`timeout`", "#{schedule.timeout}");
      VALUES("`create_time`", "#{schedule.createTime}");
      VALUES("`modify_time`", "#{schedule.modifyTime}");
      VALUES("`owner`", "#{schedule.ownerId}");
      VALUES("`schedule_status`", EnumFieldUtil.genFieldStr("schedule.scheduleStatus", ScheduleStatus.class));
    }}.toString();
  }

  public String update(Map<String, Object> parameter) {
    return new SQL() {
      {
        UPDATE(DB_NAME);

        SET("`start_date`=#{schedule.startDate}");
        SET("`end_date`=#{schedule.endDate}");
        SET("`crontab`=#{schedule.crontab}");
        SET("`dep_workflows`=#{schedule.depWorkflowsStr}");
        SET("`dep_policy`=" + EnumFieldUtil.genFieldStr("schedule.depPolicy", DepPolicyType.class));
        SET("`failure_policy`=" + EnumFieldUtil.genFieldStr("schedule.failurePolicy", FailurePolicyType.class));
        SET("`max_try_times`=#{schedule.maxTryTimes}");
        SET("`notify_type`=" + EnumFieldUtil.genFieldStr("schedule.notifyType", NotifyType.class));
        SET("`notify_mails`=#{schedule.notifyMailsStr}");
        SET("`timeout`=#{schedule.timeout}");
        SET("`create_time`=#{schedule.createTime}");
        SET("`modify_time`=#{schedule.modifyTime}");
        SET("`owner`=#{schedule.ownerId}");
        SET("`schedule_status`=" + EnumFieldUtil.genFieldStr("schedule.scheduleStatus", ScheduleStatus.class));

        WHERE("flow_id = #{schedule.flowId}");
      }
    }.toString();
  }

  public String selectByFlowId(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("p_f.name as project_flow_name");
      SELECT("p.name as project_name");
      SELECT("u.name as owner_name");
      SELECT("s.*");

      FROM(DB_NAME + " as s");

      JOIN("project_flows as p_f on s.flow_id = p_f.id");
      JOIN("project as p on p_f.project_id = p.id");
      JOIN("user as u on s.owner = u.id");

      WHERE("s.flow_id = #{flowId}");
    }}.toString();
  }

  public String selectByProject(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("p_f.name as workflow_name");
      SELECT("p_f.desc as `desc`");
      SELECT("p.name as project_name");
      SELECT("u.name as owner_name");
      SELECT("s.*");

      FROM(DB_NAME + " as s");

      JOIN("project_flows as p_f on s.flow_id = p_f.id");
      JOIN("project as p on p_f.project_id = p.id");
      JOIN("user as u on s.owner = u.id");

      WHERE("p.name = #{projectName}");
    }}.toString();
  }

  public String selectByFlowName(Map<String, Object> parameter) {
    return new SQL() {{
      SELECT("p_f.name as project_flow_name");
      SELECT("p.name as project_name");
      SELECT("u.name as owner_name");
      SELECT("s.*");

      FROM(DB_NAME + " as s");

      JOIN("project_flows as p_f on s.flow_id = p_f.id");
      JOIN("project as p on p_f.project_id = p.id");
      JOIN("user as u on s.owner = u.id");

      WHERE("p.name = #{projectName}");
      WHERE("p_f.name = #{name}");
    }}.toString();
  }
}
