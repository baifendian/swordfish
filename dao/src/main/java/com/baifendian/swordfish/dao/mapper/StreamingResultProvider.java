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

public class StreamingResultProvider {

  private static final String TABLE_NAME = "streaming_result";

  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);

        VALUES("`streaming_id`", "#{result.streamingId}");
        VALUES("`parameter`", "#{result.parameter}");
        VALUES("`user_defined_params`", "#{result.userDefinedParams}");
        VALUES("`submit_user`", "#{result.submitUserId}");
        VALUES("`submit_time`", "#{result.submitTime}");
        VALUES("`queue`", "#{result.queue}");
        VALUES("`proxy_user`", "#{result.proxyUser}");
        VALUES("`schedule_time`", "#{result.scheduleTime}");
        VALUES("`start_time`", "#{result.startTime}");
        VALUES("`end_time`", "#{result.endTime}");
        VALUES("`status`", EnumFieldUtil.genFieldStr("result.status", FlowStatus.class));
        VALUES("`app_links`", "#{result.appLinks}");
        VALUES("`job_links`", "#{result.jobLinks}");
        VALUES("`job_id`", "#{result.jobId}");
      }
    }.toString();
  }

  /**
   * 根据执行 id 查询执行结果
   *
   * @param parameter
   * @return
   */
  public String selectById(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");

        FROM(TABLE_NAME);

        WHERE("id=#{execId}");
      }
    }.toString();
  }

  /**
   * 查询某流任务最新的一条结果记录
   * for example: select * from table where id=(select max(id) from table where field=xxx limit 1);
   *
   * @param parameter
   * @return
   */
  public String findLatestDetailByStreamingId(Map<String, Object> parameter) {
    String subSql = new SQL() {
      {
        SELECT("max(id)");

        FROM(TABLE_NAME);

        WHERE("streaming_id = #{streamingId}");
      }
    }.toString() + " limit 1";

    return new SQL() {
      {
        SELECT("r.submit_user as submit_user_id");
        SELECT("s.owner as owner_id");
        SELECT("u2.name as submit_user_name");
        SELECT("u1.name as owner_name");
        SELECT("p.name as project_name");
        SELECT("r.*");

        FROM(TABLE_NAME + " as r");

        JOIN("streaming_job s on r.streaming_id = s.id");
        JOIN("project p on s.project_id = p.id");
        JOIN("user u1 on s.owner = u1.id");
        JOIN("user u2 on r.submit_user = u2.id");

        WHERE("r.id=" + "(" + subSql + ")");
      }
    }.toString();
  }

  /**
   * 查找没有完成的 job
   *
   * @param parameter
   * @return
   */
  public String findNoFinishedJob(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");

        FROM(TABLE_NAME);

        WHERE("status <= " + FlowStatus.RUNNING.ordinal());
      }
    }.toString();
  }

  /**
   * 通过 id 更新
   *
   * @param parameter
   * @return sql 语句
   */
  public String updateResult(Map<String, Object> parameter) {
    return new SQL() {
      {
        UPDATE(TABLE_NAME);

        SET("`parameter` = #{job.parameter}");
        SET("`user_defined_params` = #{job.userDefinedParams}");
        SET("`submit_user` = #{job.submitUserId}");
        SET("`submit_time` = #{job.submitTime}");
        SET("`queue` = #{job.queue}");
        SET("`proxy_user` = #{job.proxyUser}");
        SET("`schedule_time` = #{job.scheduleTime}");
        SET("`start_time` = #{job.startTime}");
        SET("`end_time` = #{job.endTime}");
        SET("`status` = " + EnumFieldUtil.genFieldStr("job.status", FlowStatus.class));
        SET("`app_links` = #{job.appLinks}");
        SET("`job_links` = #{job.jobLinks}");
        SET("`job_id` = #{job.jobId}");

        WHERE("id = #{job.execId}");
      }
    }.toString();
  }
}
