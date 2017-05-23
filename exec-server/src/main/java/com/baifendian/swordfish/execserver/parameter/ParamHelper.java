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
package com.baifendian.swordfish.execserver.parameter;

import com.baifendian.swordfish.execserver.parameter.placeholder.PlaceholderUtil;
import com.baifendian.swordfish.execserver.parameter.placeholder.TimePlaceholderUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * 参数解析帮助类 <p>
 */
public class ParamHelper {
  /**
   * LOGGER
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ParamHelper.class);

  /**
   * 替换参数的占位符
   *
   * @param text
   * @param paramMap
   * @return
   */
  public static String resolvePlaceholders(String text, Map<String, String> paramMap) {
    if (StringUtils.isEmpty(text)) {
      return text;
    }

    // 得到当前的时间, 调度执行时刻的时间
    String cycTimeStr = paramMap.get(SystemParamManager.CYC_TIME);
    Date cycTime = getCycTime(cycTimeStr);

    // 替换 ${...} 形式
    text = PlaceholderUtil.resolvePlaceholders(text, paramMap, true);

    // 替换时间的形式: $[...]
    if (cycTime != null) {
      text = TimePlaceholderUtil.resolvePlaceholders(text, cycTime, true);
    }

    return text;
  }

  /**
   * 获取 "当前时间参数信息" 信息
   *
   * @param cycTimeStr
   * @return
   */
  private static Date getCycTime(String cycTimeStr) {
    Date cycTime = null;

    if (StringUtils.isNotEmpty(cycTimeStr)) {
      try {
        cycTime = DateUtils.parseDate(cycTimeStr, new String[]{SystemParamManager.TIME_FORMAT});
      } catch (ParseException e) {
        LOGGER.error(String.format("parse time exception: %s", cycTimeStr), e);
      }
    } else {
      cycTime = new Date();
    }

    return cycTime;
  }
}
