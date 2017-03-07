/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月28日
 * File Name      : DqSqlParam.java
 */

package com.baifendian.swordfish.dao.mysql.model.flow.params.dq;

import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.dao.mysql.model.flow.params.adhoc.AdHocSqlParam;

/**
 * 数据质量 sql 节点参数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月28日
 */
public class DqSqlParam extends BaseParam {
    /** 原始 sql 语句（多条，内部可能包含换行等符号，执行时需要处理） */
    private String value;

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
}
