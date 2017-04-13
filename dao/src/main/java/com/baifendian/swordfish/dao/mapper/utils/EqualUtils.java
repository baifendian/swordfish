package com.baifendian.swordfish.dao.mapper.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;

/**
 * Created by caojingwei on 2017/4/13.
 */
public class EqualUtils {
  /**
   * 两个数组是否相等
   * @param a
   * @param b
   * @return
   */
  public static boolean equalLists(Collection<?> a, Collection<?> b){
    if (a == null && b == null){
      return true;
    }

    if((a == null && b != null) || a != null && b == null){
      return false;
    }

    return CollectionUtils.isEqualCollection(a,b);
  }
}
