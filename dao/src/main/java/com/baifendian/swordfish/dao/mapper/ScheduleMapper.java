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
import com.baifendian.swordfish.dao.model.Schedule;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
          @Result(property = "start_date", column = "startDate", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "end_date", column = "endDate", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "crontab", column = "crontab", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dep_workflows", column = "depWorkflowsStr", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dep_policy", column = "depPolicy", javaType = DepPolicyType.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "failure_policy", column = "failurePolicy", javaType = FailurePolicyType.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "max_try_times", column = "maxTryTimes", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "notify_type", column = "notifyType", javaType = NotifyType.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "notify_mails", column = "notifyMails", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "timeout", column = "timeout", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "create_time", column = "createTime", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modifyTime", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "ownerId", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "owner", column = "owner_name", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "schedule_status", column = "scheduleStatus", javaType = ScheduleStatus.class, jdbcType = JdbcType.TINYINT),
  })
  @SelectProvider(type = ScheduleMapperProvider.class, method = "selectByFlowId")
  Schedule selectByFlowId(@Param("flowId") int flowId);

  /**
   * workflow 发布任务的调度查询(单个任务) <p>
   */
  @Results(value = {
          @Result(property = "flowId", column = "flow_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "start_date", column = "startDate", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "end_date", column = "endDate", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "crontab", column = "crontab", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dep_workflows", column = "depWorkflowsStr", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dep_policy", column = "depPolicy", javaType = DepPolicyType.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "failure_policy", column = "failurePolicy", javaType = FailurePolicyType.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "max_try_times", column = "maxTryTimes", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "notify_type", column = "notifyType", javaType = NotifyType.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "notify_mails", column = "notifyMails", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "timeout", column = "timeout", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "create_time", column = "createTime", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modifyTime", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "ownerId", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "owner", column = "owner_name", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "schedule_status", column = "scheduleStatus", javaType = ScheduleStatus.class, jdbcType = JdbcType.TINYINT),
  })
  @SelectProvider(type = ScheduleMapperProvider.class, method = "selectByFlowName")
  Schedule selectByFlowName(@Param("projectName") String projectName,@Param("name") String name);



  @Results(value = {
          @Result(property = "flowId", column = "flow_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "depWorkflows", column = "dep_workflows", javaType = String.class, jdbcType = JdbcType.VARCHAR),
  })
  @SelectProvider(type = ScheduleMapperProvider.class, method = "queryAll")
  List<Schedule> queryAll();

  @DeleteProvider(type = ScheduleMapperProvider.class, method = "deleteByFlowId")
  int deleteByFlowId(@Param("flowId") int flowId);
//
//  /**
//   * 工作流类型分布 <p>
//   */
//  @Results(value = {@Result(property = "flowType", column = "flow_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
//          @Result(property = "value", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),
//  })
//  @SelectProvider(type = ScheduleMapperProvider.class, method = "queryFlowTypeDis")
//  List<DisField> queryFlowTypeDis(@Param("projectId") int projectId);

  /**
   * 查询一个项目中图形化ETL上线的的数目 <p>
   *
   * @return 查询记录数
   */
  @SelectProvider(type = ScheduleMapperProvider.class, method = "queryFlowEtlNum")
  int queryFlowEtlNum(@Param("projectId") int projectId);

//
//  /**
//   * 工作流调度类型分布 <p>
//   */
//  @Results(value = {@Result(property = "scheduleType", column = "schedule_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
//          @Result(property = "value", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),
//  })
//  @SelectProvider(type = ScheduleMapperProvider.class, method = "selectScheduleTypeDis")
//  List<DisField> selectScheduleTypeDis(@Param("projectId") int projectId);

  @SelectProvider(type = ScheduleMapperProvider.class, method = "selectScheduleTypeNull")
  int selectScheduleTypeNull(@Param("projectId") int projectId);


  /**
   * 查询一个组织里面的workflow数量 <p>
   *
   * @return 查询记录数
   */
  @SelectProvider(type = ScheduleMapperProvider.class, method = "queryFlowNum")
  int queryFlowNum(@Param("tenantId") int tenantId, @Param("flowTypes") List<FlowType> flowTypes);

}
