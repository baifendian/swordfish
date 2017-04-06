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

import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.enums.FlowType;
import com.baifendian.swordfish.dao.enums.NodeType;
import com.baifendian.swordfish.dao.model.FlowNode;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
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
public class FlowNodeMapperSqlProvider {
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

  @SuppressWarnings("unchecked")
  public String insertAll(Map<String, Object> parameter) {
    List<FlowNode> flowNodes = (List<FlowNode>) parameter.get("list");
    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO ");
    sb.append(TABLE_NAME);
    sb.append("(id, name, `desc`, type, flow_id, pos_x, pos_y, param, input_tables, output_tables, create_time, modify_time, last_modify_by) ");
    sb.append("VALUES ");
    String fm = "(#'{'list[{0}].id}, #'{'list[{0}].name}, #'{'list[{0}].desc}, " + EnumFieldUtil.genFieldSpecialStr("list[{0}].type", NodeType.class)
            + ",#'{'list[{0}].flowId}, #'{'list[{0}].posX}, #'{'list[{0}].posY}," + "#'{'list[{0}].param}, #'{'list[{0}].inputTables}, #'{'list[{0}].outputTables},"
            + " #'{'list[{0}].createTime}, #'{'list[{0}].modifyTime}, #'{'list[{0}].lastModifyBy})";
    MessageFormat mf = new MessageFormat(fm);
    for (int i = 0; i < flowNodes.size(); i++) {
      sb.append(mf.format(new Object[]{i}));
      if (i < flowNodes.size() - 1) {
        sb.append(",");
      }
    }
    return sb.toString();
  }

  /**
   * 通过 id 更新 <p>
   *
   * @return sql 语句
   */
  public String updateById(Map<String, Object> parameter) {
    FlowNode flowNode = (FlowNode) parameter.get("flowNode");
    return new SQL() {
      {
        UPDATE(TABLE_NAME);
        if (StringUtils.isNotEmpty(flowNode.getName())) { // 重命名
          SET("name = #{flowNode.name}");
          SET("`desc` = #{flowNode.desc}");
        } else { // 更新参数
          SET("param = #{flowNode.param}");
          SET("input_tables = #{flowNode.inputTables}");
          SET("output_tables = #{flowNode.outputTables}");
        }
        SET("modify_time = #{flowNode.modifyTime}");
        SET("last_modify_by = #{flowNode.lastModifyBy}");

      }
    }.toString();
  }

  /**
   * <p>
   */
  private void appendWhenThenStm(List<FlowNode> flowNodes, StringBuilder sb, Map<String, String> fieldMap) {
    Set<Entry<String, String>> fieldSet = fieldMap.entrySet();
    int dealCount = 0; // 记录处理的字段数目
    for (Entry<String, String> entry : fieldSet) {
      sb.append(entry.getKey());
      sb.append(" = CASE id ");
      String fm = "WHEN #'{'list[{0}].id} THEN #'{'list[{0}]." + entry.getValue() + "} ";
      MessageFormat mf = new MessageFormat(fm);
      for (int i = 0; i < flowNodes.size(); i++) {
        sb.append(mf.format(new Object[]{i}));
      }
      sb.append("END");
      dealCount++;
      if (dealCount < fieldSet.size()) {
        sb.append(",");
      }
    }

    sb.append(" WHERE id IN (");
    // 下面是 id 列表
    StringBuilder idStringBuilder = new StringBuilder();
    for (int i = 0; i < flowNodes.size(); i++) {
      String fm = "#'{'list[{0}].id}";
      MessageFormat mf = new MessageFormat(fm);
      idStringBuilder.append(mf.format(new Object[]{i}));
      if (i < flowNodes.size() - 1) {
        idStringBuilder.append(",");
      }
    }
    sb.append(idStringBuilder);
    sb.append(")");
  }

  /**
   * 批量更新详情 <p>
   *
   * @return sql语句
   */
  @SuppressWarnings("unchecked")
  public String updateAllDetail(Map<String, Object> parameter) {
    List<FlowNode> flowNodes = (List<FlowNode>) parameter.get("list");
    StringBuilder sb = new StringBuilder();
    sb.append("UPDATE ");
    sb.append(TABLE_NAME);
    sb.append(" SET ");

    // 待更新的字段
    Map<String, String> fieldMap = new HashMap<>();
    fieldMap.put("param", "param");
    fieldMap.put("input_tables", "inputTables");
    fieldMap.put("output_tables", "outputTables");
    fieldMap.put("modify_time", "modifyTime");
    fieldMap.put("last_modify_by", "lastModifyBy");

    appendWhenThenStm(flowNodes, sb, fieldMap);
    return sb.toString();
  }

  public String deleteByNodeId(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("id = #{nodeId}");
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

  public String selectByNodeId(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("id = #{nodeId}");
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

  /**
   * 删除项目信息
   */
  public String deleteByProjectId(Map<String, Object> parameter) {
    String subQuery = new SQL() {
      {
        SELECT("id");
        FROM("project_flows");
        WHERE("project_id = #{projectId}");
        WHERE("name = #{name}");
      }
    }.toString();

    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("flow_id in (" + subQuery + ")");
      }
    }.toString();
  }
}
