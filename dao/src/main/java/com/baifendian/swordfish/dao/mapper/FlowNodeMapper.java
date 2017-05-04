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
  @InsertProvider(type = FlowNodeMapperProvider.class, method = "insertAll")
  int insertAll(List<FlowNode> flowNodes);

  /**
   * 插入记录 <p>
   *
   * @return 插入记录数
   */
  @InsertProvider(type = FlowNodeMapperProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "flowNode.id", resultType = int.class, before = false)
  int insert(@Param("flowNode") FlowNode flowNode);

  /**
   * 删除 workflow <p>
   *
   * @return 删除记录数
   */
  @DeleteProvider(type = FlowNodeMapperProvider.class, method = "deleteByFlowId")
  int deleteByFlowId(@Param("flowId") int flowId);

  /**
   * 查询记录
   * <p>
   *
   * @param nodeId
   * @return workflow 节点详情
   */
  @Results(value = { @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "flowId", column = "flow_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "parameter", column = "parameter", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "extras", column = "extras", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dep", column = "dep", javaType = String.class, jdbcType = JdbcType.VARCHAR), })
  @SelectProvider(type = FlowNodeMapperProvider.class, method = "selectByNodeId")
  FlowNode selectByNodeId(@Param("nodeId") int nodeId);

  /**
   * 查询记录 <p>
   *
   * @return workflow 节点列表
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "extras", column = "extras", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dep", column = "dep", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "flowId", column = "flow_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "parameter", column = "parameter", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          })
  @SelectProvider(type = FlowNodeMapperProvider.class, method = "selectByFlowId")
  List<FlowNode> selectByFlowId(@Param("flowId") int flowId);

  /**
   * 根据多个ID查看flowNode
   * @param flowIds
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "type", column = "type", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "extras", column = "extras", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dep", column = "dep", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "flowId", column = "flow_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "parameter", column = "parameter", javaType = String.class, jdbcType = JdbcType.VARCHAR),
  })
  @Select("<script> select * from flows_nodes where flow_id in  <foreach item=\"flowId\" index=\"index\" collection=\"flowIds\" open=\"(\" separator=\",\" close=\")\"> #{flowId} </foreach> </script>")
  List<FlowNode> selectByFlowIds(@Param("flowIds") List<Integer> flowIds);

}
