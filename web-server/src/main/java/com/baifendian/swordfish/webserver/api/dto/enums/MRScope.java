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
  public static NodeType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    for (NodeType nodeType : NodeType.values()) {
      if (nodeType.getType().equals(type)) {
        return nodeType;
      }
    }
    throw new IllegalArgumentException("Cannot convert " + type + " to " + NodeType.class.getSimpleName() + " .");
  }

}
