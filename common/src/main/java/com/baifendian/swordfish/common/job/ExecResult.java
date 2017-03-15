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

package com.baifendian.swordfish.common.job;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行结果
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月6日
 */
public class ExecResult {
    /** 执行语句的索引，从0开始 */
    private int index;

    /** 执行的语句 */
    private String stm;

    /** 语句的执行结果 */
    private FlowStatus status;

    /** 返回的表头 */
    private List<String> titles;

    /** 返回的数据 */
    private List<List<String>> values;

    /** 返回的日志 */
    private List<String> logs = new ArrayList<>();

    /**
     * getter method
     * 
     * @see ExecResult#index
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * setter method
     * 
     * @see ExecResult#index
     * @param index
     *            the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * getter method
     * 
     * @see ExecResult#stm
     * @return the stm
     */
    public String getStm() {
        return stm;
    }

    /**
     * setter method
     * 
     * @see ExecResult#stm
     * @param stm
     *            the stm to set
     */
    public void setStm(String stm) {
        this.stm = stm;
    }

    /**
     * getter method
     * 
     * @see ExecResult#status
     * @return the status
     */
    public FlowStatus getStatus() {
        return status;
    }

    /**
     * setter method
     * 
     * @see ExecResult#status
     * @param status
     *            the status to set
     */
    public void setStatus(FlowStatus status) {
        this.status = status;
    }

    /**
     * getter method
     * 
     * @see ExecResult#titles
     * @return the titles
     */
    public List<String> getTitles() {
        return titles;
    }

    /**
     * setter method
     * 
     * @see ExecResult#titles
     * @param titles
     *            the titles to set
     */
    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    /**
     * getter method
     * 
     * @see ExecResult#values
     * @return the values
     */
    public List<List<String>> getValues() {
        return values;
    }

    /**
     * setter method
     * 
     * @see ExecResult#values
     * @param values
     *            the values to set
     */
    public void setValues(List<List<String>> values) {
        this.values = values;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
}
