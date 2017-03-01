/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月12日
 * File Name      : FlowParam.java
 */

package com.baifendian.swordfish.dao.mysql.model;

/**
 * workflow 的自定义参数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月12日
 */
public class FlowParam {

    /** workflow id */
    private int flowId;

    /** 参数名 */
    private String key;

    /** 参数值 */
    private String value;

    @Override
    public String toString() {
        return "FlowParam [flowId=" + flowId + ", key=" + key + ", value=" + value + "]";
    }

    /**
     * getter method
     * 
     * @see FlowParam#flowId
     * @return the flowId
     */
    public int getFlowId() {
        return flowId;
    }

    /**
     * setter method
     * 
     * @see FlowParam#flowId
     * @param flowId
     *            the flowId to set
     */
    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    /**
     * getter method
     * 
     * @see FlowParam#key
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * setter method
     * 
     * @see FlowParam#key
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * getter method
     * 
     * @see FlowParam#value
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * setter method
     * 
     * @see FlowParam#value
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
