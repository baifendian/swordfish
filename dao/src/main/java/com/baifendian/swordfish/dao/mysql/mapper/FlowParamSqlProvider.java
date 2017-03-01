/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月12日
 * File Name      : FlowParamSqlProvider.java
 */

package com.baifendian.swordfish.dao.mysql.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * flows_params 表的 sql 生成器
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月12日
 */
public class FlowParamSqlProvider {

    /** 表名 */
    public static final String TABLE_NAME = "flows_params";

    /**
     * 插入
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String insert(Map<String, Object> parameter) {
        // return new SQL() {
        // {
        // INSERT_INTO(TABLE_NAME);
        // VALUES("flow_id", "#{flowParam.flowId}");
        // VALUES("key", "#{flowParam.key}");
        // VALUES("value", "#{flowParam.value}");
        // }
        // }.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(TABLE_NAME);
        sb.append("(flow_id, `key`, `value`) ");
        sb.append("VALUES ");
        sb.append("(");
        sb.append("#{flowParam.flowId}");
        sb.append(",");
        sb.append("#{flowParam.key}");
        sb.append(",");
        sb.append("#{flowParam.value}");
        sb.append(")");

        sb.append(" ON DUPLICATE KEY UPDATE value=VALUES(`value`)");
        return sb.toString();
    }

  /**
   * 删除 flow id 参数配置
   * @param parameter
   * @return
   */
    public String deleteByFlowId(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_NAME);
                WHERE("flow_id = #{flowId}");
            }
        }.toString();
    }

    /**
     * 删除
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String deleteByFlowIdAndKey(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(TABLE_NAME);
                WHERE("flow_id = #{flowId}");
                WHERE("`key` = #{key}");
            }
        }.toString();
    }

    /**
     * 查询某个 workflow 的自定义参数
     * <p>
     *
     * @param parameter
     * @return sql 语句
     */
    public String queryAllByFlowId(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM(TABLE_NAME);
                WHERE("flow_id = #{flowId}");
            }
        }.toString();
    }

}
