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

import com.baifendian.swordfish.dao.model.ProjectUser;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.util.Date;
import java.util.List;

@MapperScan
public interface ProjectUserMapper {
  @InsertProvider(type = ProjectUserSqlProvider.class, method = "insert")
  int insert(@Param("projectUser") ProjectUser projectUser);

  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "userName", column = "userName", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "perm", column = "perm", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
  })
  @SelectProvider(type = ProjectUserSqlProvider.class, method = "queryForUser")
  List<ProjectUser> queryForUser(@Param("projectId") int projectId);

  @DeleteProvider(type = ProjectUserSqlProvider.class, method = "delete")
  int delete(@Param("projectId") int projectId, @Param("userId") int userId);

  @Results(value = {@Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "userId", column = "user_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),})
  @SelectProvider(type = ProjectUserSqlProvider.class, method = "query")
  ProjectUser query(@Param("userId") int userId, @Param("projectId") int projectId);
}
