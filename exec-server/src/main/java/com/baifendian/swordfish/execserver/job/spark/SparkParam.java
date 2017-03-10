/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月30日
 * File Name      : MrParam.java
 */

package com.baifendian.swordfish.execserver.job.spark;

import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.dao.mysql.model.flow.params.Property;
import com.baifendian.swordfish.execserver.job.ResourceInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MR 节点的参数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月30日
 */
public class SparkParam extends BaseParam {

    private ResourceInfo mainJar;

    private String mainClass;

    private String deployMode;

    private String args;

    private int driverCores;

    private String driverMemory;

    private int numExecutors;

    private int executorCores;

    private String executorMemory;

    private List<Property> properties = new ArrayList<>();

    private List<ResourceInfo> libJars = new ArrayList<>();

    private List<ResourceInfo> files = new ArrayList<>();

    private List<ResourceInfo> archives = new ArrayList<>();

    private String queue;

    @Override
    public boolean checkValid() {
        return mainJar != null && StringUtils.isNotEmpty(mainClass);
    }

    public ResourceInfo getMainJar() {
        return mainJar;
    }

    public void setMainJar(ResourceInfo mainJar) {
        this.mainJar = mainJar;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(String deployMode) {
        this.deployMode = deployMode;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public int getDriverCores() {
        return driverCores;
    }

    public void setDriverCores(int driverCores) {
        this.driverCores = driverCores;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public void setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
    }

    public int getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(int numExecutors) {
        this.numExecutors = numExecutors;
    }

    public int getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(int executorCores) {
        this.executorCores = executorCores;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<ResourceInfo> getLibJars() {
        return libJars;
    }

    public void setJars(List<ResourceInfo> libJjars) {
        this.libJars = libJars;
    }

    public List<ResourceInfo> getFiles() {
        return files;
    }

    public void setFiles(List<ResourceInfo> files) {
        this.files = files;
    }

    public List<ResourceInfo> getArchives() {
        return archives;
    }

    public void setArchives(List<ResourceInfo> archives) {
        this.archives = archives;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    @Override
    public List<String> getResourceFiles(){
        List<String> resFiles = new ArrayList<>();
        if(mainJar.isProjectScope()) {
            resFiles.add(mainJar.getRes());
        }
        if(libJars != null && !libJars.isEmpty())
            resFiles.addAll(libJars.stream().filter(p->p.isProjectScope())
                    .map(p->p.getRes()).collect(Collectors.toList()));
        if(files != null && !files.isEmpty())
            resFiles.addAll(files.stream().filter(p->p.isProjectScope())
                    .map(p->p.getRes()).collect(Collectors.toList()));
        if(archives != null && !archives.isEmpty())
            resFiles.addAll(archives.stream().filter(p->p.isProjectScope())
                    .map(p->p.getRes()).collect(Collectors.toList()));
        return resFiles;
    }
}
