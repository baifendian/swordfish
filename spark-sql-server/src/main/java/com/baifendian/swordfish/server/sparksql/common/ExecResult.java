package com.baifendian.swordfish.server.sparksql.common;

import java.util.List;

/**
 * 执行结果 <p>
 */
public class ExecResult {
  /**
   * 执行语句的索引，从0开始
   */
  private int index;

  /**
   * 执行的语句
   */
  private String stm;

  /**
   * 语句的执行结果
   */
  private FlowStatus status;

  /**
   * 返回的表头
   */
  private List<String> titles;

  /**
   * 返回的数据
   */
  private List<List<String>> values;
}
