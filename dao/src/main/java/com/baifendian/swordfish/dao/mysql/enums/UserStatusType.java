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
 * Created by shuanghu
 * date: 17-2-6.
 */
public enum UserStatusType {
    NORMAL(0),
    LOCKED(1),
    DELETED(2);

    private int status;

    UserStatusType(int val){
        this.status = val;
    }

    public Integer getType() {
        return status;
    }

    public static UserStatusType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        for (UserStatusType statusType : UserStatusType.values()) {
            if (statusType.getType().equals(type)) {
                return statusType;
            }
        }
        throw new IllegalArgumentException("Cannot convert " + type + " to " + UserStatusType.class.getSimpleName() + " .");
    }
}
