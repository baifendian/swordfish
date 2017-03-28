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

import com.baifendian.swordfish.dao.enums.AdHocStatus;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class AdHocMapperProvider {
  /**
   * table name
   */
  private static final String TABLE_NAME = "ad_hoc";

  /**
   * 生成插入语句 <p>
   *
   * @return sql语句
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);
        VALUES("params", "#{adHoc.params}");
        VALUES("proxy_user", "#{adHoc.proxyUser}");
        VALUES("queue", "#{adHoc.queue}");
        VALUES("status", EnumFieldUtil.genFieldStr("adHoc.status", AdHocStatus.class));
        VALUES("create_time", "#{adHoc.createTime}");
        VALUES("owner", "#{adHoc.owner}");
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
        SET("modify_time = #{adHoc.modifyTime}");
        SET("last_modify_by = #{adHoc.lastModifyBy}");
        SET("start_time = #{adHoc.startTime}");
        SET("end_time = #{adHoc.endTime}");
        SET("job_id = #{adHoc.jobId}");
        SET("status = " + EnumFieldUtil.genFieldStr("adHoc.status", AdHocStatus.class));
        WHERE("id = #{adHoc.id}");
      }
    }.toString();
  }

  public String selectById(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("id=#{id}");
      }
    }.toString();
  }

}
