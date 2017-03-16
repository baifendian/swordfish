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

import com.baifendian.swordfish.dao.enums.NodeType;
import com.baifendian.swordfish.dao.enums.NodeTypeHandler;

import org.apache.ibatis.type.EnumOrdinalTypeHandler;

/**
 * mybatis enum类型字段的工具 <p>
 *
 * @author : dsfan
 * @date : 2016年8月26日
 */
public class EnumFieldUtil {

  /**
   * 生成enum字段的字符串 <p>
   *
   * @return 字段字符串
   */
  public static String genFieldStr(String field, Class<?> enumClass) {
    if (enumClass.equals(NodeType.class)) { // NodeType类型进行特殊处理
      return "#{" + field + ",javaType=" + enumClass.getName() + ",typeHandler=" + NodeTypeHandler.class.getName() + "}";
    }
    return "#{" + field + ",javaType=" + enumClass.getName() + ",typeHandler=" + EnumOrdinalTypeHandler.class.getName() + "}";
  }

  /**
   * 生成enum字段的字符串(MessageFormat特殊字符) <p>
   *
   * @return 字段字符串
   */
  public static String genFieldSpecialStr(String field, Class<?> enumClass) {
    if (enumClass.equals(NodeType.class)) { // NodeType类型进行特殊处理
      return "#'{'" + field + ",javaType=" + enumClass.getName() + ",typeHandler=" + NodeTypeHandler.class.getName() + "}";
    }
    return "#'{'" + field + ",javaType=" + enumClass.getName() + ",typeHandler=" + EnumOrdinalTypeHandler.class.getName() + "}";
  }

}
