/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月12日
 * File Name      : FlowParamMapper.java
 */

package com.baifendian.swordfish.dao.mysql.mapper;

import com.baifendian.swordfish.dao.mysql.model.FlowParam;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * workflow 参数信息的操作
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月12日
 */
public interface FlowParamMapper {
    /**
     * 插入记录
     * <p>
     *
     * @param flowParam
     * @return 修改记录数
     */
    @InsertProvider(type = FlowParamSqlProvider.class, method = "insert")
    int insertAndGetId(@Param("flowParam") FlowParam flowParam);

    /**
     * 删除记录
     * <p>
     *
     * @param flowId
     * @return 修改记录数
     */
    @DeleteProvider(type = FlowParamSqlProvider.class, method = "deleteByFlowId")
    int deleteByFlowId(@Param("flowId") int flowId);

    /**
     * 删除记录
     * <p>
     *
     * @param flowId
     * @param key
     * @return 修改记录数
     */
    @DeleteProvider(type = FlowParamSqlProvider.class, method = "deleteByFlowIdAndKey")
    int deleteByFlowIdAndKey(@Param("flowId") int flowId, @Param("key") String key);

    /**
     * 查询 flowId 下的所有参数
     * <p>
     *
     * @param flowId
     * @return FlowParam 列表
     */
    @Results(value = { @Result(property = "flowId", column = "flow_id", javaType = int.class, jdbcType = JdbcType.INTEGER),
                       @Result(property = "key", column = "key", javaType = String.class, jdbcType = JdbcType.VARCHAR),
                       @Result(property = "value", column = "value", javaType = String.class, jdbcType = JdbcType.VARCHAR) })
    @SelectProvider(type = FlowParamSqlProvider.class, method = "queryAllByFlowId")
    List<FlowParam> queryAllByFlowId(@Param("flowId") int flowId);

}
