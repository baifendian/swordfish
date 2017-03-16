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

import com.baifendian.swordfish.dao.model.FlowParam;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * workflow 参数信息的操作 <p>
 *
 * @author : dsfan
 * @date : 2016年10月12日
 */
public interface FlowParamMapper {
  /**
   * 插入记录 <p>
   *
   * @return 修改记录数
   */
  @InsertProvider(type = FlowParamSqlProvider.class, method = "insert")
  int insertAndGetId(@Param("flowParam") FlowParam flowParam);

  /**
   * 删除记录 <p>
   *
   * @return 修改记录数
   */
  @DeleteProvider(type = FlowParamSqlProvider.class, method = "deleteByFlowId")
  int deleteByFlowId(@Param("flowId") int flowId);

  /**
   * 删除记录 <p>
   *
   * @return 修改记录数
   */
  @DeleteProvider(type = FlowParamSqlProvider.class, method = "deleteByFlowIdAndKey")
  int deleteByFlowIdAndKey(@Param("flowId") int flowId, @Param("key") String key);

  /**
   * 查询 flowId 下的所有参数 <p>
   *
   * @return FlowParam 列表
   */
  @Results(value = {@Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "key", column = "key", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "value", column = "value", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
  @SelectProvider(type = FlowParamSqlProvider.class, method = "queryAllByFlowId")
  List<FlowParam> queryAllByFlowId(@Param("flowId") int flowId);

}
