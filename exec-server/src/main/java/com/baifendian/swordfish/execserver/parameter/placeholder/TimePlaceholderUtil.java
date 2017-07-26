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
package com.baifendian.swordfish.execserver.parameter.placeholder;

import com.baifendian.swordfish.common.utils.DateUtils;
import com.baifendian.swordfish.execserver.parameter.SystemParamManager;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PropertyPlaceholderHelper;


/**
 * 时间占位符工具类 <p>
 *
 * @see <a href="https://github.com/baifendian/swordfish/wiki/parameter-desc">系统参数定义</a>
 */
public class TimePlaceholderUtil {

  /**
   * LOGGER
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(TimePlaceholderUtil.class);

  /**
   * 待替换位置的前缀 : "$["
   */
  public static final String PLACEHOLDER_PREFIX = "$[";

  /**
   * 待替换位置的后缀 :"]"
   */
  public static final String PLACEHOLDER_SUFFIX = "]";

  /**
   * 键与默认值的分割符（null 表示不支持）
   */
  public static final String VALUE_SEPARATOR = null;

  /**
   * 严格的替换工具实现，待替换的位置没有获取到对应值时，则抛出异常
   */
  private static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper(
      PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, false);

  /**
   * 非严格的替换工具实现，待替换的位置没有获取到对应值时，则忽略当前位置，继续替换下一个位置
   */
  private static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper(
      PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, true);

  /**
   * add_months
   */
  private static final String ADD_MONTHS = "add_months";

  /**
   * month_begin
   */
  private static final String MONTH_BEGIN = "month_begin";

  /**
   * month_end
   */
  private static final String MONTH_END = "month_end";

  /**
   * week_begin
   */
  private static final String WEEK_BEGIN = "week_begin";

  /**
   * week_end
   */
  private static final String WEEK_END = "week_end";

  /**
   * timestamp
   */
  private static final String TIMESTAMP = "timestamp";

