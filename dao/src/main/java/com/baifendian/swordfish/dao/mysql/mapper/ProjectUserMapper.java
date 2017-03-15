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

package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.model.Project;
import com.baifendian.swordfish.dao.mysql.model.ProjectUser;
import com.baifendian.swordfish.dao.mysql.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.util.Date;
import java.util.List;

/**
 * Created by caojingwei on 16/8/25.
 */
@MapperScan
public interface ProjectUserMapper {
    @InsertProvider(type = ProjectUserSqlProvider.class, method = "insert")
    int insert(@Param("projectUser") ProjectUser projectUser);

    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "joinTime", column = "join_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "roleType", column = "role", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ProjectUserSqlProvider.class, method = "queryForUser")
    List<User> queryForUser(@Param("projectId") int projectId);

    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "tenantId", column = "tenant_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "ownerId", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "queueId", column = "queue_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "queue_name", column = "queue_name", javaType = int.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = ProjectUserSqlProvider.class, method = "queryForProject")
    List<Project> queryForProject(@Param("userId") int userId);

    @DeleteProvider(type = ProjectUserSqlProvider.class, method = "delete")
    int delete(@Param("projectId") int projectId, @Param("userId") int userId);

    @Results(value = { @Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "userId", column = "user_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP), })
    @SelectProvider(type = ProjectUserSqlProvider.class, method = "query")
    ProjectUser query(@Param("userId") int userId, @Param("projectId") int projectId);
}
