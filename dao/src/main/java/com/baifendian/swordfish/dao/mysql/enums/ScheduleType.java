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

/**
 *  调度的类型
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月14日
 */
public enum  ScheduleType {
    /**
     * 0 分钟 ， 1 小时 ，2 天， 3 周， 4 月
     */
    MINUTE, HOUR , DAY, WEEK, MONTH;

    /**
     * getter method
     *
     * @see ScheduleType
     * @return the type
     */
    public Integer getType() {
        return ordinal();
    }

    /**
     * 通过 type 获取枚举对象
     * <p>
     *
     * @param type
     * @return {@link ScheduleType}
     * @throws IllegalArgumentException
     */
    public static ScheduleType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return ScheduleType.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + ScheduleType.class.getSimpleName() + " .", ex);
        }
    }
}
