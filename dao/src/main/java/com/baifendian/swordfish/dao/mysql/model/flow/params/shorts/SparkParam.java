/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月10日
 * File Name      : SparkParam.java
 */

package com.baifendian.swordfish.dao.mysql.model.flow.params.shorts;

import com.baifendian.swordfish.dao.mysql.model.flow.params.BaseParam;
import com.baifendian.swordfish.dao.mysql.model.flow.params.Property;
import com.baifendian.swordfish.dao.mysql.model.flow.params.ResInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * spark 参数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月10日
 */
public class SparkParam extends BaseParam {
    /** 主jar包资源信息 */
    private ResInfo mainJarResInfo;

    /** 主程序 */
    private String mainClass;

    /** 参数信息 */
    private List<String> args;

    /** 部署模式 */
    private String deployMode;

    /** 单位 M，driver 的内存大小 */
    private Integer driverMemory;

    /** 默认为 1，driver 的核数 */
    private Integer driverCores;

    /** executor 的个数 */
    private Integer numExecutors;

    /** 单位 M，executor 的内存大小 */
    private Integer executorMemory;

    /** executor 的个数，默认为 2 */
    private Integer executorCores;

    /** 配置信息列表 */
    private List<Property> properties;

    /** 额外的 jar 包，指的是本地的文件，这个可能很长 */
    private List<ResInfo> jarsResInfos;

    /** 额外的文件，指的是本地的文件，可能是多条配置 */
    private List<ResInfo> filesResInfos;

    /** 额外的压缩文件，指的是本地的文件，可能是多条配置 */
    private List<ResInfo> archivesResInfos;

    @Override
    public boolean checkValid() {
        return mainJarResInfo != null && StringUtils.isNotEmpty(mainClass);
    }

    @Override
    public List<Integer> findAllResourceIds() {
        List<Integer> resourceIds = new ArrayList<>();
        if (mainJarResInfo != null) {
            resourceIds.add(mainJarResInfo.getId());
        }
        if (CollectionUtils.isNotEmpty(jarsResInfos)) {
            addToResourceIds(jarsResInfos, resourceIds);
        }
        if (CollectionUtils.isNotEmpty(filesResInfos)) {
            addToResourceIds(filesResInfos, resourceIds);
        }
        if (CollectionUtils.isNotEmpty(archivesResInfos)) {
            addToResourceIds(archivesResInfos, resourceIds);
        }
        return resourceIds;
    }

    /**
     * getter method
     * 
     * @see SparkParam#mainJarResInfo
     * @return the mainJarResInfo
     */
    public ResInfo getMainJarResInfo() {
        return mainJarResInfo;
    }

    /**
     * setter method
     * 
     * @see SparkParam#mainJarResInfo
     * @param mainJarResInfo
     *            the mainJarResInfo to set
     */
    public void setMainJarResInfo(ResInfo mainJarResInfo) {
        this.mainJarResInfo = mainJarResInfo;
    }

    /**
     * getter method
     * 
     * @see SparkParam#mainClass
     * @return the mainClass
     */
    public String getMainClass() {
        return mainClass;
    }

    /**
     * setter method
     * 
     * @see SparkParam#mainClass
     * @param mainClass
     *            the mainClass to set
     */
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * getter method
     * 
     * @see SparkParam#args
     * @return the args
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * setter method
     * 
     * @see SparkParam#args
     * @param args
     *            the args to set
     */
    public void setArgs(List<String> args) {
        this.args = args;
    }

    /**
     * getter method
     * 
     * @see SparkParam#deployMode
     * @return the deployMode
     */
    public String getDeployMode() {
        return deployMode;
    }

    /**
     * setter method
     * 
     * @see SparkParam#deployMode
     * @param deployMode
     *            the deployMode to set
     */
    public void setDeployMode(String deployMode) {
        this.deployMode = deployMode;
    }

    /**
     * getter method
     * 
     * @see SparkParam#driverMemory
     * @return the driverMemory
     */
    public Integer getDriverMemory() {
        return driverMemory;
    }

    /**
     * setter method
     * 
     * @see SparkParam#driverMemory
     * @param driverMemory
     *            the driverMemory to set
     */
    public void setDriverMemory(Integer driverMemory) {
        this.driverMemory = driverMemory;
    }

    /**
     * getter method
     * 
     * @see SparkParam#driverCores
     * @return the driverCores
     */
    public Integer getDriverCores() {
        return driverCores;
    }

    /**
     * setter method
     * 
     * @see SparkParam#driverCores
     * @param driverCores
     *            the driverCores to set
     */
    public void setDriverCores(Integer driverCores) {
        this.driverCores = driverCores;
    }

    /**
     * getter method
     * 
     * @see SparkParam#numExecutors
     * @return the numExecutors
     */
    public Integer getNumExecutors() {
        return numExecutors;
    }

    /**
     * setter method
     * 
     * @see SparkParam#numExecutors
     * @param numExecutors
     *            the numExecutors to set
     */
    public void setNumExecutors(Integer numExecutors) {
        this.numExecutors = numExecutors;
    }

    /**
     * getter method
     * 
     * @see SparkParam#executorMemory
     * @return the executorMemory
     */
    public Integer getExecutorMemory() {
        return executorMemory;
    }

    /**
     * setter method
     * 
     * @see SparkParam#executorMemory
     * @param executorMemory
     *            the executorMemory to set
     */
    public void setExecutorMemory(Integer executorMemory) {
        this.executorMemory = executorMemory;
    }

    /**
     * getter method
     * 
     * @see SparkParam#executorCores
     * @return the executorCores
     */
    public Integer getExecutorCores() {
        return executorCores;
    }

    /**
     * setter method
     * 
     * @see SparkParam#executorCores
     * @param executorCores
     *            the executorCores to set
     */
    public void setExecutorCores(Integer executorCores) {
        this.executorCores = executorCores;
    }

    /**
     * getter method
     * 
     * @see SparkParam#properties
     * @return the properties
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * setter method
     * 
     * @see SparkParam#properties
     * @param properties
     *            the properties to set
     */
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    /**
     * getter method
     * 
     * @see SparkParam#jarsResInfos
     * @return the jarsResInfos
     */
    public List<ResInfo> getJarsResInfos() {
        return jarsResInfos;
    }

    /**
     * setter method
     * 
     * @see SparkParam#jarsResInfos
     * @param jarsResInfos
     *            the jarsResInfos to set
     */
    public void setJarsResInfos(List<ResInfo> jarsResInfos) {
        this.jarsResInfos = jarsResInfos;
    }

    /**
     * getter method
     * 
     * @see SparkParam#filesResInfos
     * @return the filesResInfos
     */
    public List<ResInfo> getFilesResInfos() {
        return filesResInfos;
    }

    /**
     * setter method
     * 
     * @see SparkParam#filesResInfos
     * @param filesResInfos
     *            the filesResInfos to set
     */
    public void setFilesResInfos(List<ResInfo> filesResInfos) {
        this.filesResInfos = filesResInfos;
    }

    /**
     * getter method
     * 
     * @see SparkParam#archivesResInfos
     * @return the archivesResInfos
     */
    public List<ResInfo> getArchivesResInfos() {
        return archivesResInfos;
    }

    /**
     * setter method
     * 
     * @see SparkParam#archivesResInfos
     * @param archivesResInfos
     *            the archivesResInfos to set
     */
    public void setArchivesResInfos(List<ResInfo> archivesResInfos) {
        this.archivesResInfos = archivesResInfos;
    }

}
