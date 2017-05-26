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
package com.baifendian.swordfish.common.job.struct.node;

import org.apache.commons.lang.StringUtils;

public class JobType {

  public static final String HQL = "HQL";
  public static final String MR = "MR";
  public static final String SHELL = "SHELL";
  public static final String SPARK = "SPARK";
  public static final String VIRTUAL = "VIRTUAL";
  public static final String SPARK_STREAMING = "SPARK_STREAMING"; // 长任务类型
  public static final String UPLOAD = "UPLOAD";

  /**
   * 判断是否是长任务
   */
  public static boolean isLongJob(String job) {
    if (StringUtils.isEmpty(job)) {
      return false;
    }

    switch (job) {
      case SPARK_STREAMING:
        return true;
      default:
        return false;
    }
  }
}
