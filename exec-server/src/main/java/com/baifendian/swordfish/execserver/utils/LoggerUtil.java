/*
 * Create Author  : dsfan
 * Create Date    : 2016年12月15日
 * File Name      : LoggerUtil.java
 */

package com.baifendian.swordfish.execserver.utils;

import com.baifendian.swordfish.dao.mysql.enums.FlowType;

/**
 * 日志记录工具类
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年12月15日
 */
public class LoggerUtil {

    /** 分隔符 */
    public static final String SEPARATOR = "_";

    /**
     * 生成 jobId
     * <p>
     *
     * @param flowType
     * @param execId
     * @return jobId
     */
    public static String genJobId(FlowType flowType, long execId) {
        return genJobId(flowType, execId, null);
    }

    /**
     * 生成 jobId
     * <p>
     *
     * @param flowType
     * @param execId
     * @param nodeId
     * @return jobId
     */
    public static String genJobId(FlowType flowType, long execId, Integer nodeId) {
        if (nodeId == null) {
            return flowType + SEPARATOR + execId;
        }
        return flowType + SEPARATOR + execId + SEPARATOR + nodeId;
    }

    public static void main(String[] args) {
        System.out.println(genJobId(FlowType.SHORT, 123));
        System.out.println(genJobId(FlowType.ADHOC, 123, 13));
    }
}
