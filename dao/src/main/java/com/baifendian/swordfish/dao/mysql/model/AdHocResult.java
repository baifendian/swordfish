/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月6日
 * File Name      : AdHocResult.java
 */

package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.common.job.FlowStatus;

import java.sql.Timestamp;

/**
 * 即席查询执行结果
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月6日
 */
public class AdHocResult {
    /** 执行 id */
    private long execId;

    /** 节点 id */
    private int nodeId;

    /** 执行语句在查询语句数组中的索引 */
    private int index;

    private FlowStatus status;

    /** 执行语句 */
    private String stm;

    /** 执行结果（JsonObject） */
    private String result;

    /** 创建时间 */
    private Timestamp createTime;

    /**
     * getter method
     * 
     * @see AdHocResult#execId
     * @return the execId
     */
    public long getExecId() {
        return execId;
    }

    /**
     * setter method
     * 
     * @see AdHocResult#execId
     * @param execId
     *            the execId to set
     */
    public void setExecId(long execId) {
        this.execId = execId;
    }

    /**
     * getter method
     * 
     * @see AdHocResult#nodeId
     * @return the nodeId
     */
    public int getNodeId() {
        return nodeId;
    }

    /**
     * setter method
     * 
     * @see AdHocResult#nodeId
     * @param nodeId
     *            the nodeId to set
     */
    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * getter method
     * 
     * @see AdHocResult#index
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * setter method
     * 
     * @see AdHocResult#index
     * @param index
     *            the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public FlowStatus getStatus() {
        return status;
    }

    public void setStatus(FlowStatus status) {
        this.status = status;
    }

    /**
     * getter method
     * 
     * @see AdHocResult#stm
     * @return the stm
     */
    public String getStm() {
        return stm;
    }

    /**
     * setter method
     * 
     * @see AdHocResult#stm
     * @param stm
     *            the stm to set
     */
    public void setStm(String stm) {
        this.stm = stm;
    }

    /**
     * getter method
     * 
     * @see AdHocResult#result
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * setter method
     * 
     * @see AdHocResult#result
     * @param result
     *            the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * getter method
     * 
     * @see AdHocResult#createTime
     * @return the createTime
     */
    public Timestamp getCreateTime() {
        return createTime;
    }

    /**
     * setter method
     * 
     * @see AdHocResult#createTime
     * @param createTime
     *            the createTime to set
     */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

}
