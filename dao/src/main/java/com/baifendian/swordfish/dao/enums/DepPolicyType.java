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

public enum DepPolicyType {

  /**
   * 0.不依赖上一调度周期  1.自动依赖等待上一周期结束才能继续
   **/
  NO_DEP_PRE, DEP_PRE;

  /**
   * getter method
   *
   * @return the type
   * @see DepPolicyType
   */
  public Integer getType() {
    return ordinal();
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link DepPolicyType}
   */
  public static DepPolicyType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    try {
      return DepPolicyType.values()[type];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + DepPolicyType.class.getSimpleName() + " .", ex);
    }
  }
}
