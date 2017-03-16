/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baifendian.swordfish.dao.model;

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
