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

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 描述一个 workflow和node的运行状态 <p>
 */
public enum FlowStatus {
  /**
   * 0(初始化) 1(调度依赖任务中) 2(调度依赖资源中) 3(正在运行) 4(运行成功) 5(kill掉) 6(运行失败)
   **/
  INIT, WAITING_DEP, WAITING_RES, RUNNING, SUCCESS, KILL, FAILED;

  @JsonValue
  public Integer getType() {
    return ordinal();
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link FlowStatus}
   */
  public static FlowStatus valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    try {
      return FlowStatus.values()[type];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + FlowStatus.class.getSimpleName() + " .", ex);
    }
  }

  /**
   * 判断是否成功状态 <p>
   *
   * @return 是否成功状态
   */
  public boolean typeIsSuccess() {
    return this == SUCCESS;
  }

  public boolean typeIsFailure() {
    return this != SUCCESS;
  }

  /**
   * 判断是否终止状态 <p>
   *
   * @return 是否终止状态
   */
  public boolean typeIsFinished() {
    return this == SUCCESS || this == KILL || this == FAILED;
  }
}
