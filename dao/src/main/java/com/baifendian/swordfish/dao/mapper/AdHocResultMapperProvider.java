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
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * 即席查询结果 sql 生成器 <p>
 */
public class AdHocResultMapperProvider {
  /**
   * table name
   */
  private static final String TABLE_NAME = "ad_hoc_results";

  /**
   * 生成插入语句 <p>
   *
   * @return sql语句
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);
        VALUES("exec_id", "#{adHocResult.execId}");
        VALUES("`index`", "#{adHocResult.index}");
        VALUES("stm", "#{adHocResult.stm}");
        VALUES("result", "#{adHocResult.result}");
        VALUES("status", EnumFieldUtil.genFieldStr("adHocResult.status", FlowStatus.class));
        VALUES("create_time", "#{adHocResult.createTime}");
      }
    }.toString();
  }

  /**
   * 生成更新语句 <p>
   *
   * @return sql语句
   */
  public String update(Map<String, Object> parameter) {
    return new SQL() {
      {
        UPDATE(TABLE_NAME);
        SET("result = #{adHocResult.result}");
        SET("start_time = #{adHocResult.startTime}");
        SET("end_time = #{adHocResult.endTime}");
        SET("status = " + EnumFieldUtil.genFieldStr("adHocResult.status", FlowStatus.class));
        WHERE("exec_id = #{adHocResult.execId}");
        WHERE("`index` = #{adHocResult.index}");
      }
    }.toString();
  }

  public String delete(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
      }
    }.toString();
  }

  /**
   * 生成查询语句 <p>
   *
   * @return sql语句
   */
  public String selectByAdHocId(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
        ORDER_BY("create_time DESC ");
      }
    }.toString();
  }

  public String selectByAdHocIdAndIndex(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
        WHERE("`index` = #{index}");
        ORDER_BY("create_time DESC limit 1");
      }
    }.toString();
  }

  public static void main(String[] args) {
  }
}
