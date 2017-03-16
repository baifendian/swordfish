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

import com.baifendian.swordfish.dao.model.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;

public interface UserMapper {

  /**
   * 根据用户名称查询用户信息
   *
   * @param name
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "password", column = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "role", column = "role", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "proxyUsers", column = "proxy_users", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)
  })
  @SelectProvider(type = UserMapperProvider.class, method = "queryByName")
  User queryByName(@Param("name") String name);

  /**
   * 根据 email 名称查询用户信息
   *
   * @param email
   * @return
   */
  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "password", column = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "role", column = "role", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "proxyUsers", column = "proxy_users", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)
  })
  @SelectProvider(type = UserMapperProvider.class, method = "queryByEmail")
  User queryByEmail(@Param("email") String email);

//
//  @InsertProvider(type = UserMapperProvider.class, method = "insert")
//  @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "user.id", before = false, resultType = int.class)
//  int insert(@Param("user") User user);
//
//  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "password", column = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
//      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
//      @Result(property = "isSystemUser", column = "is_system_user", javaType = boolean.class, jdbcType = JdbcType.BOOLEAN),
//      @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),})
//  @SelectProvider(type = UserMapperProvider.class, method = "query")
//  List<User> query(@Param("user") User user, @Param("isJoin") boolean isJoin);
//
//  @UpdateProvider(type = UserMapperProvider.class, method = "updatePswById")
//  int updatePswById(@Param("user") User user);
//
//  @UpdateProvider(type = UserMapperProvider.class, method = "updateStateById")
//  int updateStateById(@Param("id") int id, @Param("status") int status);
//
//  @DeleteProvider(type = UserMapperProvider.class, method = "deleteByEmail")
//  int deleteByEmail(@Param("email") String email);
//
//  @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "password", column = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
//      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
//      @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
//      @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
//      @Result(property = "joinTime", column = "join_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
//      @Result(property = "roleType", column = "role", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
//      @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
//  })
//  @SelectProvider(type = UserMapperProvider.class, method = "queryById")
//  User queryById(@Param("userId") int userId);
//
//  @UpdateProvider(type = UserMapperProvider.class, method = "updateForJoinTenant")
//  int updateForJoinTenant(@Param("id") Integer id, @Param("tenant_id") Integer tenant_id);
}
