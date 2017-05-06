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
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ExecutionFlowError;
import com.baifendian.swordfish.dao.model.ExecutionState;
import com.baifendian.swordfish.dao.model.MaintainQuery;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * workflow 执行的信息操作 <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月30日
 */
public interface ExecutionFlowMapper {

  /**
   * 插入记录并获取记录 id <p>
   *
   * @return 修改记录数
   */
  @InsertProvider(type = ExecutionFlowMapperProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "executionFlow.id", resultType = int.class, before = false)
  int insertAndGetId(@Param("executionFlow") ExecutionFlow executionFlow);

  /**
   * workflow 执行的信息更新 <p>
   *
   * @return 更新记录数
   */
  @UpdateProvider(type = ExecutionFlowMapperProvider.class, method = "update")
  int update(@Param("executionFlow") ExecutionFlow executionFlow);

  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "worker", column = "worker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowStatus.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "submitUserId", column = "submit_user_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "submitUser", column = "submit_user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "submitTime", column = "submit_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "scheduleTime", column = "schedule_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "workflowData", column = "workflow_data", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = ExecType.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "queue", column = "queue", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "userDefinedParams", column = "user_defined_params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "extras", column = "extras", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "maxTryTimes", column = "max_try_times", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "timeout", column = "timeout", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "notifyType", column = "notify_type", typeHandler = EnumOrdinalTypeHandler.class, javaType = NotifyType.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "notifyMails", column = "notify_mails", javaType = String.class, jdbcType = JdbcType.VARCHAR),
  })
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectByExecId")
  ExecutionFlow selectByExecId(@Param("execId") Integer execId);

  /**
   * 获取所有未完成的工作流
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "worker", column = "worker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowStatus.class, jdbcType = JdbcType.TINYINT),
  })
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectAllNoFinishFlow")
  List<ExecutionFlow> selectAllNoFinishFlow();

  /**
   * 获取指定worker未完成的工作流列表
   * @param worker
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
  })
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectNoFinishFlow")
  List<ExecutionFlow> selectNoFinishFlow(@Param("worker") String worker);

  /**
   * 按照时间查询工作流的最新状态
   * @param flowId
   * @param startDate
   * @param endDate
   * @return
   */
  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "worker", column = "worker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "scheduleTime", column = "schedule_time", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectByFlowIdAndTimes")
  List<ExecutionFlow> selectByFlowIdAndTimes(@Param("flowId") Integer flowId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

  /**
   * 查询指定工作流的运行
   * @param projectName
   * @param workflowList
   * @param startDate
   * @param endDate
   * @param start
   * @param limit
   * @param statuses
   * @return
   */
  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "worker", column = "worker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "scheduleTime", column = "schedule_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "submitTime", column = "submit_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "submitUserId", column = "schedule_user", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "submitUser", column = "submit_user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectByFlowIdAndTimesAndStatusLimit")
  List<ExecutionFlow> selectByFlowIdAndTimesAndStatusLimit(@Param("projectName") String projectName,@Param("workflowList") List<String> workflowList, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("start") int start, @Param("limit") int limit, @Param("status")List<FlowStatus> statuses);

  /**
   * 根据工作流ID时间和状态查询工作流执行情况
   * @param projectName
   * @param workflowList
   * @param startDate
   * @param endDate
   * @param statuses
   * @return
   */
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "sumByFlowIdAndTimesAndStatus")
  int sumByFlowIdAndTimesAndStatus(@Param("projectName") String projectName,@Param("workflowList") List<String> workflowList,@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("status")List<FlowStatus> statuses);

  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "worker", column = "worker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "scheduleTime", column = "schedule_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectByFlowIdAndTime")
  List<ExecutionFlow> selectByFlowIdAndTime(@Param("flowId") Integer flowId, @Param("scheduleTime") Date scheduleTime);

  /**
   * 统计出一段时间内，某个项目下各种状态下的任务数（天为单位）
   * @return
   */
  @Results(value = {
          @Result(property = "day", column = "day", id = true, javaType = Date.class, jdbcType = JdbcType.DATE),
          @Result(property = "init", column = "INIT", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "waitingDep", column = "WAITING_DEP", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "waitingRes", column = "WAITING_RES", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "running", column = "RUNNING", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "success", column = "SUCCESS", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "kill", column = "KILL", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "failed", column = "FAILED", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "depFailed", column = "DEP_FAILED", javaType = int.class, jdbcType = JdbcType.INTEGER),
  })
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectStateByProject")
  List<ExecutionState> selectStateByProject(@Param("projectId") int projectId,@Param("startDate") Date startDate,@Param("endDate") Date endDate);

  @Results(value = {
          @Result(property = "hour", column = "hour", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "init", column = "INIT", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "waitingDep", column = "WAITING_DEP", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "waitingRes", column = "WAITING_RES", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "running", column = "RUNNING", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "success", column = "SUCCESS", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "kill", column = "KILL", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "failed", column = "FAILED", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "depFailed", column = "DEP_FAILED", javaType = int.class, jdbcType = JdbcType.INTEGER),
  })
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectStateHourByProject")
  List<ExecutionState> selectStateHourByProject(@Param("projectId") int projectId,@Param("day") Date day);
  /**
   * 统计某天的工作流耗时TOP
   * @param projectId
   * @param top
   * @param date
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "worker", column = "worker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "scheduleTime", column = "schedule_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "submitTime", column = "submit_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "submitUserId", column = "schedule_user", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "submitUser", column = "submit_user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "consume", column = "consume", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
  })
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectConsumesByProject")
  List<ExecutionFlow> selectConsumesByProject(@Param("projectId") int projectId,@Param("top") int top,@Param("date") Date date);

  /**
   * 统计某天的工作流异常数TOP
   * @param projectId
   * @param top
   * @param date
   * @return
   */
  @Results(value = {
          @Result(property = "workflowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "owner", column = "owner_name",javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "proxyUser", column = "proxy_user",javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "times", column = "times", javaType = int.class, jdbcType = JdbcType.INTEGER),
  })
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectErrorsByProject")
  List<ExecutionFlowError> selectErrorsByProject(@Param("projectId") int projectId, @Param("top") int top, @Param("date") Date date);
}
