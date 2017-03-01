package com.baifendian.swordfish.common.utils;

import java.io.IOException;

/**
 * author: smile8
 * date:   01/12/2016
 * desc:
 */
public class StructuredArguments {

//  private static AtomicLong COUNTER = new AtomicLong(0);

  /**
   * @param key
   * @param value
   * @return
   */
  public static String keyValue(String key, String value) {
    return String.format("[%s=%s]", key, value/*, COUNTER.getAndIncrement()*/);
  }

  /**
   * 支持 jobId
   *
   * @param value
   * @return
   */
  public static String jobValue(String value) {
    return String.format("[jobId=%s]", value/*, COUNTER.getAndIncrement()*/);
  }

  public static void main(String[] args) throws IOException {
    System.out.println(jobValue("abc"));

    for (int i = 0; i < 10; ++i) {
      System.out.println(String.format("%d, %d", System.nanoTime(), System.currentTimeMillis()));
    }
  }
}