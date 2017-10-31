package com.baifendian.swordfish.webserver.dto;

public class IsProhibitDto {
  private int type;

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public IsProhibitDto(int type) {
    this.type = type;
  }

  public IsProhibitDto() {
  }
}
