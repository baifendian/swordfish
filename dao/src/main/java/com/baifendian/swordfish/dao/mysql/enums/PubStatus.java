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

package com.baifendian.swordfish.dao.mysql.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *  workflow 发布状态
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月27日
 */
public enum PubStatus {

    /**0.发布中  1.发布结束**/
    ING, END;
    /**
     * getter method
     *
     * @see PubStatus
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
     * @return {@link PubStatus}
     * @throws IllegalArgumentException
     */
    public static PubStatus valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return PubStatus.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + PubStatus.class.getSimpleName() + " .", ex);
        }
    }
}
