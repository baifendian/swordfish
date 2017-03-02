/*
 * Create Author  : dsfan
 * Create Date    : 2016年12月7日
 * File Name      : FlowRunnerContext.java
 */

package com.baifendian.swordfish.execserver;

import com.baifendian.swordfish.dao.mysql.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.mysql.model.ExecutionFlow;
import com.baifendian.swordfish.dao.mysql.model.Schedule;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * workflow 执行的上下文
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年12月7日
 */
public class FlowRunnerContext {
    /** {@link ExecutionFlow} */
    private ExecutionFlow executionFlow;

    /** {@link ExecutorService} */
    private ExecutorService executorService;

    /** {@link ExecutorService} */
    private ExecutorService jobExecutorService;

    /** 调度信息 */
    private Schedule schedule;

    /** 一个节点失败后的策略类型 */
    private FailurePolicyType failurePolicyType;

    /** 最大重试次数 */
    private int maxTryTimes;

    /** 节点最大的超时时间 (2) */
    private int timeout;

    /** 系统参数 */
    private Map<String, String> systemParamMap;

    /** 自定义参数 */
    private Map<String, String> customParamMap;

    /**
     * getter method
     * 
     * @see FlowRunnerContext#executionFlow
     * @return the executionFlow
     */
    public ExecutionFlow getExecutionFlow() {
        return executionFlow;
    }

    /**
     * setter method
     * 
     * @see FlowRunnerContext#executionFlow
     * @param executionFlow
     *            the executionFlow to set
     */
    public void setExecutionFlow(ExecutionFlow executionFlow) {
        this.executionFlow = executionFlow;
    }

    /**
     * getter method
     * 
     * @see FlowRunnerContext#executorService
     * @return the executorService
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * setter method
     * 
     * @see FlowRunnerContext#executorService
     * @param executorService
     *            the executorService to set
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * getter method
     * 
     * @see FlowRunnerContext#jobExecutorService
     * @return the jobExecutorService
     */
    public ExecutorService getJobExecutorService() {
        return jobExecutorService;
    }

    /**
     * setter method
     * 
     * @see FlowRunnerContext#jobExecutorService
     * @param jobExecutorService
     *            the jobExecutorService to set
     */
    public void setJobExecutorService(ExecutorService jobExecutorService) {
        this.jobExecutorService = jobExecutorService;
    }

    /**
     * getter method
     * 
     * @see FlowRunnerContext#schedule
     * @return the schedule
     */
    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * setter method
     * 
     * @see FlowRunnerContext#schedule
     * @param schedule
     *            the schedule to set
     */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * getter method
     * 
     * @see FlowRunnerContext#failurePolicyType
     * @return the failurePolicyType
     */
    public FailurePolicyType getFailurePolicyType() {
        return failurePolicyType;
    }

    /**
     * setter method
     * 
     * @see FlowRunnerContext#failurePolicyType
     * @param failurePolicyType
     *            the failurePolicyType to set
     */
    public void setFailurePolicyType(FailurePolicyType failurePolicyType) {
        this.failurePolicyType = failurePolicyType;
    }

    /**
     * getter method
     * 
     * @see FlowRunnerContext#maxTryTimes
     * @return the maxTryTimes
     */
    public int getMaxTryTimes() {
        return maxTryTimes;
    }

    /**
     * setter method
     * 
     * @see FlowRunnerContext#maxTryTimes
     * @param maxTryTimes
     *            the maxTryTimes to set
     */
    public void setMaxTryTimes(int maxTryTimes) {
        this.maxTryTimes = maxTryTimes;
    }

    /**
     * getter method
     * 
     * @see FlowRunnerContext#timeout
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * setter method
     * 
     * @see FlowRunnerContext#timeout
     * @param timeout
     *            the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * getter method
     * 
     * @see FlowRunnerContext#systemParamMap
     * @return the systemParamMap
     */
    public Map<String, String> getSystemParamMap() {
        return systemParamMap;
    }

    /**
     * setter method
     * 
     * @see FlowRunnerContext#systemParamMap
     * @param systemParamMap
     *            the systemParamMap to set
     */
    public void setSystemParamMap(Map<String, String> systemParamMap) {
        this.systemParamMap = systemParamMap;
    }

    /**
     * getter method
     * 
     * @see FlowRunnerContext#customParamMap
     * @return the customParamMap
     */
    public Map<String, String> getCustomParamMap() {
        return customParamMap;
    }

    /**
     * setter method
     * 
     * @see FlowRunnerContext#customParamMap
     * @param customParamMap
     *            the customParamMap to set
     */
    public void setCustomParamMap(Map<String, String> customParamMap) {
        this.customParamMap = customParamMap;
    }

}
