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
import com.baifendian.swordfish.dao.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.List;

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
      @Result(property = "role", column = "role", typeHandler = EnumOrdinalTypeHandler.class, javaType = UserRoleType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "proxyUsers", column = "proxy_users", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
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
      @Result(property = "role", column = "role", typeHandler = EnumOrdinalTypeHandler.class, javaType = UserRoleType.class, jdbcType = JdbcType.TINYINT),
      @Result(property = "proxyUsers", column = "proxy_users", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "createTime", column = "create_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE),
      @Result(property = "modifyTime", column = "modify_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = UserMapperProvider.class, method = "queryByEmail")
  User queryByEmail(@Param("email") String email);

  /**
   * 校验用户信息是否正确
   *
   * @param name
   * @param email
   * @param password
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
  @SelectProvider(type = UserMapperProvider.class, method = "queryForCheck")
  User queryForCheck(@Param("name") String name, @Param("email") String email, @Param("password") String password);

  /**
   * 根据 user id 进行查询
   *
   * @param userId
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
  @SelectProvider(type = UserMapperProvider.class, method = "queryById")
  User queryById(@Param("userId") int userId);

  /**
   * 插入用户
   *
   * @param user
   * @return
   */
  @InsertProvider(type = UserMapperProvider.class, method = "insert")
  @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "user.id", before = false, resultType = int.class)
  int insert(@Param("user") User user);

  /**
   * 更新信息
   *
   * @param user
   * @return
   */
  @UpdateProvider(type = UserMapperProvider.class, method = "update")
  int update(@Param("user") User user);

  /**
   * 删除用户
   *
   * @param name
   * @return
   */
  @DeleteProvider(type = UserMapperProvider.class, method = "delete")
  int delete(@Param("name") String name);

  /**
   * 查询所有用户
   *
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
  @SelectProvider(type = UserMapperProvider.class, method = "queryAllUsers")
  List<User> queryAllUsers();
}
