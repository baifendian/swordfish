/*
 * Create Author  : dsfan
 * Create Date    : 2016-7-21
 * File Name      : SqlException.java
 */

package com.baifendian.swordfish.common.hive.exception;

/**sql语句的语法错误等
 * <p>
 * 
 * @author : dsfan
 *
 * @date : 2016-7-21
 */
public class SqlException extends RuntimeException {

    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     */
    public SqlException(String msg) {
        super(msg);
    }

}
