/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月15日
 * File Name      : ExecTimeoutException.java
 */

package com.baifendian.swordfish.execserver.exception;

/**
 * 执行超时异常
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月15日
 */
public class ExecTimeoutException extends RuntimeException {
    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     */
    public ExecTimeoutException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param cause
     */
    public ExecTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
