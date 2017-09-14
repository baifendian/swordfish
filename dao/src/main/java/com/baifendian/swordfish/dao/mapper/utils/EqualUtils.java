package com.baifendian.swordfish.dao.mapper.utils;

import java.util.Collection;
import org.apache.commons.collections4.CollectionUtils;

public class EqualUtils {

  /**
   * 两个数组是否相等
   *
   * @param a
   * @param b
   * @return
   */
  public static boolean equalLists(Collection<?> a, Collection<?> b) {
    if (a == null && b == null) {
      return true;
    }

    if ((a == null && b != null) || a != null && b == null) {
      return false;
    }

    return CollectionUtils.isEqualCollection(a, b);
  }
}
