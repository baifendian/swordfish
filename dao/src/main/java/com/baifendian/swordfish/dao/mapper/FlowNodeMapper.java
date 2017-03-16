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

import com.baifendian.swordfish.dao.enums.FlowType;
import com.baifendian.swordfish.dao.enums.NodeTypeHandler;
import com.baifendian.swordfish.dao.model.FlowNode;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * workflow 节点的相关操作 <p>
 *
 * @author : dsfan
 * @date : 2016年8月27日
 */
public interface FlowNodeMapper {
  /**
   * 插入多条记录 <p>
   *
   * @return 插入记录数
   */
  @InsertProvider(type = FlowNodeMapperSqlProvider.class, method = "insertAll")
  int insertAll(List<FlowNode> flowNodes);

  /**
   * 插入记录 <p>
   *
   * @return 插入记录数
   */
  @InsertProvider(type = FlowNodeMapperSqlProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "flowNode.id", resultType = int.class, before = false)
  int insert(@Param("flowNode") FlowNode flowNode);

  /**
   * 更新记录 <p>
   *
   * @return 更新记录数
   */
  @UpdateProvider(type = FlowNodeMapperSqlProvider.class, method = "updateById")
  int updateById(@Param("flowNode") FlowNode flowNode);

  /**
   * 更新多条记录(更新pos信息) <p>
   *
   * @return 更新记录数
   */
  @UpdateProvider(type = FlowNodeMapperSqlProvider.class, method = "updateAllPos")
  int updateAllPos(List<FlowNode> flowNodes);

  /**
   * 更新多条记录(更新参数等详情信息) <p>
   *
   * @return 更新记录数
   */
  @UpdateProvider(type = FlowNodeMapperSqlProvider.class, method = "updateAllDetail")
  int updateAllDetail(List<FlowNode> flowNodes);

  /**
   * 删除 workflow 节点 <p>
   *
   * @return 删除记录数
   */
  @DeleteProvider(type = FlowNodeMapperSqlProvider.class, method = "deleteByNodeId")
  int deleteByNodeId(@Param("nodeId") int nodeId);

  /**
   * 删除 workflow <p>
   *
   * @return 删除记录数
   */
  @DeleteProvider(type = FlowNodeMapperSqlProvider.class, method = "deleteByFlowId")
  int deleteByFlowId(@Param("flowId") int flowId);

  /**
   * 查询记录 <p>
   *
   * @return workflow 节点详情
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", typeHandler = NodeTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "flowId", column = "flow_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "posX", column = "pos_x", javaType = double.class, jdbcType = JdbcType.DOUBLE),
          @Result(property = "posY", column = "pos_y", javaType = double.class, jdbcType = JdbcType.DOUBLE),
          @Result(property = "param", column = "param", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "inputTables", column = "input_tables", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "outputTables", column = "output_tables", javaType = String.class, jdbcType = JdbcType.VARCHAR),})
  @SelectProvider(type = FlowNodeMapperSqlProvider.class, method = "selectByNodeId")
  FlowNode selectByNodeId(@Param("nodeId") int nodeId);

  /**
   * 查询记录 <p>
   *
   * @return workflow 节点列表
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", typeHandler = NodeTypeHandler.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "flowId", column = "flow_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "posX", column = "pos_x", javaType = double.class, jdbcType = JdbcType.DOUBLE),
          @Result(property = "posY", column = "pos_y", javaType = double.class, jdbcType = JdbcType.DOUBLE),
          @Result(property = "param", column = "param", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "inputTables", column = "input_tables", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "outputTables", column = "output_tables", javaType = String.class, jdbcType = JdbcType.VARCHAR),})
  @SelectProvider(type = FlowNodeMapperSqlProvider.class, method = "selectByFlowId")
  List<FlowNode> selectByFlowId(@Param("flowId") int flowId);

  /**
   * 查询一个组织里面的Node数量 <p>
   *
   * @return 查询记录数
   */
  @SelectProvider(type = FlowNodeMapperSqlProvider.class, method = "queryNodeNum")
  int queryNodeNum(@Param("tenantId") int tenantId, @Param("flowTypes") List<FlowType> flowTypes);

  /**
   * 删除项目的结点信息
   */
  @DeleteProvider(type = FlowNodeMapperSqlProvider.class, method = "deleteByProjectId")
  int deleteByProjectId(@Param("projectId") int projectId, @Param("flowType") FlowType flowType);
}
