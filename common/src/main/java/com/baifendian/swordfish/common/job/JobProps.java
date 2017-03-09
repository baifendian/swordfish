package com.baifendian.swordfish.common.job;

import java.util.Map;

/**
 * @author : liujin
 * @date : 2017-03-07 17:33
 */
public class JobProps {

    /** 项目id  **/
    private int projectId;

    /** flow id **/
    private int workflowId;

    /** node id **/
    private int nodeId;

    /** 执行id **/
    private long execId;

    /** 作业执行用户 **/
    private String proxyUser;

    /** 作业配置参数 **/
    private String jobParams;

    /** 作业执行目录 **/
    private String workDir;

    /** 作业执行队列 **/
    private String queue;

    /** 环境变量文件 **/
    private String envFile;

    /** 自定义参数 **/
    private Map<String, String> definedParams;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getJobParams() {
        return jobParams;
    }

    public void setJobParams(String jobParams) {
        this.jobParams = jobParams;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public Map<String, String> getDefinedParams() {
        return definedParams;
    }

    public void setDefinedParams(Map<String, String> definedParams) {
        this.definedParams = definedParams;
    }

    public String getEnvFile() {
        return envFile;
    }

    public void setEnvFile(String envFile) {
        this.envFile = envFile;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public long getExecId() {
        return execId;
    }

    public void setExecId(long execId) {
        this.execId = execId;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
