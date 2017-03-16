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

import com.baifendian.swordfish.dao.model.ExecNodeLog;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

/**
 * workflow 执行的节点日志信息操作 <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月30日
 */
public interface ExecNodeLogMapper {

  /**
   * 插入记录 <p>
   *
   * @return 插入记录数
   */
  @InsertProvider(type = ExecNodeLogMapperProvider.class, method = "insert")
  int insert(@Param("execNodeLog") ExecNodeLog execNodeLog);

  /**
   * 查询记录 <p>
   */
  @Results(value = {@Result(property = "logId", column = "log_id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "logBytes", column = "log_info", javaType = byte[].class, jdbcType = JdbcType.BLOB),
          @Result(property = "startByte", column = "start_byte", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "endByte", column = "end_byte", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "uploadTime", column = "upload_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),})
  @SelectProvider(type = ExecNodeLogMapperProvider.class, method = "select")
  List<ExecNodeLog> select(@Param("logId") Long logId);

  @Results(value = {@Result(property = "logId", column = "log_id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "logBytes", column = "log_info", javaType = byte[].class, jdbcType = JdbcType.BLOB),
          @Result(property = "startByte", column = "start_byte", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "endByte", column = "end_byte", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "uploadTime", column = "upload_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),})
  @SelectProvider(type = ExecNodeLogMapperProvider.class, method = "selectPagination")
  List<ExecNodeLog> selectPagination(@Param("logId") Long logId, @Param("start") int start, @Param("length") int length);

  @SelectProvider(type = ExecNodeLogMapperProvider.class, method = "selectCount")
  int selectCount(@Param("logId") long logId);

  @DeleteProvider(type = ExecNodeLogMapperProvider.class, method = "deleteByLogId")
  int deleteByLogId(@Param("logId") Long logId);
}
