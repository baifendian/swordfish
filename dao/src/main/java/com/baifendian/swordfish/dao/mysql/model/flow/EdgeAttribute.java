/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月9日
 * File Name      : EdgeAttribute.java
 */

package com.baifendian.swordfish.dao.mysql.model.flow;

/**
 * 边属性
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月9日
 */
public class EdgeAttribute {
    /** 边的方向 */
    private String direction;

    /** 对 start 来说, 可能还要指定里面的分组 id */
    private String fromGroupId;

    /** 对 end 来说, 可能还要指定 port 的分组 id */
    private String toPortGroupId;

    /**
     * getter method
     * 
     * @see EdgeAttribute#direction
     * @return the direction
     */
    public String getDirection() {
        return direction;
    }

    /**
     * setter method
     * 
     * @see EdgeAttribute#direction
     * @param direction
     *            the direction to set
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getFromGroupId() {
        return fromGroupId;
    }

    public void setFromGroupId(String fromGroupId) {
        this.fromGroupId = fromGroupId;
    }

    public String getToPortGroupId() {
        return toPortGroupId;
    }

    public void setToPortGroupId(String toPortGroupId) {
        this.toPortGroupId = toPortGroupId;
    }
}
