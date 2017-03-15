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
