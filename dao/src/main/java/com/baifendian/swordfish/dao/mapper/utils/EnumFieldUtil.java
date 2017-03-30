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
package com.baifendian.swordfish.dao.mapper.utils;

import org.apache.ibatis.type.EnumOrdinalTypeHandler;

public class EnumFieldUtil {

  /**
   * 生成 enums 字段的字符串
   *
   * @param field
   * @param enumClass
   * @return
   */
  public static String genFieldStr(String field, Class<?> enumClass) {
    return "#{" + field + ",javaType=" + enumClass.getName() + ",typeHandler=" + EnumOrdinalTypeHandler.class.getName() + "}";
  }

  /**
   * 生成enum字段的字符串(MessageFormat特殊字符) <p>
   *
   * @return 字段字符串
   */
  public static String genFieldSpecialStr(String field, Class<?> enumClass) {
    return "#'{'" + field + ",javaType=" + enumClass.getName() + ",typeHandler=" + EnumOrdinalTypeHandler.class.getName() + "}";
  }

}
