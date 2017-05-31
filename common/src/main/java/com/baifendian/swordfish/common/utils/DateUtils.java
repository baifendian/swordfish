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
import org.apache.commons.lang3.StringUtils;
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
   * 获取系统默认时区
   */
  public static String getDefaultTimeZome() {
    return TimeZone.getDefault().getID();
  }

  /**
   * 获取当前时刻的格式化的日期字符串
   *
   * @return 日期字符串
   */
  public static String now() {
    return now(Constants.BASE_DATETIME_FORMAT);
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
    return format(date, Constants.BASE_DATETIME_FORMAT);
  }

  /**
   * 获取格式化的日期字符串
   *
   * @param date
   * @param formatString
   * @return
   */
  public static String format(Date date, String formatString) {
    if (date == null) {
      return StringUtils.EMPTY;
    }

    FastDateFormat format = FastDateFormat.getInstance(formatString);
    return format.format(date);
  }

  /**
   * 通过字符串获取日期
   *
   * @param dateStr
   * @param formatString
   * @return
   */
  public static Date parse(String dateStr, String formatString) {
    try {
      DateFormat formatter = new SimpleDateFormat(formatString);

      return formatter.parse(dateStr);
    } catch (ParseException e) {
      throw new RuntimeException("Time parse failed exception", e);
    }
  }

  /**
   * 获取两个日期相差多少天
   *
   * @param d1
   * @param d2
   * @return
   */
  public static long diffDays(Date d1, Date d2) {
    return (long) Math.ceil(diffHours(d1, d2) / 24.0);
  }

  /**
   * 获取两个日期相差多少小时
   *
   * @param d1
   * @param d2
   * @return
   */
  public static long diffHours(Date d1, Date d2) {
    return (long) Math.ceil(diffMin(d1, d2) / 60.0);
  }

  /**
   * 获取两个日期相差多少分钟
   *
   * @param d1
   * @param d2
   * @return
   */
  public static long diffMin(Date d1, Date d2) {
    return (long) Math.ceil(diffSec(d1, d2) / 60.0);
  }

  /**
   * 获取两个日期之间相差多少秒
   *
   * @param d1
   * @param d2
   * @return
   */
  public static long diffSec(Date d1, Date d2) {
    return (long) Math.ceil(diffMs(d1, d2) / 1000.0);
  }

  /**
   * 获取两个日期之间相差多少毫秒
   *
   * @param d1
   * @param d2
   * @return
   */
  public static long diffMs(Date d1, Date d2) {
    return Math.abs(d1.getTime() - d2.getTime());
  }

  /**
   * 比较两个日期大小
   *
   * @param future
   * @param old
   * @return
   */
  public static boolean compare(Date future, Date old) {
    return future.getTime() > old.getTime();
  }
}
