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

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

public interface AdHocMapper {
  /**
   * 插入记录 <p>
   *
   * @return 插入记录数
   */
  @InsertProvider(type = AdHocMapperProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() as id", keyProperty = "adHoc.id", before = false, resultType = Integer.class)
  int insert(@Param("adHocResult") AdHoc adHoc);

  /**
   * 更新记录 <p>
   *
   * @return 更新记录数
   */
  @UpdateProvider(type = AdHocMapperProvider.class, method = "update")
  int update(@Param("adHoc") AdHoc adHoc);

  @SelectProvider(type = AdHocMapperProvider.class, method = "selectById")
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Long.class, jdbcType = JdbcType.BIGINT),
          @Result(property = "params", column = "params", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "queue", column = "queue", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "owner", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "lastModifyBy", column = "last_modify_by", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "jobId", column = "job_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
  })
  AdHoc selectById(@Param("id") Long id);
}
