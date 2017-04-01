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

import org.apache.commons.lang.StringUtils;

public enum DbType {
  /**
   * 0 mysql, 1 oracle, 2 sqlserver, 3 mongodb, 4 hbase094x, 5 hbase11x, 6 ftp
   */
  MYSQL, ORACLE, SQLSERVER, MONOGODB, HBASE094X, HBASE11X, FTP;

  /**
   *
   * @return
   */
  public Integer getType() {
    return ordinal();
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link DbType}
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
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link DbType}
   */
  public static DbType valueOfType(String type) throws IllegalArgumentException {
    if (StringUtils.isEmpty(type)) {
      return null;
    }

    try {
      return DbType.valueOf(type);
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + DbType.class.getSimpleName() + " .", ex);
    }
  }


  /**
   * 判断一个类型是否属于Enum
   */
  public static boolean isInEnum(String type) {
    for (DbType dbType : values()) {
      if (dbType.name().equals(type)) {
        return true;
      }
    }

    return false;
  }
}
