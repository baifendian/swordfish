package com.baifendian.swordfish.dao.mysql.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *  workflow 发布状态
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月27日
 */
public enum PubStatus {

    /**0.发布中  1.发布结束**/
    ING, END;
    /**
     * getter method
     *
     * @see PubStatus
     * @return the type
     */
    @JsonValue
    public Integer getType() {
        return ordinal();
    }

    /**
     * 通过 type 获取枚举对象
     * <p>
     *
     * @param type
     * @return {@link PubStatus}
     * @throws IllegalArgumentException
     */
    public static PubStatus valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return PubStatus.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + PubStatus.class.getSimpleName() + " .", ex);
        }
    }
}
