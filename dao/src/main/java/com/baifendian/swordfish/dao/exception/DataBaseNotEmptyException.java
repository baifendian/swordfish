package com.baifendian.swordfish.dao.exception;

/**
 * Created by caojingwei on 2016/11/19.
 */
public class DataBaseNotEmptyException extends SqlException {
    /**
     * @param msg
     */
    public DataBaseNotEmptyException(String msg) {
        super(msg);
    }
}
