/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月10日
 * File Name      : Property.java
 */

package com.baifendian.swordfish.dao.mysql.model.flow.params;

/**
 * 配置信息
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月10日
 */
public class Property {
    /** 配置名 */
    private String prop;

    /** 配置值 */
    private String value;

    public Property(){}

    public Property(String prop, String value){
        this.prop = prop;
        this.value = value;
    }
    /**
     * getter method
     * 
     * @see Property#prop
     * @return the prop
     */
    public String getProp() {
        return prop;
    }

    /**
     * setter method
     * 
     * @see Property#prop
     * @param prop
     *            the prop to set
     */
    public void setProp(String prop) {
        this.prop = prop;
    }

    /**
     * getter method
     * 
     * @see Property#value
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * setter method
     * 
     * @see Property#value
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
