/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月26日
 * File Name      : Job.java
 */

package com.baifendian.swordfish.common.job;

import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.List;

/**
 * 执行的 Job (用于执行某个具体任务，如 MR/Spark 等)
 * <p>
 * 
 * @author : liujin
 * @date : 2017年3月2日
 */
public interface Job<T> {
    /**
     * 获取 生成的作业ID: NODE_{nodeid}_yyyyMMddHHmmss
     * <p>
     *
     * @return 执行 id
     */
    String getJobId();

    /**
     * 作业前处理
     * @throws Exception
     */
    void before() throws Exception;

    /**
     * 执行(一个job对象仅执行一次)
     * <p>
     *
     * @throws Exception
     */
    void exec() throws Exception;

    /**
     * 作业后处理
     * @throws Exception
     */
    void after() throws Exception;

    /**
     * 取消执行(执行 cancel 之前，必须要保证已经调用 run)
     * <p>
     *
     * @throws Exception
     */
    void cancel() throws Exception;

    /**
     * 作业是否执行完成
     * <p>
     *
     * @return 是否已完成
     */
    boolean isCompleted();

    /**
     * 作业是否被取消了
     * @return
     */
    boolean isCanceled();

    /**
     * 获取作业的配置参数信息
     * @return
     */
    PropertiesConfiguration getProperties();

    /**
     * 获取返回码
     * @return 0-成功，其他值-失败
     */
    int getExitCode();

    /**
     * job执行是否有返回结果
     * @return
     */
    boolean hasResult();

    T getResult();

}
