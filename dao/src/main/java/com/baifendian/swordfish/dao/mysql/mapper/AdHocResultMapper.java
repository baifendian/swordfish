/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月6日
 * File Name      : AdHocResultMapper.java
 */

package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.model.AdHocResult;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * 即席查询执行结果的操作
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月6日
 */
public interface AdHocResultMapper {
    /**
     * 插入记录
     * <p>
     *
     * @param adHocResult
     * @return 插入记录数
     */
    @InsertProvider(type = AdHocResultMapperProvider.class, method = "insert")
    int insert(@Param("adHocResult") AdHocResult adHocResult);

    /**
     * 更新记录
     * <p>
     *
     * @param adHocResult
     * @return 更新记录数
     */
    @UpdateProvider(type = AdHocResultMapperProvider.class, method = "update")
    int update(@Param("adHocResult") AdHocResult adHocResult);

    /**
     * 查询即席查询的执行结果(通过 execId 和 nodeId)
     * <p>
     *
     * @param execId
     * @param nodeId
     * @return 执行结果
     */
    @Results(value = { @Result(property = "execId", column = "exec_id", javaType = long.class, jdbcType = JdbcType.BIGINT),
                       @Result(property = "nodeId", column = "node_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "index", column = "index", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "stm", column = "stm", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "result", column = "result", javaType = String.class, jdbcType = JdbcType.VARCHAR) })
    @SelectProvider(type = AdHocResultMapperProvider.class, method = "selectByExecIdAndNodeId")
    List<AdHocResult> selectByExecIdAndNodeId(@Param("execId") long execId, @Param("nodeId") int nodeId);

    /**
     * 查询即席查询的执行结果(通过 execId 和 flowId)
     * <p>
     *
     * @param execId
     * @param flowId
     * @return 执行结果
     */
    @Results(value = { @Result(property = "execId", column = "exec_id", javaType = long.class, jdbcType = JdbcType.BIGINT),
                       @Result(property = "nodeId", column = "node_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "index", column = "index", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "status", column = "status", typeHandler = EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "stm", column = "stm", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "result", column = "result", javaType = String.class, jdbcType = JdbcType.VARCHAR) })
    @SelectProvider(type = AdHocResultMapperProvider.class, method = "selectByExecIdAndFlowId")
    List<AdHocResult> selectByExecIdAndFlowId(@Param("execId") long execId, @Param("flowId") int flowId);

    @Results(value = { @Result(property = "execId", column = "exec_id", javaType = long.class, jdbcType = JdbcType.BIGINT),
                       @Result(property = "nodeId", column = "node_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "index", column = "index", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "status", column = "status",  typeHandler= EnumOrdinalTypeHandler.class, jdbcType = JdbcType.TINYINT),
                       @Result(property = "stm", column = "stm", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "result", column = "result", javaType = String.class, jdbcType = JdbcType.VARCHAR) })
    @SelectProvider(type = AdHocResultMapperProvider.class, method = "selectByExecIdAndFlowIdAndIndex")
    List<AdHocResult> selectByExecIdAndFlowIdAndIndex(@Param("execId") long execId, @Param("flowId") int flowId, @Param("index") int index);
}
