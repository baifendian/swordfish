package com.baifendian.swordfish.common.enums;

/**
 * 写入模式
 */
public enum WriteMode {
  /**
   * 0 追加写入，1 覆盖写入
   */
  APPEND, OVERWRITER;

  /**
   * 获取写入模式中的hivesql
   * @return
   */
  public String gethiveSql() {
    switch (this) {
      case APPEND:
        return "INTO";
      case OVERWRITER:
        return "OVERWRITE";
      default:
        return null;
    }
  }
}
