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

import com.baifendian.swordfish.common.consts.Constants;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间操作工具类 <p>
 */
public class DateUtils {

  /**
   * 日期格式
   */
  private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(Constants.BASE_DATETIME_FORMAT);

  /**
   * 获取系统默认时区 <p>
   */
  public static String getDefaultTimeZome() {
    return TimeZone.getDefault().getID();
  }

  /**
   * 获取当前时刻的格式化的日期字符串 <p>
   *
   * @return 日期字符串
   */
  public static String now() {
    return DATE_FORMAT.format(new Date());
  }

  /**
   * 获取当前时间指定格式的日期字符串 <p>
   *
   * @param format
   * @return 日期字符串
   */
  public static String now(String format) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    return dateFormat.format(new Date());
  }

  /**
   * 获取默认的格式化的日期字符串 <p>
   *
   * @param date
   * @return 日期字符串
   */
  public static String defaultFormat(Date date) {
    return DATE_FORMAT.format(date);
  }

  /**
   * 获取格式化的日期字符串
   *
   * @param date
   * @param formatString
   * @return 日期字符串
   */
  public static String format(Date date, String formatString) {
    FastDateFormat format = FastDateFormat.getInstance(formatString);
    return format.format(date);
  }

  /**
   * 通过字符串获取日期
   *
   * @param dateStr
   * @return 日期
   */

  public static Date parse(String dateStr) {
    try {
      DateFormat formatter = new SimpleDateFormat(Constants.BASE_DATETIME_FORMAT);

      return formatter.parse(dateStr);
    } catch (ParseException e) {
      throw new RuntimeException("Time parse failed exception", e);
    }
  }

  /**
   * 通过字符串获取日期
   *
   * @param dateStr
   * @param formatString
   * @return 日期
   */

  public static Date parse(String dateStr, String formatString) {
    try {
      DateFormat formatter = new SimpleDateFormat(formatString);

      return formatter.parse(dateStr);
    } catch (ParseException e) {
      throw new RuntimeException("Time parse failed exception", e);
    }
  }
}
