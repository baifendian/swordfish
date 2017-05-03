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
package com.baifendian.swordfish.execserver.utils;

/**
 * 日志记录工具类 <p>
 */
public class LoggerUtil {

  /**
   * 分隔符
   */
  public static final String SEPARATOR = "_";

  /**
   * 生成 jobId <p>
   *
   * @return jobId
   */
  public static String genJobId(String prefix, long execId, String nodeName) {
    if (nodeName == null) {
      return prefix + SEPARATOR + execId;
    }
    return prefix + SEPARATOR + execId + SEPARATOR + nodeName;
  }

}
