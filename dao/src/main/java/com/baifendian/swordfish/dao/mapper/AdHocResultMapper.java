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
import com.baifendian.swordfish.dao.model.AdHocResult;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * 即席查询执行结果的操作 <p>
 */
public interface AdHocResultMapper {
  /**
   * 插入记录 <p>
   *
   * @return 插入记录数
   */
  @InsertProvider(type = AdHocResultMapperProvider.class, method = "insert")
  int insert(@Param("adHocResult") AdHocResult adHocResult);

  /**
   * 更新记录 <p>
   *
   * @return 更新记录数
   */
  @UpdateProvider(type = AdHocResultMapperProvider.class, method = "update")
  int update(@Param("adHocResult") AdHocResult adHocResult);

  @DeleteProvider(type = AdHocResultMapperProvider.class, method = "delete")
  int delete(@Param("adHocId") Long adHocId);

  /**
   * 查询即席查询的执行结果(通过 adHocId ) <p>
   *
   * @return 执行结果
   */
  @Results(value = {
          @Result(property = "adHocId", column = "exec_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "index", column = "index", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowStatus.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "stm", column = "stm", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "result", column = "result", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
  @SelectProvider(type = AdHocResultMapperProvider.class, method = "selectByAdHocId")
  List<AdHocResult> selectByAdHocId(@Param("adHocId") long adHocId);

  @Results(value = {
          @Result(property = "adHocId", column = "exec_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "index", column = "index", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowStatus.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "stm", column = "stm", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "result", column = "result", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
  @SelectProvider(type = AdHocResultMapperProvider.class, method = "selectByAdHocIdAndIndex")
  List<AdHocResult> selectByAdHocIdAndIndex(@Param("adHocId") long adHocId, @Param("index") int index);
}
