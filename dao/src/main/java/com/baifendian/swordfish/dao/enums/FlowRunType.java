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

import com.baifendian.swordfish.common.job.FlowStatus;

/**
 * workflow 等运行的类型 <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月30日
 */
public enum FlowRunType {

  /**
   * 0(开发测试) 1(调度的任务) 2(补数据的任务) 3(流式)
   **/
  DIRECT_RUN, DISPATCH, ADD_DATA, STREAMING;

  public Integer getType() {
    return ordinal();
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link FlowStatus}
   */
  public static FlowRunType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    try {
      return FlowRunType.values()[type];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + FlowRunType.class.getSimpleName() + " .", ex);
    }
  }
}
