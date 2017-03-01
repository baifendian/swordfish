package com.baifendian.swordfish.dao.mysql.enums;

/**
 * author: smile8
 * date:   24/09/2016
 * desc:
 */
public enum WriterModeType {
  OVERWRITE,
  INSERT,
  UPDATE,
  UPDATE_INSERT; // 修改

  public Integer getType() {
    return ordinal();
  }

  public static WriterModeType valueOfType(Integer type) throws IllegalArgumentException {
    if (type == null) {
      return null;
    }
    try {
      return WriterModeType.values()[type];
    } catch (Exception ex) {
      throw new IllegalArgumentException("Cannot convert " + type + " to " + WriterModeType.class.getSimpleName() + " .", ex);
    }
  }
}
