package com.baifendian.swordfish.common.enums;

/**
 * 读hdfs文件类型
 */
public enum WriteHdfsType {
  ORC("orcfile"),TEXT("textfile");

  private String type;

  WriteHdfsType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
