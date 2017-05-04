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

import com.baifendian.swordfish.dao.enums.NodeType;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.model.FlowNode;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * workflow 节点表的 sql 生成器 <p>
 *
 * @author : dsfan
 * @date : 2016年8月27日
 */
public class FlowNodeMapperProvider {
  /**
   * 表名
   */
  public static final String TABLE_NAME = "flows_nodes";

  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);
        VALUES("name", "#{flowNode.name}");
        VALUES("`desc`", "#{flowNode.desc}");
        VALUES("type", "#{flowNode.type}");
        VALUES("flow_id", "#{flowNode.flowId}");
        VALUES("parameter", "#{flowNode.parameter}");
        VALUES("extras", "#{flowNode.extras}");
        VALUES("dep", "#{flowNode.dep}");
      }
    }.toString();
  }

  public String deleteByFlowId(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("flow_id = #{flowId}");
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
