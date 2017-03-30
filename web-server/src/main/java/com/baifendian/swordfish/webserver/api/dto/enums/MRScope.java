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
    return scope;
  }

  @JsonCreator
  public static MRScope valueOfName(String name) throws IllegalArgumentException {
    if (StringUtils.isEmpty(name)) {
      return null;
    }
    return MRScope.valueOf(name);
  }

}
