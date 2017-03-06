/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月28日
 * File Name      : MasterConfig.java
 */

package com.baifendian.swordfish.webserver.config;

import com.bfd.harpc.common.configure.PropertiesConfiguration;

/**
 * Master 配置信息
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月28日
 */
public class MasterConfig {
    /** 失败重试次数,默认为 2 次 */
    public static int failRetryCount = PropertiesConfiguration.getValue("masterToWorker.failRetry.count", 2);

    /** 失败重试的队列大小,默认为 10000 */
    public static int failRetryQueueSize = PropertiesConfiguration.getValue("masterToWorker.failRetry.queueSize", 10000);
}
