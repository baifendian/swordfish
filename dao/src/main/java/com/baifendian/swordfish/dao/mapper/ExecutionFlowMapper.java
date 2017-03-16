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

import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.MaintainQuery;
import com.baifendian.swordfish.dao.model.statistics.FlowAvgTime;
import com.baifendian.swordfish.dao.model.statistics.FlowNodeErrorNum;
import com.baifendian.swordfish.dao.model.statistics.FlowNodeTimeConsumer;
import com.baifendian.swordfish.dao.model.statistics.FlowStatusNum;

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
  @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "executionFlow.id", resultType = long.class, before = false)
  int insertAndGetId(@Param("executionFlow") ExecutionFlow executionFlow);

  /**
   * workflow 执行的信息更新 <p>
   *
   * @return 更新记录数
   */
  @UpdateProvider(type = ExecutionFlowMapperProvider.class, method = "update")
  int update(@Param("executionFlow") ExecutionFlow executionFlow);

  /**
   * 查询记录 <p>
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "workId", column = "work_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "submitUser", column = "submit_user", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "submitTime", column = "submit_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "workflowData", column = "workflow_data", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "flowType", column = "flow_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "submitUserName", column = "submit_user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "errorCode", column = "error_code", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),

  })
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "select")
  List<ExecutionFlow> select(@Param("maintainQuery") MaintainQuery maintainQuery);

  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectCount")
  int selectCount(@Param("maintainQuery") MaintainQuery maintainQuery);

  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "workId", column = "work_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "submitUser", column = "submit_user", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "submitTime", column = "submit_time", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "startTime", column = "start_time", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "scheduleTime", column = "schedule_time", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "endTime", column = "end_time", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "workflowData", column = "workflow_data", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "errorCode", column = "error_code", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "queue", column = "queue", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "flowType", column = "flow_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectByExecId")
  ExecutionFlow selectByExecId(@Param("execId") Long execId);

  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "worker", column = "worker", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
  })
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectNoFinishFlow")
  List<ExecutionFlow> selectNoFinishFlow();

  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "workId", column = "work_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "scheduleTime", column = "schedule_time", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectByFlowIdAndTimes")
  List<ExecutionFlow> selectByFlowIdAndTimes(@Param("flowId") Integer flowId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "workId", column = "work_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "scheduleTime", column = "schedule_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectByFlowIdAndTime")
  List<ExecutionFlow> selectByFlowIdAndTime(@Param("flowId") Integer flowId, @Param("scheduleTime") Date scheduleTime);

  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "workId", column = "work_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectNewestExeFlow")
  List<ExecutionFlow> selectNewestExeFlow(@Param("flowIds") Set<Integer> flowIds);

  @DeleteProvider(type = ExecutionFlowMapperProvider.class, method = "deleteByExecId")
  int deleteByExecId(@Param("execId") Long execId);

  /**
   * 查询某个项目执行的workflow 状态记录 <p>
   */
  @Results(value = {@Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectFlowStatus")
  List<FlowStatusNum> selectFlowStatus(@Param("projectId") Integer projectId, @Param("queryDate") Date queryDate);

  /**
   * 查询某个项目的用户执行的workflow 状态记录 <p>
   */
  @Results(value = {@Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectUserFlowStatus")
  List<FlowStatusNum> selectUserFlowStatus(@Param("projectId") Integer projectId, @Param("queryDate") Date queryDate, @Param("userId") int userId);

  /**
   * 查询某个项目每日工作流运行状态(每天趋势) <p>
   */
  @Results(value = {@Result(property = "day", column = "day", javaType = Date.class, jdbcType = JdbcType.DATE),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectDayFlowStatus")
  List<FlowStatusNum> selectDayFlowStatus(@Param("projectId") Integer projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

  /**
   * 查询某个项目每小时工作流运行状态(每小时趋势) <p>
   */
  @Results(value = {@Result(property = "hour", column = "hour", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectHourFlowStatus")
  List<FlowStatusNum> selectHourFlowStatus(@Param("projectId") Integer projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

  /**
   * 查询某个workflow每日平均时间 <p>
   */
  @Results(value = {@Result(property = "day", column = "day", javaType = Date.class, jdbcType = JdbcType.DATE),
          @Result(property = "duration", column = "duration", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectFlowDayAvgTime")
  List<FlowAvgTime> selectFlowDayAvgTime(@Param("flowId") Integer flowId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

  /**
   * 查询某个workflow每小时平均时间 <p>
   */
  @Results(value = {@Result(property = "hour", column = "hour", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "duration", column = "duration", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectFlowHourAvgTime")
  List<FlowAvgTime> selectFlowHourAvgTime(@Param("flowId") Integer flowId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

  /**
   * 查询某个项目工作流耗时排行信息 <p>
   */
  @Results(value = {@Result(property = "execId", column = "id", id = true, javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "flowType", column = "flow_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "submitUser", column = "submit_user", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "submitUserName", column = "submit_user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "duration", column = "duration", javaType = int.class, jdbcType = JdbcType.INTEGER),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectFlowTopTimes")
  List<FlowNodeTimeConsumer> selectFlowTopTimes(@Param("projectId") Integer projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("num") int num);

  /**
   * 查询某个项目工作流和任务出错排行信息 <p>
   */
  @Results(value = {@Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "flowName", column = "flow_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "flowType", column = "flow_type", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "submitUser", column = "submit_user", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "submitUserName", column = "submit_user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "num", column = "num", javaType = int.class, jdbcType = JdbcType.INTEGER),})
  @SelectProvider(type = ExecutionFlowMapperProvider.class, method = "selectFlowErrorNum")
  List<FlowNodeErrorNum> selectFlowErrorNum(@Param("projectId") Integer projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("num") int num);

}
