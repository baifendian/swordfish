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

/**
 * workflow 类型 <p>
 *
 * @author : dsfan
 * @date : 2016年8月27日
 */
public enum FlowType {

  /**
   * 0 短任务, 1 长任务, 2 ETL, 3 即席查询 , 4 数据质量 , 5 文件导入
   **/
  SHORT, LONG, ETL, ADHOC, DQ, FILE_IMPORT;

  /**
   * getter method
   *
   * @return the type
   * @see FlowType
   */
  public Integer getType() {
    return ordinal();
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link FlowType}
   */
  public static FlowType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    try {
      return FlowType.values()[type];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + FlowType.class.getSimpleName() + " .", ex);
    }
  }
}
