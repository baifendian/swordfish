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
 * 执行任务的节点依赖类型 <p>
 *
 * @author : dsfan
 * @date : 2016年8月31日
 */
public enum NodeDepType {
  /**
   * 0 表示仅执行 node ，1 表示执行 node 本身及其依赖，2 表示执行 node 及依赖当前 node 的 node
   */
  NODE_ONLY, DEPENDENCIES_NODE, NODE_DEPENDENCIES;

  /**
   * getter method
   *
   * @return the type
   * @see NodeDepType#type
   */
  @JsonValue
  public Integer getType() {
    return ordinal();
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link NodeDepType}
   */
  public static NodeDepType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    try {
      return NodeDepType.values()[type];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + NodeDepType.class.getSimpleName() + " .", ex);
    }
  }
}
