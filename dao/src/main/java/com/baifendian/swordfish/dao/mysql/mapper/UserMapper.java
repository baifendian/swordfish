
package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.model.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;

import java.util.Date;
import java.util.List;

/**
 * Created by caojingwei on 16/7/22.
 */
@MapperScan
public interface UserMapper {

    @InsertProvider(type = UserMapperSQL.class, method = "insert")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="user.id", before=false, resultType=int.class)
    int insert(@Param("user") User user);

    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "password", column = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "isSystemUser", column = "is_system_user", javaType = boolean.class, jdbcType = JdbcType.BOOLEAN),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER), })
    @SelectProvider(type = UserMapperSQL.class, method = "query")
    List<User> query(@Param("user") User user, @Param("isJoin") boolean isJoin);

    @UpdateProvider(type = UserMapperSQL.class, method = "updatePswById")
    int updatePswById(@Param("user") User user);

    @UpdateProvider(type = UserMapperSQL.class, method = "updateStateById")
    int updateStateById(@Param("id") int id, @Param("status") int status);

    @DeleteProvider(type = UserMapperSQL.class, method = "deleteByEmail")
    int deleteByEmail(@Param("email") String email);

    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "password", column = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "joinTime", column = "join_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "roleType", column = "role", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
    })
    @SelectProvider(type = UserMapperSQL.class, method = "queryById")
    User queryById(@Param("userId") int userId);

    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "password", column = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "tenantName", column = "tenant_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "joinTime", column = "join_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "roleType", column = "role", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = UserMapperSQL.class, method = "queryByEmail")
    User queryByEmail(@Param("email") String email);

    @Results(value = { @Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "password", column = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "modifyTime", column = "modify_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "tenantId", column = "tenant_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "joinTime", column = "join_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "roleType", column = "role", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = UserMapperSQL.class, method = "findByName")
    User findByName(@Param("name") String name);
    
    @UpdateProvider(type = UserMapperSQL.class, method = "updateForJoinTenant")
    int updateForJoinTenant(@Param("id") Integer id, @Param("tenant_id") Integer tenant_id);
 }
