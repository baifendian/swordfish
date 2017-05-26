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
import com.baifendian.swordfish.dao.model.StreamingResult;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.List;

public interface StreamingResultMapper {
//
//  /**
//   * 插入记录 id <p>
//   *
//   * @param result
//   * @return 插入记录数
//   */
//  @InsertProvider(type = StreamingResultProvider.class, method = "insert")
//  int insert(@Param("result") StreamingResult result);

//  /**
//   * 根据流 id 查询结果信息, 会有连接, 查询的是详情
//   *
//   * @param id
//   * @return
//   */
//  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
//      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
//      @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "type", column = "type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "parameter", column = "parameter", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "userDefinedParams", column = "user_defined_params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "extras", column = "extras", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//  })
//  @SelectProvider(type = StreamingResultProvider.class, method = "findById")
//  StreamingResult findById(@Param("id") int id);

  /**
   * 插入记录并获取记录 id <p>
   *
   * @param result
   * @return 插入记录数
   */
  @InsertProvider(type = StreamingResultProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "result.id", resultType = int.class, before = false)
  int insert(@Param("result") StreamingResult result);

  /**
   * 根据流 id 查询结果信息, 查询的是简单的信息
   *
   * @param streamingId
   * @return
   */
  @Results(value = {@Result(property = "execId", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "streamingId", column = "streaming_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "parameter", column = "parameter", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "userDefinedParams", column = "user_defined_params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "queue", column = "queue", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "ownerId", column = "owner_id", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "type", column = "type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "submitTime", column = "submit_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "startTime", column = "start_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "endTime", column = "end_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "submitUserId", column = "submit_user_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "submitUser", column = "submit_user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowStatus.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "appLinks", column = "app_links", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "jobLinks", column = "job_links", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "jobId", column = "job_id", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = StreamingResultProvider.class, method = "findLatestByStreamingId")
  StreamingResult findLatestByStreamingId(@Param("streamingId") int streamingId);

  /**
   * 查询没有完成的流任务
   *
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "streamingId", column = "streaming_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "submitUserId", column = "submit_user_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "submitTime", column = "submit_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "queue", column = "queue", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "scheduleTime", column = "schedule_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "startTime", column = "start_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "endTime", column = "end_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowStatus.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "logLinks", column = "log_links", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "jobId", column = "job_id", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = StreamingResultProvider.class, method = "findNoFinishedJob")
  List<StreamingResult> findNoFinishedJob();

  /**
   * 更新任务信息
   *
   * @param job
   * @return
   */
  @UpdateProvider(type = StreamingResultProvider.class, method = "updateResult")
  int updateResult(@Param("job") StreamingResult job);
}
