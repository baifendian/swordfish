/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月26日
 * File Name      : ResourceType.java
 */

package com.baifendian.swordfish.dao.mysql.enums;

/**
 * 资源的类型
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月26日
 */
public enum ResourceType {
    /**
     * 0 jar包 , 1 文件, 2 构建包
     */
    JAR, FILE, ARCHIVE;

    /**
     * getter method
     * 
     * @see ResourceType#type
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
     * @return {@link ResourceType}
     * @throws IllegalArgumentException
     */
    public static ResourceType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return ResourceType.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + ResourceType.class.getSimpleName() + " .", ex);
        }
    }
}
