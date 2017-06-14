package com.baifendian.swordfish.common.enums;

/**
 * 写入模式
 */
public enum WriteMode {
  /**
   * 0 追加写入，1 覆盖写入
   */
  APPEND, OVERWRITE;

  /**
   * 获取写入模式中的hivesql
   *
   * @return
   */
  public String gethiveSql() {
    switch (this) {
      case APPEND:
        return "INTO";
      case OVERWRITE:
        return "OVERWRITE";
      default:
        return null;
    }
  }

  /**
   * 获取hdfs 对应的写入模式
   * @return
   */
  public String getHdfsType() {
    switch (this) {
      case APPEND:
        return "append";
      case OVERWRITE:
        return "nonConflict";
      default:
        return null;
    }
  }
}
