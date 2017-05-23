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

import com.baifendian.swordfish.common.utils.http.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 日志记录工具类 <p>
 */
public class LoggerUtil {

  /**
   * 生成 jobId 并返回
   *
   * @param prefix
   * @param execId
   * @param nodeName
   * @return
   */
  public static String genJobId(String prefix, long execId, String nodeName) {
    if (StringUtils.isEmpty(nodeName)) {
      return String.format("%s_%s", prefix, execId);
    }

    String postfix = HttpUtil.getMd5(nodeName).substring(0, 8);

    return String.format("%s_%s_%s", prefix, execId, postfix);
  }
}
