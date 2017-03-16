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

package com.baifendian.swordfish.dao.enums;

public enum DbType {
	 /**
     * 0 hive, 1 mysql, 2 mongodb, 3 hbase11, 4 redis
     */
    HIVE,MYSQL,MONGODB,HBASE11,REDIS;

  /**
   *
   * @return
   */
  public Integer getType() {
        return ordinal();
    }

    /**
     * 通过 type 获取枚举对象
     * <p>
     *
     * @param type
     * @return {@link DbType}
     * @throws IllegalArgumentException
     */
    public static DbType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return DbType.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + DbType.class.getSimpleName() + " .", ex);
        }
    }

    /**
     * 判断一个类型是否属于Enum
     * @param type
     * @return
     */
    public static boolean isInEnum(String type) {
        for (DbType dbType: values()) {
            if (dbType.name().equals(type)) {
                return true;
            }
        }

        return false;
    }
}
