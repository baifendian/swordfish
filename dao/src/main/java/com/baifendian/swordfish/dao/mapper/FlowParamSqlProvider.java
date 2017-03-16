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

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * flows_params 表的 sql 生成器 <p>
 *
 * @author : dsfan
 * @date : 2016年10月12日
 */
public class FlowParamSqlProvider {

  /**
   * 表名
   */
  public static final String TABLE_NAME = "flows_params";

  /**
   * 插入 <p>
   *
   * @return sql 语句
   */
  public String insert(Map<String, Object> parameter) {
    // return new SQL() {
    // {
    // INSERT_INTO(TABLE_NAME);
    // VALUES("flow_id", "#{flowParam.flowId}");
    // VALUES("key", "#{flowParam.key}");
    // VALUES("value", "#{flowParam.value}");
    // }
    // }.toString();

    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO ");
    sb.append(TABLE_NAME);
    sb.append("(flow_id, `key`, `value`) ");
    sb.append("VALUES ");
    sb.append("(");
    sb.append("#{flowParam.flowId}");
    sb.append(",");
    sb.append("#{flowParam.key}");
    sb.append(",");
    sb.append("#{flowParam.value}");
    sb.append(")");

    sb.append(" ON DUPLICATE KEY UPDATE value=VALUES(`value`)");
    return sb.toString();
  }

  /**
   * 删除 flow id 参数配置
   */
  public String deleteByFlowId(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("flow_id = #{flowId}");
      }
    }.toString();
  }

  /**
   * 删除 <p>
   *
   * @return sql 语句
   */
  public String deleteByFlowIdAndKey(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("flow_id = #{flowId}");
        WHERE("`key` = #{key}");
      }
    }.toString();
  }

  /**
   * 查询某个 workflow 的自定义参数 <p>
   *
   * @return sql 语句
   */
  public String queryAllByFlowId(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("flow_id = #{flowId}");
      }
    }.toString();
  }

}
