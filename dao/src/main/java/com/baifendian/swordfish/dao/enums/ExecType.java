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
 * workflow 等运行的类型 <p>
 */
public enum ExecType {

  /**
   * 0(直接运行) 1(调度的任务) 2(补数据的任务)
   **/
  DIRECT, SCHEDULER, COMPLEMENT_DATA;

  public Integer getType() {
    return ordinal();
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @param type
   * @return {@link FlowStatus}
   */
  public static ExecType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }

    try {
      return ExecType.values()[type];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + ExecType.class.getSimpleName() + " .", ex);
    }
  }
}
