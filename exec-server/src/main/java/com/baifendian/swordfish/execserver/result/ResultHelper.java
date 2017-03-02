/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月26日
 * File Name      : ResultHelper.java
 */

package com.baifendian.swordfish.execserver.result;

//import com.bfd.dw.rpc.scheduler.RetInfo;

import com.baifendian.swordfish.execserver.rpc.RetInfo;

/**
 * 返回结果帮助类
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月26日
 */
public class ResultHelper {
    /** SUCCESS */
    public static final RetInfo SUCCESS = new RetInfo(0, "success");

    /**
     * 创建一个错误异常的返回包
     * <p>
     *
     * @param msg
     * @return {@link RetInfo}
     */
    public static RetInfo createErrorResult(String msg) {
        return new RetInfo(1, msg);
    }
}
