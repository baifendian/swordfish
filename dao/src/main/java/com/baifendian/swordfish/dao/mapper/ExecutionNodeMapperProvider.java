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
import com.baifendian.swordfish.dao.enums.FlowType;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.model.ExecutionNode;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * workflow 执行的节点信息操作 <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月30日
 */
public class ExecutionNodeMapperProvider {

  public static final String TABLE_NAME = "execution_nodes";

  List<Integer> flowTypes = new ArrayList<>();

  public ExecutionNodeMapperProvider() {
    flowTypes.add(FlowType.LONG.getType());
    flowTypes.add(FlowType.SHORT.getType());
    flowTypes.add(FlowType.ETL.getType());
  }

  public String insert(Map<String, Object> parameter) {
    return new SQL() {
      {
        INSERT_INTO(TABLE_NAME);
        VALUES("exec_id", "#{executionNode.execId}");
        VALUES("name", "#{executionNode.name}");
        VALUES("start_time", "#{executionNode.startTime}");
        VALUES("end_time", "#{executionNode.endTime}");
        VALUES("attempt", "#{executionNode.attempt}");
        VALUES("log_links", "#{executionNode.logLinks}");
        VALUES("job_id", "#{executionNode.jobId}");
        VALUES("status", EnumFieldUtil.genFieldStr("executionNode.status", FlowStatus.class));
      }
    }.toString();
  }

  public String update(Map<String, Object> parameter) {
    ExecutionNode executionNode = (ExecutionNode) parameter.get("executionNode");
    return new SQL() {
      {
        UPDATE(TABLE_NAME);
        if (executionNode.getAttempt() != null) {
          SET("attempt = #{executionNode.attempt}");
        }
        if (executionNode.getEndTime() != null) {
          SET("end_time = #{executionNode.endTime}");
        }
        if (executionNode.getStatus() != null) {
          SET("status = " + EnumFieldUtil.genFieldStr("executionNode.status", FlowStatus.class));
        }
        if (executionNode.getLogLinks() != null) {
          SET("log_links = #{executionNode.logLinks}");
        }
        WHERE("exec_id = #{executionNode.execId}");
        WHERE("name = #{executionNode.name}");
      }
    }.toString();
  }


  public String selectExecNode(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
        WHERE("name = #{name}");
      }
    }.toString();
  }

  public String selectExecNodeById(Map<String, Object> parameter){
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
      }
    }.toString();
  }

  public String selectExecNodeByJobId(Map<String, Object> parameter){
    return new SQL() {
      {
        SELECT("*");
        FROM(TABLE_NAME);
        WHERE("job_id = #{jobId}");
      }
    }.toString();
  }

  public String selectStatusByFlowId(Map<String, Object> parameter) {
    return new SQL() {
      {
        SELECT("flow_id, node_id, status, attempt");
        FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
        WHERE("flow_id = #{flowId}");
      }
    }.toString();
  }

  public String deleteByExecId(Map<String, Object> parameter) {
    return new SQL() {
      {
        DELETE_FROM(TABLE_NAME);
        WHERE("exec_id = #{execId}");
      }
    }.toString();
  }

}
