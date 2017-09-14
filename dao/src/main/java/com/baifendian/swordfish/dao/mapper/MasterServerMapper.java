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

import com.baifendian.swordfish.dao.model.MasterServer;
import java.util.Date;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface MasterServerMapper {
  /**
   * 查询 master
   *
   * @return
   */
  @Results(value = {
      @Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "host", column = "host", javaType = String.class, jdbcType = JdbcType.VARCHAR),
      @Result(property = "port", column = "port", javaType = int.class, jdbcType = JdbcType.INTEGER),
      @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
      @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)
  })
  @SelectProvider(type = MasterServerMapperProvider.class, method = "query")
  MasterServer query();

  /**
   * 插入一个 master 信息
   *
   * @param masterServer
   * @return
   */
  @InsertProvider(type = MasterServerMapperProvider.class, method = "insert")
  int insert(@Param("masterServer") MasterServer masterServer);

  /**
   * 更新一个 master 信息
   *
   * @param masterServer
   * @return
   */
  @UpdateProvider(type = MasterServerMapperProvider.class, method = "update")
  int update(@Param("masterServer") MasterServer masterServer);

  /**
   * 删除 master 信息
   */
  @DeleteProvider(type = MasterServerMapperProvider.class, method = "delete")
  void delete();

  /**
   * 根据 host, port 删除
   *
   * @param host
   * @param port
   */
  @DeleteProvider(type = MasterServerMapperProvider.class, method = "deleteByHostPort")
  int deleteByHostPort(@Param("host") String host, @Param("port") int port);
}
