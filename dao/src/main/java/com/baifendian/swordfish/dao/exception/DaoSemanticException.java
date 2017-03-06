package com.baifendian.swordfish.dao.exception;

/**
 * Created by wenting on 10/22/16.
 */
public class DaoSemanticException extends SqlException{
    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     */
    public DaoSemanticException(String msg) {
        super(msg);
    }
}
