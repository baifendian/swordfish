/*
 * Create Author  : dsfan
 * Create Date    : 2016年11月16日
 * File Name      : ExecException.java
 */

package com.baifendian.swordfish.common.job.exception;

/**
 * 执行异常
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年11月16日
 */
public class ExecException extends RuntimeException {
    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     */
    public ExecException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param cause
     */
    public ExecException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
