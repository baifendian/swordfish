/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月24日
 * File Name      : QuartzException.java
 */

package com.baifendian.swordfish.execserver.exception;

/**
 * Quartz 调度的异常
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月24日
 */
public class QuartzException extends RuntimeException {
    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     */
    public QuartzException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param cause
     */
    public QuartzException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
