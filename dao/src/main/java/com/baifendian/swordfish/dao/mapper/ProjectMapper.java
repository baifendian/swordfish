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

import com.baifendian.swordfish.dao.model.Project;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.util.Date;
import java.util.List;

/**
 * Created by caojingwei on 16/8/25.
 */
@MapperScan
public interface ProjectMapper {

  @InsertProvider(type = ProjectSqlProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "project.id", before = false, resultType = int.class)
  int insert(@Param("project") Project project);

  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
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
  @SelectProvider(type = ProjectSqlProvider.class, method = "queryUserProject")
  List<Project> queryUserProject(@Param("userId") int userId);

  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tenantId", column = "tenant_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "createTime", column = "create_time", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "modifyTime", column = "modify_time", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "ownerId", column = "owner", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
  })
  @SelectProvider(type = ProjectSqlProvider.class, method = "queryAllProject")
  List<Project> queryAllProject();

  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
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
  @SelectProvider(type = ProjectSqlProvider.class, method = "queryTenantProject")
  List<Project> queryTenantProject(@Param("tenantId") int tenantId);

  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "ownerId", column = "owner", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "queueId", column = "queue_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "queueName", column = "queue_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dbId", column = "db_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "mailGroups", column = "mail_groups", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ProjectSqlProvider.class, method = "query")
  List<Project> query(@Param("project") Project project, @Param("isJoin") boolean isJoin);

  @UpdateProvider(type = ProjectSqlProvider.class, method = "updateById")
  int updateById(@Param("project") Project project);

  @DeleteProvider(type = ProjectSqlProvider.class, method = "deleteById")
  int deleteById(@Param("id") int id);

  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tenantId", column = "tenant_id", id = true, javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "ownerId", column = "owner", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "queueId", column = "queue_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "queueName", column = "queue_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
  })
  @SelectProvider(type = ProjectSqlProvider.class, method = "queryById")
  Project queryById(@Param("projectId") int projectId);

  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "ownerId", column = "owner", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "queueId", column = "queue_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "mailGroups", column = "mail_groups", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ProjectSqlProvider.class, method = "queryByName")
  Project queryByName(@Param("name") String name);

  @UpdateProvider(type = ProjectSqlProvider.class, method = "updateDescAndMailById")
  int updateDescAndMailById(@Param("projectId") int projectId, @Param("desc") String desc, @Param("modifyTime") Date modifyTime, @Param("mailGroups") String mailGroups);

  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "ownerId", column = "owner", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "queueId", column = "queue_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "queueName", column = "queue_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dbId", column = "db_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "mailGroups", column = "mail_groups", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ProjectSqlProvider.class, method = "queryByTableId")
  Project queryByTableId(@Param("tableId") Integer tableId);

  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "ownerId", column = "owner", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "queueId", column = "queue_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "queueName", column = "queue_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dbId", column = "db_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectSize", column = "project_size", javaType = double.class, jdbcType = JdbcType.DOUBLE),
          @Result(property = "mailGroups", column = "mail_groups", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ProjectSqlProvider.class, method = "queryProjectSizeTopByTenantId")
  List<Project> queryProjectSizeTopByTenantId(@Param("tenantId") int tenantId, @Param("top") int top);

  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "ownerId", column = "owner", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "ownerName", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "queueId", column = "queue_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "queueName", column = "queue_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "dbId", column = "db_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "mailGroups", column = "mail_groups", javaType = String.class, jdbcType = JdbcType.VARCHAR)
  })
  @SelectProvider(type = ProjectSqlProvider.class, method = "queryByDBName")
  Project queryByDBName(@Param("dbName") String dbName);

}
