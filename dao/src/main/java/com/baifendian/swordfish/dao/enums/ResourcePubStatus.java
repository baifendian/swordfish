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
 * 资源发布状态 <p>
 *
 * @author : dsfan
 * @date : 2016年9月26日
 */
public enum ResourcePubStatus {
  /**
   * 0-未发布，1-发布中，2-已发布且资源为最新，3-已发布但资源不是最新（资源已被修改，但没发布修改内容）
   */
  UNPUB, PUB_ING, PUBED_NEW, PUBED_OLD;

  /**
   * getter method
   *
   * @return the status
   * @see ResourcePubStatus#status
   */
  public Integer getStatus() {
    return ordinal();
  }

  /**
   * 通过 status 获取枚举对象 <p>
   *
   * @return {@link ResourcePubStatus}
   */
  public static ResourcePubStatus valueOfStatus(Integer status) throws IllegalArgumentException {
    if (status == null) {
      return null;
    }
    try {
      return ResourcePubStatus.values()[status];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + status + " to " + ResourcePubStatus.class.getSimpleName() + " .", ex);
    }
  }
}
