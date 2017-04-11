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

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.ExecutionNode;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

/**
 * workflow 执行的节点信息操作 <p>
 *
 */
public interface ExecutionNodeMapper {

  /**
   * 插入记录并获取记录 id <p>
   *
   * @return 插入记录数
   */
  @InsertProvider(type = ExecutionNodeMapperProvider.class, method = "insert")
  int insert(@Param("executionNode") ExecutionNode executionNode);

  /**
   * workflow 执执行的节点的信息更新 <p>
   *
   * @return 更新记录数
   */
  @UpdateProvider(type = ExecutionNodeMapperProvider.class, method = "update")
  int update(@Param("executionNode") ExecutionNode executionNode);

  /**
   * 查询单个Node记录 <p>
   *
   * @param execId, nodeId
   */
  @Results(value = {
          @Result(property = "execId", column = "exec_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowStatus.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "attempt", column = "attempt", javaType = int.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "logLinks", column = "log_links", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "jobId", column = "job_id", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectExecNode")
  ExecutionNode selectExecNode(@Param("execId") Long execId, @Param("name") String name);

  /**
   * 根据jobId查询
   * @param jobId
   * @return
   */
  @Results(value = {
          @Result(property = "execId", column = "exec_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowStatus.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "attempt", column = "attempt", javaType = int.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "logLinks", column = "log_links", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "jobId", column = "job_id", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ExecutionNodeMapperProvider.class, method = "selectExecNodeByJobId")
  ExecutionNode selectExecNodeByJobId(@Param("jobId") String jobId);

  @DeleteProvider(type = ExecutionNodeMapperProvider.class, method = "deleteByExecId")
  int deleteByExecId(@Param("execId") Long execId);
}
