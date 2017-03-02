/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月15日
 * File Name      : ExecTimeoutException.java
 */

package com.baifendian.swordfish.execserver.exception;

/**
 * 配置信息错误
 * <p>
 * 
 * @author : liujin
 * @date : 2017年3月2日
 */
public class ConfigException extends RuntimeException {
    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     */
    public ConfigException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param cause
     */
    public ConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
