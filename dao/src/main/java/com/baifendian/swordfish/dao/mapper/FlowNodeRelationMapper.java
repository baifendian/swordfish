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

import com.baifendian.swordfish.dao.model.FlowNodeRelation;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * workflow 节点关系表
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月29日
 */
public interface FlowNodeRelationMapper {
    /**
     * 插入多条记录
     * <p>
     *
     * @param flowNodeRelations
     * @return 插入记录数
     */
    @InsertProvider(type = FlowNodeRelationMapperProvider.class, method = "insertAll")
    int insertAll(List<FlowNodeRelation> flowNodeRelations);

    /**
     * 插入记录
     * <p>
     *
     * @param flowNodeRelation
     * @return 插入记录数
     */
    @InsertProvider(type = FlowNodeRelationMapperProvider.class, method = "insert")
    int insert(@Param("flowNodeRelation") FlowNodeRelation flowNodeRelation);

    /**
     * 删除记录
     * <p>
     *
     * @param flowId
     * @return 删除记录数
     */
    @DeleteProvider(type = FlowNodeRelationMapperProvider.class, method = "deleteByFlowId")
    int deleteByFlowId(@Param("flowId") int flowId);

    /**
     * 查询记录
     * <p>
     *
     * @param flowId
     * @return
     */
    @Results(value = { @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "startId", column = "start_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "endId", column = "end_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "attribute", column = "attribute", javaType = String.class, jdbcType = JdbcType.VARCHAR) })
    @SelectProvider(type = FlowNodeRelationMapperProvider.class, method = "selectByFlowId")
    List<FlowNodeRelation> selectByFlowId(@Param("flowId") int flowId);

    /**
     * 删除节点相关的边
     * <p>
     *
     * @param workflowId
     * @param nodeId
     */
    @DeleteProvider(type = FlowNodeRelationMapperProvider.class, method = "deleteByFlowIdAndNodeId")
    void deleteByFlowIdAndNodeId(@Param("flowId") Integer workflowId, @Param("nodeId") Integer nodeId);
}
