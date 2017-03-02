package com.baifendian.swordfish.execserver.rpc;

/**
 * @author : liujin
 * @date : 2017-03-02 18:03
 */
public class RetInfo {
    private int code;
    private String msg;

    public RetInfo(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
