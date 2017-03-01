/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月25日
 * File Name      : FunctionType.java
 */

package com.baifendian.swordfish.dao.mysql.enums;

/**
 * 函数类型
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月25日
 */
public enum FunctionType {
    /** 0表示系统函数，1表示自定义函数 */
    SYSTEM, CUSTOM;

    /**
     * getter method
     * 
     * @see FunctionType#type
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
     * @return {@link FunctionType}
     * @throws IllegalArgumentException
     */
    public static FunctionType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return FunctionType.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + FunctionType.class.getSimpleName() + " .", ex);
        }
    }
}
