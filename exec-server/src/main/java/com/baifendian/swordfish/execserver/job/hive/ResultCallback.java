/*
 * Create Author  : dsfan
 * Create Date    : 2016年12月21日
 * File Name      : ResultCallback.java
 */

package com.baifendian.swordfish.execserver.job.hive;

import com.baifendian.swordfish.common.job.ExecResult;

/**
 * 执行结果回调处理
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年12月21日
 */
public interface ResultCallback {

    /**
     * 处理执行结果
     * <p>
     *
     * @param execResult
     */
    void handleResult(ExecResult execResult);
}
