package com.baifendian.swordfish.common.job.struct.node.impexp.reader;

/**
 * 读接口
 */
public interface Reader {
  /**
   * 校验reader是否合法
   * @return
   */
  default boolean checkValid(){
    return true;
  }
}
