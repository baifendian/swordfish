package com.baifendian.swordfish.dao.exception;

/**
 * Created by wenting on 11/22/16.
 */
public class ScheduleNotValidException extends RuntimeException{

    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /**
     * @param msg
     */
    public ScheduleNotValidException(String msg) {
        super(msg);
    }
}
