/*
 * Create Author  : dsfan
 * Create Date    : 2016-7-21
 * File Name      : StmType.java
 */

package com.baifendian.swordfish.common.hive.beans;

/**HQL语句类型
 * <p>
 * 
 * @author : dsfan
 *
 * @date : 2016-7-21
 */
public enum StmType {
    EXPLAIN(0), SELECT(1), INSERT(2), UPDATE(3), DELETE(4), LOAD(5), EXPORT(6), IMPORT(7), DDL(8), OTHER(99);

    /** 语句类型值 */
    private Integer type;

    /**
     * private constructor
     */
    private StmType(Integer type) {
        this.type = type;
    }

    /**
     * getter method 
     * @see StmType#type
     * @return the type
     */
    public Integer getType() {
        return type;
    }
}