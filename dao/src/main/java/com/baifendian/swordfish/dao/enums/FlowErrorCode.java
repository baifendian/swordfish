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
 * workflow 运行失败时的详细错误码 <p>
 *
 * @author : dsfan
 * @date : 2017年1月3日
 */
public enum FlowErrorCode {
  /**
   * 0(成功) 1(依赖自身调度的上一周期运行失败) 2(依赖的工作流运行失败) 3(运行失败) 4(被人工 KILL)
   **/
  SUCCESS, DEP_PRE_FAILED, DEP_FAILED, EXEC_FAILED, KILL;

  public Integer getType() {
    return ordinal();
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link FlowErrorCode}
   */
  public static FlowErrorCode valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    try {
      return FlowErrorCode.values()[type];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + FlowErrorCode.class.getSimpleName() + " .", ex);
    }
  }
}
