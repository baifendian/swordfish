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

public class StreamingResultProvider {

  private static final String TABLE_NAME = "streaming_result";

  /**
   * find by id, but not join
   *
   * @param parameter
   * @return
   */
  public String findByIdNoJoin(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("s.submit_user as submit_user_id");
        SELECT("u.name as submit_user_name");
        SELECT("s.*");

        FROM(TABLE_NAME + " s");

        JOIN("user u on s.submit_user = u.id");

        WHERE("s.id = #{id}");
      }
    }.toString();
  }
}
