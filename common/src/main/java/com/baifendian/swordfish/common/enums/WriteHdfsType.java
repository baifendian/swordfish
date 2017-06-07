package com.baifendian.swordfish.common.enums;

/**
 * 读hdfs文件类型
 */
public enum WriteHdfsType {
  ORC("orc"),TEXT("text");

  private String type;

  WriteHdfsType(String type) {
    this.type = type;
  }
}
