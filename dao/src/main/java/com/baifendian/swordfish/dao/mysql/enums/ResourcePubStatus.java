/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月26日
 * File Name      : ResourcePubStatus.java
 */

package com.baifendian.swordfish.dao.mysql.enums;

/**
 * 资源发布状态
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月26日
 */
public enum ResourcePubStatus {
    /** 0-未发布，1-发布中，2-已发布且资源为最新，3-已发布但资源不是最新（资源已被修改，但没发布修改内容） */
    UNPUB, PUB_ING, PUBED_NEW, PUBED_OLD;

    /**
     * getter method
     * 
     * @see ResourcePubStatus#status
     * @return the status
     */
    public Integer getStatus() {
        return ordinal();
    }

    /**
     * 通过 status 获取枚举对象
     * <p>
     *
     * @param status
     * @return {@link ResourcePubStatus}
     * @throws IllegalArgumentException
     */
    public static ResourcePubStatus valueOfStatus(Integer status) throws IllegalArgumentException {
        if (status == null) {
            return null;
        }
        try {
            return ResourcePubStatus.values()[status];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + status + " to " + ResourcePubStatus.class.getSimpleName() + " .", ex);
        }
    }
}
