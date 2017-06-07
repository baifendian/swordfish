package com.baifendian.swordfish.common.job.struct.node.impexp.writer;

/**
 * 写接口
 */
public interface Writer {
  /**
   * 校验writer是否合法
   * @return
   */
  default boolean checkValid(){
    return true;
  }
}
