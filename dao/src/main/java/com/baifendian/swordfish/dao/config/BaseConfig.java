package com.baifendian.swordfish.dao.config;

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

    /** workflow 执行本地目录 projectName/workflowName/execId*/
    private static final String FLOW_EXEC_PATH_FORMAT="{0}/{1}/{2}";

    public static String getFlowExecPath(String projectName, String workflowName, long execId){
        return MessageFormat.format("{0}/{1}/{2}/{3}", localBasePath, projectName, workflowName, execId);
    }

    /**
     * 获取workflow本地资源存放的路径
     * @param projectName
     * @param workflowName
     * @param execId
     * @return
     */
    public static String getFlowExecResPath(String projectName, String workflowName, long execId){
        return MessageFormat.format("{0}/{1}/{2}/{3}/resources", localBasePath, projectName, workflowName, execId);
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
