package com.baifendian.swordfish.webserver.api.dto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

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
