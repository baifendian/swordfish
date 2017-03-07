/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月30日
 * File Name      : MrParam.java
 */

package com.baifendian.swordfish.execserver.job.spark;

import com.baifendian.swordfish.common.job.BaseParam;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * MR 节点的参数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月30日
 */
public class SparkParam extends BaseParam {

    private String mainJar;

    private String mainClass;

    private List<String> appArgs = new ArrayList<>();

    private String driverCores;

    private String driverMemory;

    private String numExecutors;

    private String executorCores;

    private String executorMemory;

    private List<String> jars = new ArrayList<>();

    private List<String> files = new ArrayList<>();

    private List<String> archives = new ArrayList<>();

    private String queue;

    @Override
    public boolean checkValid() {
        return mainJar != null && StringUtils.isNotEmpty(mainClass);
    }

    public String getMainJar() {
        return mainJar;
    }

    public void setMainJar(String mainJar) {
        this.mainJar = mainJar;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public List<String> getAppArgs() {
        return appArgs;
    }

    public void setAppArgs(List<String> appArgs) {
        this.appArgs = appArgs;
    }

    public String getDriverCores() {
        return driverCores;
    }

    public void setDriverCores(String driverCores) {
        this.driverCores = driverCores;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public void setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
    }

    public String getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(String numExecutors) {
        this.numExecutors = numExecutors;
    }

    public String getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(String executorCores) {
        this.executorCores = executorCores;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    public List<String> getJars() {
        return jars;
    }

    public void setJars(List<String> jars) {
        this.jars = jars;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<String> getArchives() {
        return archives;
    }

    public void setArchives(List<String> archives) {
        this.archives = archives;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
