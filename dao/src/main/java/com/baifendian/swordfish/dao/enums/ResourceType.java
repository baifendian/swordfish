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
 * 资源的类型 <p>
 *
 * @author : dsfan
 * @date : 2016年8月26日
 */
public enum ResourceType {
  /**
   * 0 jar包 , 1 文件, 2 构建包
   */
  JAR, FILE, ARCHIVE;

  /**
   * getter method
   *
   * @return the type
   * @see ResourceType#type
   */
  public Integer getType() {
    return ordinal();
  }

  /**
   * 通过 type 获取枚举对象 <p>
   *
   * @return {@link ResourceType}
   */
  public static ResourceType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    try {
      return ResourceType.values()[type];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + ResourceType.class.getSimpleName() + " .", ex);
    }
  }
}
