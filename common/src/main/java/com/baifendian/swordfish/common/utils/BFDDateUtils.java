/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月23日
 * File Name      : DateUtils.java
 */

package com.baifendian.swordfish.common.utils;

import com.baifendian.swordfish.common.consts.Constants;
import org.apache.commons.lang.time.FastDateFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间操作工具类
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月23日
 */
public class BFDDateUtils {
    /** 默认时区 */
    // private static final TimeZone TIME_ZONE =
    // TimeZone.getTimeZone("Etc/GMT-8");

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(Constants.BASE_DATETIME_FORMAT);

    public static void main(String[] args) {
        System.out.println(TimeZone.getDefault().getID());
    }

    /**
     * 获取系统默认时区
     * <p>
     *
     * @return
     */
    public static String getDefaultTimeZome() {
        return TimeZone.getDefault().getID();
    }

    /**
     * 获取当前时刻的格式化的日期字符串(格式为：yyyy-MM-dd hh:mm:ss)
     * <p>
     *
     * @return 日期字符串
     */
    public static String now() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * 获取当前时间指定格式的日期字符串
     * <p>
     *
     * @param format
     * @return 日期字符串
     */
    public static String now(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date());
    }

    /**
     * 获取默认的格式化的日期字符串(格式为：yyyy-MM-dd hh:mm:ss)
     * <p>
     *
     * @param date
     * @return 日期字符串
     */
    public static String defaultFormat(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String defaultFormat(int unixTimestamp) {
        return DATE_FORMAT.format(new Date(unixTimestamp*1000));
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
            throw new RuntimeException("时间转换失败异常", e);
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
            throw new RuntimeException("时间转换失败异常", e);
        }
    }

    public static int getSecs(){
        return (int)(System.currentTimeMillis()/1000);
    }

    public static int getSecs(Date date){
        return (int)(date.getTime()/1000);
    }

}
