package com.baifendian.swordfish.common.enums;

/**
 * mongo 写入模式枚举
 */
public enum MongoWriteMode {
  INSERT, UPDATE, UPSET;

  /**
   * 判断是否需要upsetKey
   * @return
   */
  public boolean hasUpsetKey(){
    return this == UPDATE || this == UPDATE;
  }
}
