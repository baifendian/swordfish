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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
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
        SELECT("submit_user as submit_user_id");
        SELECT("r.*");

        FROM(TABLE_NAME + " r");

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

    return constructCommonDetailSQL().
        WHERE("r.id=" + "(" + subSql + ")").
        toString();
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
        SELECT("submit_user as submit_user_id");
        SELECT("r.*");

        FROM(TABLE_NAME + " as r");

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

  /**
   * 查询项目的 id
   *
   * @param parameter
   * @return
   */
  public String queryProject(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("p.*");

        FROM("project p");

        JOIN("streaming_job s on p.id = s.project_id");
        JOIN(TABLE_NAME + " as r on s.id = r.streaming_id");

        WHERE("r.id=#{execId}");
      }
    }.toString();
  }

  /**
   * 根据执行 id 查询详细信息
   *
   * @param parameter
   * @return
   */
  public String findDetailByExecId(Map<String, Object> parameter) {
    return constructCommonDetailSQL().
        WHERE("r.id=#{execId}")
        .toString();
  }

  /**
   * 根据项目和名称查询
   *
   * @param parameter
   * @return
   */
  public String findDetailByProjectAndNames(Map<String, Object> parameter) {
    List<String> nameList = (List<String>) parameter.get("nameList");

    if (CollectionUtils.isEmpty(nameList)) {
      return constructCommonDetailSQL().
          WHERE("p.id=#{projectId}")
          .toString();
    }

    String names = "\"" + String.join("\", \"", nameList) + "\"";

    return constructCommonDetailSQL().
        WHERE("p.id=#{projectId}").
        WHERE("s.name in (" + names + ")")
        .toString();
  }

  /**
   * 根据多个条件一起组合查询
   *
   * @param parameter
   * @return
   */
  public String findByMultiCondition(Map<String, Object> parameter) {
    Integer status = (Integer) parameter.get("status");
    String name = (String) parameter.get("name");

    SQL sql = constructCommonDetailSQL().
        WHERE("s.project_id=#{projectId}").
        WHERE("schedule_time >= #{startDate}").
        WHERE("schedule_time < #{endDate}");

    if (StringUtils.isNotEmpty(name)) {
      sql = sql.WHERE("name = #{name}");
    }

    if (status != null) {
      sql = sql.WHERE("`status`=#{status}");
    }

    String subClause = sql.toString();

    return new SQL() {
      {
        SELECT("*");

        FROM("(" + subClause + ") e_f");
      }
    }.toString() + " order by schedule_time DESC limit #{start}, #{limit}";
  }

  /**
   * 查询数目
   *
   * @param parameter
   * @return
   */
  public String findCountByMultiCondition(Map<String, Object> parameter) {
    Integer status = (Integer) parameter.get("status");
    String name = (String) parameter.get("name");

    return new SQL() {
      {
        SELECT("count(0)");

        FROM(TABLE_NAME + " r");

        JOIN("streaming_job s on r.streaming_id = s.id");

        WHERE("s.project_id=#{projectId}");
        WHERE("schedule_time >= #{startDate}");
        WHERE("schedule_time < #{endDate}");

        if (StringUtils.isNotEmpty(name)) {
          WHERE("name = #{name}");
        }

        if (status != null) {
          WHERE("`status`=#{status}");
        }
      }
    }.toString();
  }

  /**
   * 构造一个比较通用的 sql, 主要做了详细的关联
   *
   * @return
   */
  private SQL constructCommonDetailSQL() {
    return new SQL() {{
      SELECT("r.submit_user as submit_user_id");
      SELECT("s.owner as owner_id");
      SELECT("s.name");
      SELECT("s.`desc`");
      SELECT("s.create_time");
      SELECT("s.modify_time");
      SELECT("s.type");
      SELECT("u2.name as submit_user_name");
      SELECT("u1.name as owner_name");
      SELECT("p.name as project_name");
      SELECT("r.*");

      FROM(TABLE_NAME + " as r");

      JOIN("streaming_job s on r.streaming_id = s.id");
      JOIN("project p on s.project_id = p.id");
      JOIN("user u1 on s.owner = u1.id");
      JOIN("user u2 on r.submit_user = u2.id");
    }};
  }
}
