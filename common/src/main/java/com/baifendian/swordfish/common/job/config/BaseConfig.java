/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.common.job.config;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * @author : liujin
 * @date : 2017-03-07 12:55
 */
public class BaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseConfig.class);

    private static String hdfsBasePath;

    private static String localBasePath;

    /** 环境变量信息 */
    private static String systemEnvPath;

    private static Properties properties = new Properties();

    static {
        InputStream is = null;
        try {
            File dataSourceFile = ResourceUtils.getFile("classpath:base_config.properties");
            is = new FileInputStream(dataSourceFile);
            properties.load(is);
            hdfsBasePath = properties.getProperty("hdfs.base.path");
            localBasePath = properties.getProperty("local.base.path");
            systemEnvPath = properties.getProperty("sf.env.file");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /** workflow 执行本地目录 projectId/workflowId/execId*/
    private static final String FLOW_EXEC_PATH_FORMAT="{0}/{1}/{2}";

    public static String getFlowExecPath(int projectId, int workflowId, long execId){
        return String.format("%s/%d/%d/%d", localBasePath, projectId, workflowId, execId);
    }

    public static String getHdfsProjectResourcesPath(int projectId){
        return MessageFormat.format("{0}/{1}/resources", hdfsBasePath, projectId);
    }

    public static String getHdfsFlowResourcesPath(int projectId, int flowId){
        return MessageFormat.format("{0}/{1}/workflows/{2}", hdfsBasePath, projectId, flowId);
    }

    public static String getSystemEnvPath(){
        return systemEnvPath;
    }

    public static void main(String[] args) {
        System.out.println(BaseConfig.localBasePath);
    }
}
