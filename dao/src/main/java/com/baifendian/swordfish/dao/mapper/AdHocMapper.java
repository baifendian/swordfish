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

import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.dao.model.AdHocResult;
import com.baifendian.swordfish.dao.model.Project;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

public interface AdHocMapper {

  /**
   * 插入记录
   *
   * @param adHoc
   * @return 插入记录数
   */
  @InsertProvider(type = AdHocMapperProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() as id", keyProperty = "adHoc.id", before = false, resultType = Integer.class)
  int insert(@Param("adHoc") AdHoc adHoc);

  /**
   * 根据执行 id 查询项目的信息
   *
   * @param execId
   * @return
   */
  @SelectProvider(type = AdHocMapperProvider.class, method = "selectProjectByExecId")
  Project queryProjectByExecId(@Param("execId") int execId);

  /**
   * 更新记录 <p>
   *
   * @return 更新记录数
   */
  @UpdateProvider(type = AdHocMapperProvider.class, method = "update")
  int update(@Param("adHoc") AdHoc adHoc);

  /**
   * 根据 id 查询
   *
   * @param id
   * @return
   */
  @Results(value = {
      @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectId", column = "project_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "queue", column = "queue", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "owner", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "parameter", column = "parameter", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "jobId", column = "job_id", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = AdHocMapperProvider.class, method = "selectById")
  AdHoc selectById(@Param("id") int id);

  /**
   * 根据执行 id 查询结果
   *
   * @param execId
   * @return
   */
  @Results(value = {
      @Result(property = "id", column = "exec_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectId", column = "index", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "proxyUser", column = "stm", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "queue", column = "result", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "owner", column = "create_time", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "parameter", column = "start_time", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "parameter", column = "end_time", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = AdHocMapperProvider.class, method = "selectResultById")
  List<AdHocResult> selectResultById(@Param("execId") int execId);

  /**
   * search by exec id and index
   *
   * @param execId
   * @param index
   * @return
   */
  @Results(value = {
      @Result(property = "id", column = "exec_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "projectId", column = "index", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "proxyUser", column = "stm", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "queue", column = "result", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "owner", column = "create_time", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "parameter", column = "start_time", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "parameter", column = "end_time", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = AdHocMapperProvider.class, method = "selectResultByIdAndIndex")
  AdHocResult selectResultByIdAndIndex(@Param("execId") int execId, @Param("index") int index);
}
