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
