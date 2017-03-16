/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.common.utils;

import java.io.IOException;

/**
 * author: smile8 date:   01/12/2016 desc:
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