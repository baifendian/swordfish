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
 * 报警类型
 */
public enum NotifyType {
  /**
   *  0-NONE-都不发 1-SUCCESS-成功发，2-FAILURE-失败发，3-ALL-成功或失败都发
   */
  NONE,SUCCESS,FAILURE,ALL;

  public Integer getType() {
    return ordinal();
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link NotifyType}
   */
  public static NotifyType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    try {
      return NotifyType.values()[type];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + NotifyType.class.getSimpleName() + " .", ex);
    }
  }

  public boolean typeIsSendMail(){
    return this == SUCCESS || this == FAILURE || this == ALL;
  }

  public boolean typeIsSendFailureMail(){
    return this == FAILURE || this == ALL;
  }

  public boolean typeIsSendSuccessMail(){
    return this == SUCCESS || this == ALL;
  }
}
