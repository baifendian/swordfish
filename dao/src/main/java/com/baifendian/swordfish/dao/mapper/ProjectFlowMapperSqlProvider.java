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

import com.baifendian.swordfish.dao.enums.FlowType;
import com.baifendian.swordfish.dao.mapper.utils.EnumFieldUtil;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * workflow 的 sql 生成器
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月27日
 */
public class ProjectFlowMapperSqlProvider {

    /** 表名 */
    public static final String TABLE_NAME = "project_flows";
    /**
     * 插入
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String insert(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(TABLE_NAME);
                VALUES("name", "#{flow.name}");
                VALUES("type", EnumFieldUtil.genFieldStr("flow.type", FlowType.class));
                VALUES("project_id", "#{flow.projectId}");
                VALUES("owner_id", "#{flow.ownerId}");
                VALUES("modify_time", "#{flow.modifyTime}");
                VALUES("create_time", "#{flow.createTime}");
                VALUES("last_modify_by", "#{flow.lastModifyBy}");
                VALUES("canvas", "#{flow.canvas}");
            }
        }.toString();
    }

    /**
     * 通过 id 更新(用于重命名)
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String updateById(Map<String, Object> parameter) {
        ProjectFlow projectFlow = (ProjectFlow) parameter.get("flow");
        return new SQL() {
            {
                UPDATE(TABLE_NAME);
                if (StringUtils.isNotEmpty(projectFlow.getName())) {
                    SET("name = #{flow.name}");
                }
                if (projectFlow.getOwnerId() != 0) {
                    SET("owner_id = #{flow.ownerId}");
                }
                if (projectFlow.getLastModifyBy() != 0) {
                    SET("last_modify_by = #{flow.lastModifyBy}");
                }
                if (projectFlow.getLastPublishBy() != 0) {
                    SET("last_publish_by = #{flow.lastPublishBy}");
                }
                if (projectFlow.getModifyTime() != null) {
                    SET("modify_time = #{flow.modifyTime}");
                }
                if (projectFlow.getInputTables() != null) {
                    SET("input_tables = #{flow.inputTables}");
                }
                if (projectFlow.getOutputTables() != null) {
                    SET("output_tables = #{flow.outputTables}");
                }
                if (projectFlow.getResources() != null) {
                    SET("resources = #{flow.resources}");
                }
                WHERE("id = #{flow.id}");
            }
        }.toString();
    }

    /**
     * 查询一个记录
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String query(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("f.*");
                SELECT("p.name as project_name");
                FROM(TABLE_NAME + " as f");
                INNER_JOIN("project as p on p.id = f.project_id");
                WHERE("f.id = #{id}");
            }
        }.toString();
    }

    public String queryByName(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM(TABLE_NAME);
                WHERE("project_id = #{projectId}");
                WHERE("name = #{name}");
            }
        }.toString();
    }

    /**
     * 查询多个记录
     * <p>
     *
     * @param flowIds
     * @return sql 语句
     */
    public String findByIds(Set<Integer> flowIds) {
        StringBuilder sb = new StringBuilder();
        sb.append("select a.*, b.name as project_name, c.name as owner_name, s.schedule_status ");
        sb.append("from ");
        sb.append(TABLE_NAME);
        sb.append(" as a ");
        sb.append("inner join schedules as s on a.id = s.flow_id ");
        sb.append("inner join project as b on a.project_id = b.id ");
        sb.append("inner join user as c on a.owner_id = c.id ");

        if (flowIds != null && flowIds.size() > 0) {
            StringUtils.join(flowIds, ",");
            sb.append("where a.id in (");
            sb.append(StringUtils.join(flowIds, ","));
            sb.append(") ");
        }

        return sb.toString();
    }

    /**
     * 删除某个workflow
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String deleteById(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_NAME);
                WHERE("id = #{id}");
            }
        }.toString();
    }

    public String queryFlowNum(int tenantId, List<FlowType> flowTypes) {

        StringBuilder sb = new StringBuilder();
        sb.append("select count(0) ");
        sb.append("from "+ TABLE_NAME + " as a ");
        sb.append("inner join project as b on a.project_id = b.id ");
        sb.append("inner join tenant as c on b.tenant_id = c.id and c.id = #{tenantId} ");

        if (flowTypes != null && flowTypes.size() > 0) {
            sb.append("where a.type in (-1 ");
            for (FlowType flowType : flowTypes) {
                sb.append(",");
                sb.append(flowType.getType());
            }
            sb.append(") ");
        }

        return sb.toString();
    }

    public String queryFlowNumByProjectId(int projectId, List<FlowType> flowTypes) {

        StringBuilder sb = new StringBuilder();
        sb.append("select count(0) ");
        sb.append("from " + TABLE_NAME + " as a ");
        sb.append("inner join project as b on a.project_id = b.id and b.id = #{projectId} ");

        if (flowTypes != null && flowTypes.size() > 0) {
            sb.append("where a.type in (-1 ");
            for (FlowType flowType : flowTypes) {
                sb.append(",");
                sb.append(flowType.getType());
            }
            sb.append(") ");
        }

        return sb.toString();
    }

    /**
     * 查询某个项目下的特定类型的所有workflow
     * <p>
     *
     * @param projectId
     * @param flowType
     * @return sql 语句
     */
    public String queryFlowsByProjectId(int projectId, FlowType flowType) {

        StringBuilder sb = new StringBuilder();
        sb.append("select a.* ");
        sb.append("from " + TABLE_NAME + " as a ");
        sb.append("inner join project as b on a.project_id = b.id and b.id = #{projectId} ");

        if (flowType != null) {
            sb.append("where a.type = ");
            sb.append(flowType.getType());
        }

        return sb.toString();
    }

    /**
     * 删除某个项目下的工作流类型
     *
     * @param parameter
     * @return
     */
    public String deleteByProjectId(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_NAME);
                WHERE("project_id = #{projectId}");
                WHERE("type = " + EnumFieldUtil.genFieldStr("flowType", FlowType.class));
            }
        }.toString();
    }
}
