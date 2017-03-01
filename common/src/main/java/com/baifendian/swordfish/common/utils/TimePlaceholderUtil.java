/*
 * Create Author  : dsfan
 * Create Date    : 2016年12月9日
 * File Name      : TimePlaceholderUtil.java
 */

package com.baifendian.swordfish.common.utils;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PropertyPlaceholderHelper;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;



/**
 * 时间占位符工具类
 * <p>
 * @see <a href="http://wiki.baifendian.com/pages/viewpage.action?pageId=13503112#id-14.参数设计说明-1.1时间相关的系统参数">系统参数定义</a>
 *
 * @author : dsfan
 * @date : 2016年12月9日
 */
public class TimePlaceholderUtil {
    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(TimePlaceholderUtil.class);

    /** 待替换位置的前缀 : "$[" */
    public static final String PLACEHOLDER_PREFIX = "$[";

    /** 待替换位置的后缀 :"]" */
    public static final String PLACEHOLDER_SUFFIX = "]";

    /** 键与默认值的分割符（null表示不支持） */
    public static final String VALUE_SEPARATOR = null;

    /** 严格的替换工具实现，待替换的位置没有获取到对应值时，则抛出异常 */
    private static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, false);

    /** 非严格的替换工具实现，待替换的位置没有获取到对应值时，则忽略当前位置，继续替换下一个位置 */
    private static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, true);

    /** add_months */
    private static final String ADD_MONTHS = "add_months";

    /** month_begin */
    private static final String MONTH_BEGIN = "month_begin";

    /** month_end */
    private static final String MONTH_END = "month_end";

    /** week_begin */
    private static final String WEEK_BEGIN = "week_begin";

    /** week_end */
    private static final String WEEK_END = "week_end";

    /** timestamp */
    private static final String TIMESTAMP = "timestamp";

    /**
     * 替换文本的占位符
     * <p>
     *
     * @param text
     *            待替换文本
     * @param date
     *            占位符的数据字典
     * @return 替换后的字符串
     */
    public static String resolvePlaceholders(String text, Date date) {
        return resolvePlaceholders(text, date, false);
    }

    /**
     * 替换文本的占位符
     * <p>
     *
     * @param text
     *            待替换文本
     * @param date
     *            需要自定义的日期
     * @param ignoreUnresolvablePlaceholders
     *            是否忽略没有匹配到值的占位符
     * @return 替换后的字符串
     */
    public static String resolvePlaceholders(String text, Date date, boolean ignoreUnresolvablePlaceholders) {
        PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper);
        return helper.replacePlaceholders(text, new TimePlaceholderResolver(text, date));
    }

    /**
     * 替换文本的占位符（空替换）
     * <p>
     *
     * @param text
     * @param constValue
     * @return 替换后的字符串
     */
    public static String resolvePlaceholdersConst(String text, String constValue) {
        return nonStrictHelper.replacePlaceholders(text, new ConstPlaceholderResolver(constValue));
    }

    /**
     * 占位符替换的处理
     * <p>
     * 
     * @author : dsfan
     * @date : 2016年10月11日
     */
    private static class TimePlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

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
                LOGGER.error("Could not resolve placeholder '" + placeholderName + "' in [" + this.text + "]", ex);
                return null;
            }
        }

    }

    /**
     * 占位符替换的处理（空字符串替换占位符）
     * <p>
     * 
     * @author : dsfan
     * @date : 2016年11月29日
     */
    private static class ConstPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

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
     * 计算自定义时间
     * <p>
     *
     * @param expr
     * @param date
     * @return 自定义的时间
     */
    private static String customTime(String expr, Date date) {
        // 后N年：$[add_months(yyyyMMdd,12*N)],前N月：$[add_months(yyyyMMdd,-N)]
        // 等
        String value;
        try {
            if (expr.startsWith(TIMESTAMP)) {
                String timeExpr = expr.substring(TIMESTAMP.length() + 1, expr.length() - 1);
                Map.Entry<Date, String> entry = calcTimeExpr(timeExpr, date);
                String dateStr = BFDDateUtils.format(entry.getKey(), entry.getValue());
                Date timestamp = BFDDateUtils.parse(dateStr, entry.getValue());
                value = String.valueOf(timestamp.getTime() / 1000); // 获取时间戳（精确到s）
            } else {
                Map.Entry<Date, String> entry = calcTimeExpr(expr, date);
                value = BFDDateUtils.format(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        return value;
    }

    /**
     * 计算时间表达式
     * <p>
     *
     * @param expr
     * @param date
     * @return <日期,时间格式>
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
     * 获取月初
     * <p>
     *
     * @param expr
     * @param cycTime
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
            targetDate = DateUtils.addDays(targetDate, day);
            return new AbstractMap.SimpleImmutableEntry<Date, String>(targetDate, dateFormat);
        }
        throw new RuntimeException("表达式不正确");
    }

    /**
     * 获取月末
     * <p>
     *
     * @param expr
     * @param cycTime
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
            targetDate = DateUtils.addDays(targetDate, day);
            return new AbstractMap.SimpleImmutableEntry<Date, String>(targetDate, dateFormat);
        }
        throw new RuntimeException("表达式不正确");
    }

    /**
     * 获取周一
     * <p>
     *
     * @param expr
     * @param cycTime
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
            targetDate = DateUtils.addDays(targetDate, day);
            return new AbstractMap.SimpleImmutableEntry<Date, String>(targetDate, dateFormat);
        }
        throw new RuntimeException("表达式不正确");
    }

    /**
     * 获取月末
     * <p>
     *
     * @param expr
     * @param cycTime
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
            targetDate = DateUtils.addDays(targetDate, day);
            return new AbstractMap.SimpleImmutableEntry<Date, String>(targetDate, dateFormat);
        }
        throw new RuntimeException("表达式不正确");
    }

    /**
     * 计算时间表达式（增加月）
     * <p>
     *
     * @param expr
     * @param cycTime
     * @return <日期,时间格式>
     */
    private static Map.Entry<Date, String> addMonthCalc(String expr, Date cycTime) {
        String addMonthExpr = expr.substring(ADD_MONTHS.length() + 1, expr.length() - 1);
        String[] params = addMonthExpr.split(",");
        if (params.length == 2) {
            String dateFormat = params[0];
            String monthExpr = params[1];
            Integer addMonth = CalculateUtil.calc(monthExpr);
            Date targetDate = DateUtils.addMonths(cycTime, addMonth);
            return new AbstractMap.SimpleImmutableEntry<Date, String>(targetDate, dateFormat);
        }
        throw new RuntimeException("表达式不正确");
    }

    /**
     * 计算时间表达式（增加分钟）
     * <p>
     *
     * @param expr
     * @param cycTime
     * @return <日期,时间格式>
     */
    private static Map.Entry<Date, String> addMinuteCalc(String expr, Date cycTime) {
        if (expr.contains("+")) {
            int index = expr.lastIndexOf('+');
            // $[HHmmss+N/24/60]
            if (Character.isDigit(expr.charAt(index + 1))) {
                String addMinuteExpr = expr.substring(index + 1);
                Date targetDate = DateUtils.addMinutes(cycTime, calcAddMinute(addMinuteExpr));
                String dateFormat = expr.substring(0, index);
                return new AbstractMap.SimpleImmutableEntry<Date, String>(targetDate, dateFormat);
            }
        } else if (expr.contains("-")) {
            int index = expr.lastIndexOf('-');
            // $[HHmmss-N/24/60]
            if (Character.isDigit(expr.charAt(index + 1))) {
                String addMinuteExpr = expr.substring(index + 1);
                Date targetDate = DateUtils.addMinutes(cycTime, 0 - calcAddMinute(addMinuteExpr));
                String dateFormat = expr.substring(0, index);
                return new AbstractMap.SimpleImmutableEntry<Date, String>(targetDate, dateFormat);
            } else { // $[yyyy-MM-dd/HH:mm:ss]
                return new AbstractMap.SimpleImmutableEntry<Date, String>(cycTime, expr);
            }
        }
        // 类似 $[HHmmss] 的情况
        return new AbstractMap.SimpleImmutableEntry<Date, String>(cycTime, expr);
    }

    /**
     * 计算需要 add 的分钟数
     * <p>
     *
     * @param addMinuteExpr
     * @return 增加的分钟数
     */
    private static Integer calcAddMinute(String addMinuteExpr) {
        int index = addMinuteExpr.indexOf("/");
        String calcExpr;
        if (index == -1) {
            calcExpr = MessageFormat.format("60*24*({0})", addMinuteExpr);
        } else {
            calcExpr = MessageFormat.format("60*24*({0}){1}", addMinuteExpr.substring(0, index), addMinuteExpr.substring(index));
        }

        return CalculateUtil.calc(calcExpr);
    }

    /**
     * 获取周一
     * <p>
     *
     * @param date
     * @return 周一
     */
    public static Date getMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);// 将每周第一天设为星期一，默认是星期天
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获取周日
     * <p>
     *
     * @param date
     * @return 周日
     */
    public static Date getSunday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);// 将每周第一天设为星期一，默认是星期天
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal.getTime();
    }

    /**
     * 获取每月的第一天
     * <p>
     *
     * @param date
     * @return 每月1号
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 获取每月的最后一天
     * <p>
     *
     * @param date
     * @return 每月最后一天
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 取下个月1号的前一天
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

        String test3 = TimePlaceholderUtil.resolvePlaceholders("$[timestamp(month_begin(yyyyMMdd, 1))],$[timestamp(month_end(yyyyMMdd, -1))],$[timestamp(week_begin(yyyyMMdd, 1))],$[timestamp(week_end(yyyyMMdd, -1))]",
                                                               new Date(), true);
        System.out.println(test3);

    }
}
