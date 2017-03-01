/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月24日
 * File Name      : HdfsPathManager.java
 */

package com.baifendian.swordfish.dao.hadoop.hdfs;

import java.text.MessageFormat;

/**
 * hdfs 路径管理
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月24日
 */
public class HdfsPathManager {

    /** 路径分割符 "/" (Linux下) */
    public static final String PATH_SEPARATOR = "/";

    /** 文件基础目录, 这个可能是要修改的(comment by qifeng.dai) */
    private static String BASE_PATH = "/tmp/test/";

    /** 未发布目录 */
    private static final String UNPUBLISHED_PATH = "unpub/";

    /** 已发布目录 */
    private static final String PUBLISHED_PATH = "pub/";

    /** 资源未发布目录 */
    private static final String RESOURCE_UNPUBLISHED_PATH_FORMAT = "{0}/{1}/resource/" + UNPUBLISHED_PATH + "{2}/";

    /** 资源已发布目录 */
    private static final String RESOURCE_PUBLISHED_PATH_FORMAT = "{0}/{1}/resource/" + PUBLISHED_PATH + "{2}/";

    /** 任务的目录 */
    private static final String TASK_PATH_FORMAT = "{0}/{1}/task/";

    /** 工作流的目录 */
    private static final String FLOW_PATH_FORMAT = "{0}/{1}/flow/";

    /**
     * 初始化
     * <p>
     *
     * @param basePath
     */
    public static void init(String basePath) {
        BASE_PATH = basePath;
    }

    /**
     * 生成资源的hdfs路径
     * <p>
     *
     * @param orgName
     * @param projectName
     * @param resourceId
     * @param isPub
     * @return hdfs路径
     */
    public static String genResourceHdfsPath(String orgName, String projectName, int resourceId, boolean isPub) {
        if (isPub) {
            return MessageFormat.format(BASE_PATH + RESOURCE_PUBLISHED_PATH_FORMAT, orgName, projectName, resourceId);
        }
        return MessageFormat.format(BASE_PATH + RESOURCE_UNPUBLISHED_PATH_FORMAT, orgName, projectName, resourceId);
    }

    /**
     * 生成任务的目录
     * <p>
     *
     * @param orgName
     * @param projectName
     * @return hdfs路径
     */
    public static String genTaskHdfsPath(String orgName, String projectName) {
        return MessageFormat.format(BASE_PATH + TASK_PATH_FORMAT, orgName, projectName);
    }

    /**
     * 生成工作流的 archive 目录
     * <p>
     *
     * @param orgName
     * @param projectName
     * @return hdfs路径
     */
    public static String genFlowHdfsPath(String orgName, String projectName) {
        return MessageFormat.format(BASE_PATH + FLOW_PATH_FORMAT, orgName, projectName);
    }
}
