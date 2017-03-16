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

import com.baifendian.swordfish.dao.model.Session;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.sql.Timestamp;
import java.util.Date;

/**
 * author: smile8 date:   2017/3/16 desc:   session 操作接口
 */
@MapperScan
public interface SessionMapper {

  /**
   * 根据 session id 进行查询
   */
  @Results(value = {
          @Result(property = "id", column = "id", id = true, javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "userId", column = "user_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "ip", column = "ip", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "lastLoginTime", column = "last_login_time", javaType = Timestamp.class, jdbcType = JdbcType.DATE)
  })
  @SelectProvider(type = SessionMapperProvider.class, method = "queryById")
  Session queryById(@Param("sessionId") String sessionId);

  /**
   * 插入一个 session 数据
   */
  @InsertProvider(type = SessionMapperProvider.class, method = "insert")
  int insert(@Param("session") Session session);

  /**
   * 删除指定的 session 数据
   */
  @SelectProvider(type = SessionMapperProvider.class, method = "deleteById")
  void deleteById(@Param("sessionId") String sessionId);

  /**
   * 删除过期的 session
   */
  @SelectProvider(type = SessionMapperProvider.class, method = "deleteByExpireTime")
  void deleteByExpireTime(@Param("expireTime") Date expireTime);

  /**
   * 更新 session 的最后登陆时间
   */
  @UpdateProvider(type = SessionMapperProvider.class, method = "update")
  int update(@Param("sessionId") String sessionId, @Param("loginTime") Date loginTime);
}
