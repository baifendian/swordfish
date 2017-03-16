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
import com.baifendian.swordfish.dao.model.ProjectFlow;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * workflow的相关操作
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月27日
 */
public interface ProjectFlowMapper {
    /**
     * 插入记录并获取记录 id
     * <p>
     *
     * @return 修改记录数
     */
    @InsertProvider(type = ProjectFlowMapperSqlProvider.class, method = "insert")
    @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "flow.id", resultType = int.class, before = false)
    int insertAndGetId(@Param("flow") ProjectFlow flow);

    /**
     * 通过 id 更新记录
     * <p>
     *
     * @param flow
     */
    @UpdateProvider(type = ProjectFlowMapperSqlProvider.class, method = "updateById")
    int updateById(@Param("flow") ProjectFlow flow);

    /**
     * 查询一个 workflow
     * <p>
     *
     * @param flowId
     * @return {@link ProjectFlow}
     */
    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowType.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
                       @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
                       @Result(property = "proxyUser", column = "proxy_user", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "mailGroups", column = "mail_groups", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "lastModifyBy", column = "last_modify_by", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "inputTables", column = "input_tables", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "outputTables", column = "output_tables", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "resources", column = "resources", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "datasources", column = "datasources", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "userDefinedParams", column = "user_defined_params", javaType = String.class, jdbcType = JdbcType.VARCHAR), })
    @SelectProvider(type = ProjectFlowMapperSqlProvider.class, method = "query")
    ProjectFlow findById(@Param("id") int flowId);

    /**
     * 查询多个 workflow
     * <p>
     *
     * @param flowIds
     * @return {@link ProjectFlow}
     */
    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowType.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "canvas", column = "canvas", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "publishTime", column = "publish_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "scheduleStatus", column = "schedule_status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "lastModifyBy", column = "last_modify_by", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "lastPublishBy", column = "last_publish_by", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER), })
    @SelectProvider(type = ProjectFlowMapperSqlProvider.class, method = "findByIds")
    List<ProjectFlow> findByIds(@Param("flowIds") Set<Integer> flowIds);

    /**
     * 删除一个 workflow
     * <p>
     *
     * @param flowId
     * @return 删除记录数
     */
    @DeleteProvider(type = ProjectFlowMapperSqlProvider.class, method = "deleteById")
    int deleteById(@Param("id") int flowId);

    /**
     * 查询一个组织里面的workflow数量(流，ETL，高级ETL)
     * <p>
     *
     * @param tenantId
     * @return 查询记录数
     */
    @SelectProvider(type = ProjectFlowMapperSqlProvider.class, method = "queryFlowNum")
    int queryFlowNum(@Param("tenantId") int tenantId, @Param("flowTypes") List<FlowType> flowTypes);

    /**
     * 查询一个组织里面的workflow数量(流，ETL，高级ETL)
     * <p>
     *
     * @param projectId
     * @return 查询记录数
     */
    @SelectProvider(type = ProjectFlowMapperSqlProvider.class, method = "queryFlowNumByProjectId")
    int queryFlowNumByProjectId(@Param("projectId") int projectId, @Param("flowTypes") List<FlowType> flowTypes);

    /**
     * 查询一个项目下某个类型的所有workflow
     * <p>
     *
     * @param projectId
     * @param flowType
     * @return {@link ProjectFlow}
     */
    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowType.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "canvas", column = "canvas", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "publishTime", column = "publish_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "scheduleStatus", column = "schedule_status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "lastModifyBy", column = "last_modify_by", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "lastPublishBy", column = "last_publish_by", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER), })
    @SelectProvider(type = ProjectFlowMapperSqlProvider.class, method = "queryFlowsByProjectId")
    List<ProjectFlow> queryFlowsByProjectId(@Param("projectId") int projectId, @Param("flowType") FlowType flowType);

    /**
     * 删除项目的任务信息
     *
     * @param projectId
     * @param flowType
     * @return
     */
    @DeleteProvider(type = ProjectFlowMapperSqlProvider.class, method = "deleteByProjectId")
    int deleteByProjectId(@Param("projectId") int projectId, @Param("flowType") FlowType flowType);

    /**
     * 查询一个 workflow (by name)
     * <p>
     *
     * @return {@link ProjectFlow}
     */
    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = FlowType.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "canvas", column = "canvas", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "publishTime", column = "publish_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "lastModifyBy", column = "last_modify_by", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "lastPublishBy", column = "last_publish_by", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "ownerId", column = "owner_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "inputTables", column = "input_tables", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "outputTables", column = "output_tables", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "resourceIds", column = "resource_ids", javaType = String.class, jdbcType = JdbcType.VARCHAR), })
    @SelectProvider(type = ProjectFlowMapperSqlProvider.class, method = "queryByName")
    ProjectFlow findByName(@Param("projectId") Integer projectId, @Param("name") String name);

}
