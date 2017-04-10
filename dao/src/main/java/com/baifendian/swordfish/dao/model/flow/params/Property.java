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
package com.baifendian.swordfish.dao.model.flow.params;

import java.util.Objects;

public class Property {
  /**
   * 配置名
   */
  private String prop;

  /**
   * 配置值
   */
  private String value;

  public Property() {
  }

  public Property(String prop, String value) {
    this.prop = prop;
    this.value = value;
  }

  /**
   * getter method
   *
   * @return the prop
   * @see Property#prop
   */
  public String getProp() {
    return prop;
  }

  /**
   * setter method
   *
   * @param prop the prop to set
   * @see Property#prop
   */
  public void setProp(String prop) {
    this.prop = prop;
  }

  /**
   * getter method
   *
   * @return the value
   * @see Property#value
   */
  public String getValue() {
    return value;
  }

  /**
   * setter method
   *
   * @param value the value to set
   * @see Property#value
   */
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Property property = (Property) o;
    return Objects.equals(prop, property.prop) &&
            Objects.equals(value, property.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(prop, value);
  }
}
