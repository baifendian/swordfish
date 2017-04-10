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
package com.baifendian.swordfish.webserver.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * MR paramter scope
 */
public enum MRScope {
  PROJECT("project"),WORKFLOW("workflow");

  private String scope;

  private MRScope(String socpe){
    this.scope = socpe;
  }

  @JsonValue
  public String getScope() {
    return this.scope;
  }


  @JsonCreator
  public static MRScope valueOfType(String scope) throws IllegalArgumentException {
    if (scope == null) {
      return null;
    }
    for (MRScope mrScope : MRScope.values()) {
      if (mrScope.getScope().equals(scope)) {
        return mrScope;
      }
    }
    throw new IllegalArgumentException("Cannot convert " + scope + " to " + MRScope.class.getSimpleName() + " .");
  }

}
