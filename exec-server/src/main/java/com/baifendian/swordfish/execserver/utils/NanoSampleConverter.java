/*
 * Create Author  : dsfan
 * Create Date    : 2016年12月20日
 * File Name      : MySampleConverter.java
 */

package com.baifendian.swordfish.execserver.utils;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 纳秒转换
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年12月20日
 */
public class NanoSampleConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        long nowInNanos = System.nanoTime();
        return Long.toString(nowInNanos);
    }
}
