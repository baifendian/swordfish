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

import com.baifendian.swordfish.dao.enums.ResourcePubStatus;
import com.baifendian.swordfish.dao.enums.ResourceType;
import com.baifendian.swordfish.dao.model.Resource;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * 资源相关操作
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月24日
 */
@MapperScan
public interface ResourceMapper {
    /**
     * 插入记录并获取记录 id
     * <p>
     *
     * @param resource
     * @return 修改记录数
     */
    @InsertProvider(type = ResourceSqlProvider.class, method = "insert")
    @SelectKey(statement = "SELECT LAST_INSERT_ID() AS id", keyProperty = "resource.id", resultType = int.class, before = false)
    int insertAndGetId(@Param("resource") Resource resource);

    /**
     * 查询详情
     * <p>
     *
     * @param resourceId
     * @return {@link Resource}
     */
    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = ResourceType.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "parentId", column = "parent_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "lastPublishBy", column = "last_publish_by", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "lastPublishByName", column = "last_publish_by_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "publishTime", column = "publish_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "pubStatus", column = "pub_status", typeHandler = EnumOrdinalTypeHandler.class, javaType = ResourcePubStatus.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "orgId", column = "org_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "orgName", column = "org_name", javaType = String.class, jdbcType = JdbcType.VARCHAR), })
    @SelectProvider(type = ResourceSqlProvider.class, method = "queryDetail")
    Resource queryDetail(@Param("resourceId") Integer resourceId);

    /**
     * 查询详情
     * <p>
     *
     * @param resourceIds
     * @return {@link Resource}
     */
    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "type", column = "type", typeHandler = EnumOrdinalTypeHandler.class, javaType = ResourceType.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "parentId", column = "parent_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "ownerId", column = "owner_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "publishTime", column = "publish_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
                       @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "pubStatus", column = "pub_status", typeHandler = EnumOrdinalTypeHandler.class, javaType = ResourcePubStatus.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "orgId", column = "org_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "orgName", column = "org_name", javaType = String.class, jdbcType = JdbcType.VARCHAR), })
    @SelectProvider(type = ResourceSqlProvider.class, method = "queryDetails")
    List<Resource> queryDetails(@Param("resourceIds") Set<Integer> resourceIds);

    /**
     * 删除资源
     * <p>
     *
     * @param resourceId
     * @return 删除记录数
     */
    @DeleteProvider(type = ResourceSqlProvider.class, method = "deleteById")
    int delete(@Param("resourceId") Integer resourceId);

    /**
     * 更新资源
     * <p>
     *
     * @param resource
     * @return 更新记录数
     */
    @UpdateProvider(type = ResourceSqlProvider.class, method = "updateById")
    int updateById(@Param("resource") Resource resource);

    /**
     * 删除项目的资源信息
     *
     * @param projectId
     * @return
     */
    @DeleteProvider(type = ResourceSqlProvider.class, method = "deleteByProjectId")
    int deleteByProjectId(@Param("projectId") Integer projectId);

    @SelectProvider(type = ResourceSqlProvider.class, method = "queryIdByNames")
    List<Integer> queryIdByNames(@Param("projectId") Integer projectId, @Param("resourceNames") List<String> resourceNames);

    @SelectProvider(type = ResourceSqlProvider.class, method = "countByProject")
    Integer countByProject(@Param("projectId") Integer projectId);
}
