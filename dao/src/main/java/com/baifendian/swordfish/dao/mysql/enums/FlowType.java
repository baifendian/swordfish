/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月27日
 * File Name      : FlowType.java
 */

package com.baifendian.swordfish.dao.mysql.enums;

/**
 * workflow 类型
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月27日
 */
public enum FlowType {

    /** 0 短任务, 1 长任务, 2 ETL, 3 即席查询 , 4 数据质量 , 5 文件导入 **/
    SHORT, LONG, ETL, ADHOC, DQ, FILE_IMPORT;

    /**
     * getter method
     * 
     * @see FlowType
     * @return the type
     */
    public Integer getType() {
        return ordinal();
    }

    /**
     * 通过 type 获取枚举对象
     * <p>
     *
     * @param type
     * @return {@link FlowType}
     * @throws IllegalArgumentException
     */
    public static FlowType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return FlowType.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + FlowType.class.getSimpleName() + " .", ex);
        }
    }
}