  /**
   * 替换文本的占位符 <p>
   *
   * @param text 待替换文本
   * @param date 需要自定义的日期
   * @param ignoreUnresolvablePlaceholders 是否忽略没有匹配到值的占位符
   * @return 替换后的字符串
   */
  public static String resolvePlaceholders(String text, Date date,
      boolean ignoreUnresolvablePlaceholders) {
    PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper
        : strictHelper);
    return helper.replacePlaceholders(text, new TimePlaceholderResolver(text, date));
  }

  /**
   * 替换文本的占位符（空替换） <p>
   *
   * @return 替换后的字符串
   */
  public static String resolvePlaceholdersConst(String text, String constValue) {
    return nonStrictHelper.replacePlaceholders(text, new ConstPlaceholderResolver(constValue));
  }

  /**
   * 占位符替换的处理 <p>
   */
  private static class TimePlaceholderResolver implements
      PropertyPlaceholderHelper.PlaceholderResolver {

    private final String text;

    private final Date date;

    public TimePlaceholderResolver(String text, Date date) {
      this.text = text;
      this.date = date;
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
      try {
        String propVal = customTime(placeholderName, date);

        return propVal;
      } catch (Throwable ex) {
        LOGGER
            .error("Could not resolve placeholder '" + placeholderName + "' in [" + text + "]", ex);
        return null;
      }
    }
  }

  /**
   * 占位符替换的处理（空字符串替换占位符） <p>
   */
  private static class ConstPlaceholderResolver implements
      PropertyPlaceholderHelper.PlaceholderResolver {

    private final String constValue;

    public ConstPlaceholderResolver(String constValue) {
      this.constValue = constValue;
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
      return constValue;
    }
  }

  /**
   * 计算自定义时间 <p>
   *
   * @param expr 要替换的表达式, 详解 {@link https://github.com/baifendian/swordfish/wiki/parameter-desc#toc0|swordfish}
   * @param date 日期
   * @return 自定义的时间
   */
  private static String customTime(String expr, Date date) {
    // 后 N 年：$[add_months(yyyyMMdd,12*N)], 前 N 月：$[add_months(yyyyMMdd,-N)]等
    String value;

    try {
      // timestamp 比较特别, 格式化不一定好找
      if (expr.startsWith(TIMESTAMP)) {
        String timeExpr = expr.substring(TIMESTAMP.length() + 1, expr.length() - 1);

        // 日期, 表达式对应出来
        Map.Entry<Date, String> entry = calcTimeExpr(timeExpr, date);

        // 采用日期对表达式进行 format
        String dateStr = DateUtils.format(entry.getKey(), entry.getValue());

        // 得到 timestamp, 这里采用固定的格式来进行解析
        Date timestamp = DateUtils.parse(dateStr, SystemParamManager.TIME_FORMAT);

        value = String.valueOf(timestamp.getTime() / 1000); // 获取时间戳（精确到s）
      } else {
        // 找出里面的日期, 以及相关的格式化
        Map.Entry<Date, String> entry = calcTimeExpr(expr, date);
        value = DateUtils.format(entry.getKey(), entry.getValue());
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw e;
    }

    return value;
  }

  /**
   * 计算时间表达式 <p>
   *
   * @return <日期, 时间格式>
   */
  private static Map.Entry<Date, String> calcTimeExpr(String expr, Date date) {
    Map.Entry<Date, String> resultEntry;

    if (expr.startsWith(ADD_MONTHS)) {
      resultEntry = addMonthCalc(expr, date);
    } else if (expr.startsWith(MONTH_BEGIN)) {
      resultEntry = monthBeginCalc(expr, date);
    } else if (expr.startsWith(MONTH_END)) {
      resultEntry = monthEndCalc(expr, date);
    } else if (expr.startsWith(WEEK_BEGIN)) {
      resultEntry = weekStartCalc(expr, date);
    } else if (expr.startsWith(WEEK_END)) {
      resultEntry = weekEndCalc(expr, date);
    } else { // 周、天、时、分的表达式，如：$[yyyyMMdd+7*N]
      resultEntry = addMinuteCalc(expr, date);
    }

    return resultEntry;
  }

  /**
   * 获取月初 <p>
   *
   * @return 月初
   */
  private static Map.Entry<Date, String> monthBeginCalc(String expr, Date cycTime) {
    String addMonthExpr = expr.substring(MONTH_BEGIN.length() + 1, expr.length() - 1);
    String[] params = addMonthExpr.split(",");

    if (params.length == 2) {
      String dateFormat = params[0];
      String dayExpr = params[1];
      Integer day = CalculateUtil.calc(dayExpr);
      Date targetDate = getFirstDayOfMonth(cycTime);
      targetDate = org.apache.commons.lang.time.DateUtils.addDays(targetDate, day);

      return new AbstractMap.SimpleImmutableEntry<>(targetDate, dateFormat);
    }

    throw new RuntimeException("Expression not valid");
  }

  /**
   * 获取月末 <p>
   *
   * @return 月末
   */
  private static Map.Entry<Date, String> monthEndCalc(String expr, Date cycTime) {
    String addMonthExpr = expr.substring(MONTH_END.length() + 1, expr.length() - 1);
    String[] params = addMonthExpr.split(",");

    if (params.length == 2) {
      String dateFormat = params[0];
      String dayExpr = params[1];
      Integer day = CalculateUtil.calc(dayExpr);
      Date targetDate = getLastDayOfMonth(cycTime);
      targetDate = org.apache.commons.lang.time.DateUtils.addDays(targetDate, day);

      return new AbstractMap.SimpleImmutableEntry<>(targetDate, dateFormat);
    }

    throw new RuntimeException("Expression not valid");
  }

  /**
   * 获取周一 <p>
   *
   * @return 周一
   */
  private static Map.Entry<Date, String> weekStartCalc(String expr, Date cycTime) {
    String addMonthExpr = expr.substring(WEEK_BEGIN.length() + 1, expr.length() - 1);
    String[] params = addMonthExpr.split(",");

    if (params.length == 2) {
      String dateFormat = params[0];
      String dayExpr = params[1];
      Integer day = CalculateUtil.calc(dayExpr);
      Date targetDate = getMonday(cycTime);
      targetDate = org.apache.commons.lang.time.DateUtils.addDays(targetDate, day);
      return new AbstractMap.SimpleImmutableEntry<>(targetDate, dateFormat);
    }

    throw new RuntimeException("Expression not valid");
  }

  /**
   * 获取月末 <p>
   *
   * @return 周末
   */
  private static Map.Entry<Date, String> weekEndCalc(String expr, Date cycTime) {
    String addMonthExpr = expr.substring(WEEK_END.length() + 1, expr.length() - 1);
    String[] params = addMonthExpr.split(",");

    if (params.length == 2) {
      String dateFormat = params[0];
      String dayExpr = params[1];
      Integer day = CalculateUtil.calc(dayExpr);
      Date targetDate = getSunday(cycTime);
      targetDate = org.apache.commons.lang.time.DateUtils.addDays(targetDate, day);

      return new AbstractMap.SimpleImmutableEntry<>(targetDate, dateFormat);
    }

    throw new RuntimeException("Expression not valid");
  }

  /**
   * 计算时间表达式（增加月） <p>
   *
   * @return <日期, 时间格式>
   */
  private static Map.Entry<Date, String> addMonthCalc(String expr, Date cycTime) {
    String addMonthExpr = expr.substring(ADD_MONTHS.length() + 1, expr.length() - 1);
    String[] params = addMonthExpr.split(",");

    if (params.length == 2) {
      String dateFormat = params[0];
      String monthExpr = params[1];
      Integer addMonth = CalculateUtil.calc(monthExpr);
      Date targetDate = org.apache.commons.lang.time.DateUtils.addMonths(cycTime, addMonth);

      return new AbstractMap.SimpleImmutableEntry<>(targetDate, dateFormat);
    }

    throw new RuntimeException("Expression not valid");
  }

  /**
   * 计算时间表达式（增加分钟） <p>
   *
   * @return <日期, 时间格式>
   */
  private static Map.Entry<Date, String> addMinuteCalc(String expr, Date cycTime) {
    if (expr.contains("+")) {
      int index = expr.lastIndexOf('+');

      // HHmmss+N/24/60
      if (Character.isDigit(expr.charAt(index + 1))) {
        String addMinuteExpr = expr.substring(index + 1);
        Date targetDate = org.apache.commons.lang.time.DateUtils
            .addMinutes(cycTime, calcAddMinute(addMinuteExpr));
        String dateFormat = expr.substring(0, index);

        return new AbstractMap.SimpleImmutableEntry<>(targetDate, dateFormat);
      }
    } else if (expr.contains("-")) {
      int index = expr.lastIndexOf('-');

      // HHmmss-N/24/60
      if (Character.isDigit(expr.charAt(index + 1))) {
        String addMinuteExpr = expr.substring(index + 1);
        Date targetDate = org.apache.commons.lang.time.DateUtils
            .addMinutes(cycTime, 0 - calcAddMinute(addMinuteExpr));
        String dateFormat = expr.substring(0, index);

        return new AbstractMap.SimpleImmutableEntry<>(targetDate, dateFormat);
      }

      // yyyy-MM-dd/HH:mm:ss
      return new AbstractMap.SimpleImmutableEntry<>(cycTime, expr);
    }

    // 类似 $[HHmmss] 的情况
    return new AbstractMap.SimpleImmutableEntry<>(cycTime, expr);
  }

  /**
   * 计算需要 add 的分钟数 <p>
   */
  private static Integer calcAddMinute(String addMinuteExpr) {
    int index = addMinuteExpr.indexOf("/");

    String calcExpr;

    if (index == -1) {
      calcExpr = MessageFormat.format("60*24*({0})", addMinuteExpr);
    } else {
      calcExpr = MessageFormat.format("60*24*({0}){1}", addMinuteExpr.substring(0, index),
          addMinuteExpr.substring(index));
    }

    return CalculateUtil.calc(calcExpr);
  }

  /**
   * 获取周一 <p>
   */
  public static Date getMonday(Date date) {
    Calendar cal = Calendar.getInstance();

    cal.setTime(date);

    // 将每周第一天设为星期一，默认是星期天
    cal.setFirstDayOfWeek(Calendar.MONDAY);
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

    return cal.getTime();
  }

  /**
   * 获取周日 <p>
   */
  public static Date getSunday(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);

    // 将每周第一天设为星期一，默认是星期天
    cal.setFirstDayOfWeek(Calendar.MONDAY);
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

    return cal.getTime();
  }

  /**
   * 获取每月的第一天 <p>
   */
  public static Date getFirstDayOfMonth(Date date) {
    Calendar cal = Calendar.getInstance();

    cal.setTime(date);
    cal.set(Calendar.DAY_OF_MONTH, 1);

    return cal.getTime();
  }

  /**
   * 获取每月的最后一天 <p>
   */
  public static Date getLastDayOfMonth(Date date) {
    Calendar cal = Calendar.getInstance();

    cal.setTime(date);

    // 取下个月 1 号的前一天
    cal.add(Calendar.MONTH, 1);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.add(Calendar.DAY_OF_MONTH, -1);

    return cal.getTime();
  }

  public static void main(String[] args) {
    System.out.println(addMonthCalc("add_months(yyyyMMdd,12*1)", new Date()));
    System.out.println(addMonthCalc("add_months(yyyyMMdd,-12*1)", new Date()));

    System.out.println(addMinuteCalc("yyyy", new Date()));
    System.out.println(addMinuteCalc("yyyyMMdd+7*1", new Date()));
    System.out.println(addMinuteCalc("yyyyMMdd-7*1", new Date()));
    System.out.println(addMinuteCalc("yyyyMMdd+1", new Date()));
    System.out.println(addMinuteCalc("yyyyMMdd-1", new Date()));
    System.out.println(addMinuteCalc("yyyyMMddHH+1/24", new Date()));
    System.out.println(addMinuteCalc("yyyyMMddHH-1/24", new Date()));
    System.out.println(addMinuteCalc("yyyyMMddHHmm+1/24/60", new Date()));
    System.out.println(addMinuteCalc("yyyyMMddHHmm-1/24/60", new Date()));
    System.out.println(addMinuteCalc("yyyy-MM-dd/HH:mm:ss", new Date()));
    System.out.println(addMinuteCalc("yyyy-MM-dd/HH:mm:ss-1/24", new Date()));

    String test1 = TimePlaceholderUtil.resolvePlaceholders("$[yyyy]test1$[yyyy:***]$[yyyy-MM-dd-1],$[month_begin(yyyyMMdd, 1)],$[month_end(yyyyMMdd, -1)],$[week_begin(yyyyMMdd, 1)],$[week_end(yyyyMMdd, -1)]",
        new Date(), true);
    System.out.println(test1);

    String test2 = TimePlaceholderUtil.resolvePlaceholdersConst("$[test1]test1$[parm1:***]$[test1]", "NULL");
    System.out.println(test2);

    String test3 = TimePlaceholderUtil.resolvePlaceholders("$[timestamp(yyyyMMdd00mmss)],"
            + "$[timestamp(month_begin(yyyyMMddHHmmss, 1))],"
            + "$[timestamp(month_end(yyyyMMddHHmmss, -1))],"
            + "$[timestamp(week_begin(yyyyMMddHHmmss, 1))],"
            + "$[timestamp(week_end(yyyyMMdd000000, -1))],"
            + "$[timestamp(yyyyMMddHHmmss)]",
        new Date(), true);

    System.out.println(test3);
  }
}
