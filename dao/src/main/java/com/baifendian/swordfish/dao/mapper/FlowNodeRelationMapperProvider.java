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

import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * workflow 节点关系表 sql 生成器 <p>
 *
 * @author : dsfan
 * @date : 2016年8月29日
 */
public class FlowNodeRelationMapperProvider {

  /**
   * 表名
   */
  public static final String TABLE_NAME = "flows_nodes_relation";

  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);
        VALUES("flow_id", "#{flowNodeRelation.flowId}");
        VALUES("start_id", "#{flowNodeRelation.startId}");
        VALUES("end_id", "#{flowNodeRelation.endId}");
        VALUES("attribute", "#{flowNodeRelation.attribute}");
      }
    }.toString();
  }

  public String insertAll(Map<String, Object> parameter) {
    @SuppressWarnings("unchecked")
    List<FlowNodeRelation> flowNodeRelations = (List<FlowNodeRelation>) parameter.get("list");
    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO ");
    sb.append(TABLE_NAME);
    sb.append("(flow_id, start_id, end_id, attribute) ");
    sb.append("VALUES ");
    MessageFormat mf = new MessageFormat("(#'{'list[{0}].flowId}, #'{'list[{0}].startId}, #'{'list[{0}].endId}, #'{'list[{0}].attribute})");
    for (int i = 0; i < flowNodeRelations.size(); i++) {
      sb.append(mf.format(new Object[]{i}));
      if (i < flowNodeRelations.size() - 1) {
        sb.append(",");
      }
    }
    return sb.toString();
  }

  public String deleteByFlowId(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("flow_id = #{flowId}");
      }
    }.toString();
  }

  public String deleteByFlowIdAndNodeId(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("flow_id = #{flowId}");
        AND();
        WHERE("start_id = #{nodeId} or end_id = #{nodeId}");
      }
    }.toString();
  }

  public String selectByFlowId(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("flow_id = #{flowId}");
      }
    }.toString();
  }
}
