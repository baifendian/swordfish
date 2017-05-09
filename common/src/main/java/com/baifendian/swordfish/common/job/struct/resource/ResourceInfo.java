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
package com.baifendian.swordfish.common.job.struct.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

/**
 * 资源信息
 */
public class ResourceInfo {

  private ResScope scope;

  private String res;

  private String alias;

  public ResScope getScope() {
    return scope;
  }

  public void setScope(ResScope scope) {
    this.scope = scope;
  }

  public String getRes() {
    return res;
  }

  public void setRes(String res) {
    this.res = res;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  /**
   * 得到资源的符号连接
   *
   * @return
   */
  @JsonIgnore
  public String getSymbolicRes() {
    if (StringUtils.isNotEmpty(alias)) {
      return String.format("%s#%s", res, alias);
    }

    return res;
  }

  /**
   * scope 没有值时默认为 PROJECT
   */
  @JsonIgnore
  public boolean isProjectScope() {
    switch (scope) {
      case WORKFLOW:
        return false;
      default:
        return true;
    }
  }
}
