package com.baifendian.swordfish.dao.exception;

/**
 * Created by wenting on 10/29/16.
 */
public class DataBaseNotExsitException extends SqlException{
    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     */
    public DataBaseNotExsitException(String msg) {
        super(msg);
    }
}
