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
