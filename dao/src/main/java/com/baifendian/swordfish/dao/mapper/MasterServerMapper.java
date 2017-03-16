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

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.util.Date;

/**
 * @author : liujin
 * @date : 2017-03-10 16:01
 */
@MapperScan
public interface MasterServerMapper {
  @Results(value = {
          @Result(property = "host", column = "host", javaType = String.class, jdbcType = JdbcType.VARCHAR),
          @Result(property = "port", column = "port", javaType = int.class, jdbcType = JdbcType.INTEGER),
          @Result(property = "updateTime", column = "update_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
  })
  @SelectProvider(type = MasterServerMapperSQL.class, method = "query")
  MasterServer query();


  @InsertProvider(type = MasterServerMapperSQL.class, method = "insert")
  int insert(@Param("masterServer") MasterServer masterServer);

  @UpdateProvider(type = MasterServerMapperSQL.class, method = "update")
  int update(@Param("masterServer") MasterServer masterServer);

  @SelectProvider(type = MasterServerMapperSQL.class, method = "delete")
  void delete();

}
