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
 * 描述一个 workflow 和 node 的运行状态 <p>
 */
public enum FlowStatus {
  /**
   * 0(初始化) 1(调度依赖任务中) 2(调度依赖资源中) 3(正在运行) 4(运行成功) 5(kill掉) 6(运行失败) 7(依赖失败) 8(暂停)
   **/
  INIT, WAITING_DEP, WAITING_RES, RUNNING, SUCCESS, KILL, FAILED, DEP_FAILED, INACTIVE;

  /**
   * 判断是否成功状态 <p>
   *
   * @return 是否成功状态
   */
  public boolean typeIsSuccess() {
    return this == SUCCESS;
  }

  /**
   * 判断是否失败状态
   *
   * @return 是否失败状态
   */
  public boolean typeIsFailure() {
    return this == KILL || this == FAILED || this == DEP_FAILED;
  }

  /**
   * 判断是否终止状态 <p>
   *
   * @return 是否终止状态
   */
  public boolean typeIsFinished() {
    return typeIsSuccess() || typeIsFailure();
  }

  /**
   * 判断是否非终止状态
   *
   * @return
   */
  public boolean typeIsNotFinished() {
    return !typeIsFinished();
  }
}
