package com.baifendian.swordfish.dao.mysql.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *  调度状态
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月27日
 */
public enum  ScheduleStatus {

    OFFLINE, ONLINE;
    /**
     * getter method
     *
     * @see ScheduleStatus
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
     * @return {@link ScheduleStatus}
     * @throws IllegalArgumentException
     */
    public static ScheduleStatus valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return ScheduleStatus.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + ScheduleStatus.class.getSimpleName() + " .", ex);
        }
    }
}
