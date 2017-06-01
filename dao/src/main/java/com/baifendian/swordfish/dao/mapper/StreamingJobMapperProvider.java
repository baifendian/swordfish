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

import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class StreamingJobMapperProvider {

  private static final String TABLE_NAME = "streaming_job";

  /**
   * 插入一条流式的记录到数据库
   *
   * @param parameter
   * @return
   */
  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);

        VALUES("`name`", "#{job.name}");
        VALUES("`desc`", "#{job.desc}");
        VALUES("`project_id`", "#{job.projectId}");
        VALUES("`create_time`", "#{job.createTime}");
        VALUES("`modify_time`", "#{job.modifyTime}");
        VALUES("`owner`", "#{job.ownerId}");
        VALUES("`type`", "#{job.type}");
        VALUES("`parameter`", "#{job.parameter}");
        VALUES("`user_defined_params`", "#{job.userDefinedParams}");
        VALUES("`notify_type`", EnumFieldUtil.genFieldStr("job.notifyType", NotifyType.class));
        VALUES("`notify_mails`", "#{job.notifyMails}");
      }
    }.toString();
  }

  /**
   * 根据项目名称和流任务名称查询
   *
   * @param parameter
   * @return
   */
  public String findByProjectNameAndName(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("s.owner as owner_id");
        SELECT("p.name as project_name");
        SELECT("u.name as owner_name");
        SELECT("s.*");

        FROM(TABLE_NAME + " s");

        JOIN("project p on s.project_id = p.id");
        JOIN("user u on s.owner = u.id");

        WHERE("p.name = #{projectName}");
        WHERE("s.name = #{name}");
      }
    }.toString();
  }

  /**
   * 更新流任务信息
   *
   * @param parameter
   * @return
   */
  public String updateStreamingJob(Map<String, Object> parameter) {
    return new SQL() {
      {
        UPDATE(TABLE_NAME);

        SET("`desc`=#{job.desc}");
        SET("`modify_time`=#{job.modifyTime}");
        SET("`owner`=#{job.ownerId}");
        SET("`parameter`=#{job.parameter}");
        SET("`user_defined_params`=#{job.userDefinedParams}");
        SET("`notify_type`=" + EnumFieldUtil.genFieldStr("job.notifyType", NotifyType.class));
        SET("`notify_mails`=#{job.notifyMails}");

        WHERE("id = #{job.id}");
      }
    }.toString();
  }

  /**
   * 删除流任务
   *
   * @param parameter
   * @return
   */
  public String deleteById(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);

        WHERE("id = #{id}");
      }
    }.toString();
  }

  /**
   * 查询项目的流任务个数
   *
   * @param parameter
   * @return
   */
  public String selectProjectStreamingCount(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("count(0)");

        FROM(TABLE_NAME);

        WHERE("project_id = #{projectId}");
      }
    }.toString();
  }

  /**
   * 根据项目查询流任务
   *
   * @param parameter
   * @return
   */
  public String queryProjectStreamingJobs(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("s.owner as owner_id");
        SELECT("p.name as project_name");
        SELECT("u.name as owner_name");
        SELECT("s.*");

        FROM(TABLE_NAME + " s");

        JOIN("project p on s.project_id = p.id");
        JOIN("user u on s.owner = u.id");

        WHERE("s.project_id = #{projectId}");
      }
    }.toString();
  }
}
