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

import com.baifendian.swordfish.dao.mysql.model.Session;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.util.Date;

/**
 * Created by caojingwei on 16/8/19.
 */
@MapperScan
public interface SessionMapper {
    @Results(value = {
            @Result(property = "id", column = "id", id = true,javaType = String.class,jdbcType = JdbcType.VARCHAR),
            @Result(property = "userId", column = "user_id",javaType = int.class,jdbcType = JdbcType.INTEGER),
            @Result(property = "ip", column = "ip",javaType = String.class,jdbcType = JdbcType.VARCHAR),
            @Result(property = "startTime", column = "start_time",javaType = int.class,jdbcType = JdbcType.INTEGER),
            @Result(property = "endTime", column = "end_time",javaType = int.class,jdbcType = JdbcType.INTEGER),
            @Result(property = "isRemember", column = "is_remember",javaType = Boolean.class,jdbcType = JdbcType.BOOLEAN),
    })
    @SelectProvider(type = SessionMapperSQL.class, method = "findById")
    Session findById(@Param("sessionId") String sessionId);


    @InsertProvider(type = SessionMapperSQL.class, method = "insert")
    int insert(@Param("session") Session session);

    @SelectProvider(type = SessionMapperSQL.class, method = "delete")
    void delete(@Param("sessionId") String sessionId, @Param("endTime") int endTime);

    @UpdateProvider(type = SessionMapperSQL.class, method = "update")
    int update(@Param("sessionId") String sessionId, @Param("endTime") Date endTime, @Param("startTime") Date startTime);

}
