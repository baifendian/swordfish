/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.model.MasterServer;
import com.baifendian.swordfish.dao.mysql.model.Session;
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
            @Result(property = "ip", column = "ip",javaType = String.class,jdbcType = JdbcType.VARCHAR),
            @Result(property = "port", column = "port",javaType = int.class,jdbcType = JdbcType.INTEGER),
            @Result(property = "updateTime", column = "update_time",javaType = int.class,jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = MasterServerMapperSQL.class, method = "query")
    MasterServer query();


    @InsertProvider(type = MasterServerMapperSQL.class, method = "insert")
    int insert(@Param("session") Session session);

    @SelectProvider(type = MasterServerMapperSQL.class, method = "delete")
    void delete();

}
