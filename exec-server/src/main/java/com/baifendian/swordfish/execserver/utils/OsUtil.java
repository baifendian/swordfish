/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月2日
 * File Name      : OsUtil.java
 */

package com.baifendian.swordfish.execserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 操作系统工具类
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月2日
 */
public class OsUtil {

    private static final Logger logger = LoggerFactory.getLogger(OsUtil.class);
    /**
     * private constructor
     */
    private OsUtil() {
    }

    /**
     * 是否 windows
     * <p>
     *
     * @return
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.startsWith("Windows");
    }

}
