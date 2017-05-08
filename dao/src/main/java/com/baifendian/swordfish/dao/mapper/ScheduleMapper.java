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
import com.baifendian.swordfish.dao.model.Schedule;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

public interface ScheduleMapper {

  /**
   * 插入记录 <p>
   *
   * @return 插入记录数
   */
  @InsertProvider(type = ScheduleMapperProvider.class, method = "insert")
  int insert(@Param("schedule") Schedule schedule);

  /**
   * 任务的调度设置 <p>
   *
   * @return 更新记录数
   */
  @UpdateProvider(type = ScheduleMapperProvider.class, method = "update")
  int update(@Param("schedule") Schedule schedule);

  /**
   * workflow 发布任务的调度查询(单个任务) <p>
   */
  @Results(value = {
      @Result(property = "flowId", column = "flow_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "startDate", column = "start_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "endDate", column = "end_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "crontab", column = "crontab", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "depWorkflowsStr", column = "dep_workflows", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "depPolicy", column = "dep_policy", typeHandler = EnumOrdinalTypeHandler.class, javaType = DepPolicyType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "failurePolicy", column = "failure_policy", typeHandler = EnumOrdinalTypeHandler.class, javaType = FailurePolicyType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "maxTryTimes", column = "max_try_times", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "notifyType", column = "notify_type", typeHandler = EnumOrdinalTypeHandler.class, javaType = NotifyType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "notifyMailsStr", column = "notify_mails", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "timeout", column = "timeout", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "ownerId", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "scheduleStatus", column = "schedule_status", typeHandler = EnumOrdinalTypeHandler.class, javaType = ScheduleStatus.class, jdbcType = JdbcType.TINYINT),
  })
  @SelectProvider(type = ScheduleMapperProvider.class, method = "selectByFlowId")
  Schedule selectByFlowId(@Param("flowId") int flowId);

  /**
   * 查询一个项目下所有的调度
   *
   * @param projectName
   * @return
   */
  @Results(value = {
      @Result(property = "flowId", column = "flow_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "startDate", column = "start_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "endDate", column = "end_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "crontab", column = "crontab", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "depWorkflowsStr", column = "dep_workflows", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "depPolicy", column = "dep_policy", typeHandler = EnumOrdinalTypeHandler.class, javaType = DepPolicyType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "failurePolicy", column = "failure_policy", typeHandler = EnumOrdinalTypeHandler.class, javaType = FailurePolicyType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "maxTryTimes", column = "max_try_times", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "notifyType", column = "notify_type", typeHandler = EnumOrdinalTypeHandler.class, javaType = NotifyType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "notifyMailsStr", column = "notify_mails", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "timeout", column = "timeout", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "ownerId", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "scheduleStatus", column = "schedule_status", typeHandler = EnumOrdinalTypeHandler.class, javaType = ScheduleStatus.class, jdbcType = JdbcType.TINYINT),
  })
  @SelectProvider(type = ScheduleMapperProvider.class, method = "selectByProject")
  List<Schedule> selectByProject(@Param("projectName") String projectName);

  /**
   * workflow 发布任务的调度查询(单个任务) <p>
   */
  @Results(value = {
      @Result(property = "flowId", column = "flow_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "startDate", column = "start_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "endDate", column = "end_date", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "crontab", column = "crontab", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "depWorkflowsStr", column = "dep_workflows", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "depPolicy", column = "dep_policy", typeHandler = EnumOrdinalTypeHandler.class, javaType = DepPolicyType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "failurePolicy", column = "failure_policy", typeHandler = EnumOrdinalTypeHandler.class, javaType = FailurePolicyType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "maxTryTimes", column = "max_try_times", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "notifyType", column = "notify_type", typeHandler = EnumOrdinalTypeHandler.class, javaType = NotifyType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "notifyMailsStr", column = "notify_mails", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "timeout", column = "timeout", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "ownerId", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "scheduleStatus", column = "schedule_status", typeHandler = EnumOrdinalTypeHandler.class, javaType = ScheduleStatus.class, jdbcType = JdbcType.TINYINT),
  })
  @SelectProvider(type = ScheduleMapperProvider.class, method = "selectByFlowName")
  Schedule selectByFlowName(@Param("projectName") String projectName, @Param("name") String name);
}
