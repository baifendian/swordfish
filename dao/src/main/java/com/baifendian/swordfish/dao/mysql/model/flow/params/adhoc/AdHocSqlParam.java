/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月23日
 * File Name      : AdHocSqlParam.java
 */

package com.baifendian.swordfish.dao.mysql.model.flow.params.adhoc;

import com.baifendian.swordfish.dao.mysql.model.flow.params.BaseParam;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 即席查询 sql 节点的参数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月23日
 */
public class AdHocSqlParam extends BaseParam {

    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** 原始 sql 语句（多条，内部可能包含换行等符号，执行时需要处理） */
    private String value;

    /** 查询结果的限制条数 */
    private Integer limit;

    @Override
    public boolean checkValid() {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        try {
            for (String sql : value.split(";")) {
                sql = sql.replaceAll("\n", " ");
                sql = sql.replaceAll("\r", "");
                if (StringUtils.isNotBlank(sql)) {
                    ParseDriver pd = new ParseDriver();
                    pd.parse(sql);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     * getter method
     * 
     * @see AdHocSqlParam#value
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * setter method
     * 
     * @see AdHocSqlParam#value
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * getter method
     * 
     * @see AdHocSqlParam#limit
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * setter method
     * 
     * @see AdHocSqlParam#limit
     * @param limit
     *            the limit to set
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

}
