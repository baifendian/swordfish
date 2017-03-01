/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月12日
 * File Name      : HdfsException.java
 */

package com.baifendian.swordfish.common.hadoop;

/**
 * Hdfs 异常
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月12日
 */
public class HdfsException extends RuntimeException {

    /** Serial version UID */
    private static final long serialVersionUID = -3271763024261592214L;

    /**
     * @param msg
     */
    public HdfsException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param th
     */
    public HdfsException(String msg, Throwable th) {
        super(msg, th);
    }

    /**
     * @param th
     */
    public HdfsException(Throwable th) {
        super(th);
    }
}
