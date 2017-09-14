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

import com.baifendian.swordfish.dao.enums.UserRoleType;
import com.baifendian.swordfish.dao.model.ProjectUser;
import com.baifendian.swordfish.dao.model.User;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

public interface ProjectUserMapper {
  /**
   * 查询项目下的用户
   *
   * @param projectId
   * @param userId
   * @return
   */
  @Results(value = {@Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "userId", column = "user_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "perm", column = "perm", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)})
  @SelectProvider(type = ProjectUserMapperProvider.class, method = "query")
  ProjectUser query(@Param("projectId") int projectId, @Param("userId") int userId);

  /**
   * 插入 "项目/用户" 数据到相关表中
   *
   * @param projectUser
   * @return
   */
  @InsertProvider(type = ProjectUserMapperProvider.class, method = "insert")
  int insert(@Param("projectUser") ProjectUser projectUser);

  /**
   * 修改用户的权限等信息
   *
   * @param projectUser
   * @return
   */
  @UpdateProvider(type = ProjectUserMapperProvider.class, method = "update")
  int modify(@Param("projectUser") ProjectUser projectUser);

  /**
   * 删除项目下用户
   *
   * @param projectId
   * @param userId
   * @return
   */
  @DeleteProvider(type = ProjectUserMapperProvider.class, method = "delete")
  int delete(@Param("projectId") int projectId, @Param("userId") int userId);

  /**
   * 查询一个项目下的所有用户
   *
   * @param projectId
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectId", column = "project_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "userId", column = "user_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "owner", column = "owner_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "perm", column = "perm", javaType = Integer.class, jdbcType = JdbcType.INTEGER)

  })
  @SelectProvider(type = ProjectUserMapperProvider.class, method = "queryByProject")
  List<ProjectUser> queryByProject(@Param("projectId") int projectId);

  /**
   * 查询项目下的所有用户信息
   *
   * @param projectId
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "password", column = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "role", column = "role", typeHandler = EnumOrdinalTypeHandler.class, javaType = UserRoleType.class, jdbcType = JdbcType.TINYINT),
          @Result(property = "proxyUsers", column = "proxy_users", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
          @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = ProjectUserMapperProvider.class, method = "queryForUser")
  List<User> queryForUser(@Param("projectId") int projectId);

  /**
   * 根据用户名称进行查询
   *
   * @param userId
   * @return
   */
  @Results(value = {@Result(property = "projectId", column = "project_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "projectName", column = "project_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "userId", column = "user_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "perm", column = "perm", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
          @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)})
  @SelectProvider(type = ProjectUserMapperProvider.class, method = "queryByUser")
  List<ProjectUser> queryByUser(@Param("userId") int userId);
}
