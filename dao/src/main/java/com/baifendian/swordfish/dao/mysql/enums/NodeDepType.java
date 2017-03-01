/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月31日
 * File Name      : NodeDepType.java
 */

package com.baifendian.swordfish.dao.mysql.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 执行任务的节点依赖类型
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月31日
 */
public enum NodeDepType {
    /** 0 表示仅执行 node ，1 表示执行 node 本身及其依赖，2 表示执行 node 及依赖当前 node 的 node */
    NODE_ONLY, DEPENDENCIES_NODE, NODE_DEPENDENCIES;

    /**
     * getter method
     * 
     * @see NodeDepType#type
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
     * @return {@link NodeDepType}
     * @throws IllegalArgumentException
     */
    public static NodeDepType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return NodeDepType.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + NodeDepType.class.getSimpleName() + " .", ex);
        }
    }
}
