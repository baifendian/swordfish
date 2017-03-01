/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月30日
 * File Name      : MrParam.java
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
 * MR 节点的参数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月30日
 */
public class MrParam extends BaseParam {

    /** 主jar包资源信息 */
    private ResInfo mainJarResInfo;

    /** 主程序 */
    private String mainClass;

    /** 参数信息 */
    private List<String> args;

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
     * @see MrParam#mainJarResInfo
     * @return the mainJarResInfo
     */
    public ResInfo getMainJarResInfo() {
        return mainJarResInfo;
    }

    /**
     * setter method
     * 
     * @see MrParam#mainJarResInfo
     * @param mainJarResInfo
     *            the mainJarResInfo to set
     */
    public void setMainJarResInfo(ResInfo mainJarResInfo) {
        this.mainJarResInfo = mainJarResInfo;
    }

    /**
     * getter method
     * 
     * @see MrParam#mainClass
     * @return the mainClass
     */
    public String getMainClass() {
        return mainClass;
    }

    /**
     * setter method
     * 
     * @see MrParam#mainClass
     * @param mainClass
     *            the mainClass to set
     */
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * getter method
     * 
     * @see MrParam#args
     * @return the args
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * setter method
     * 
     * @see MrParam#args
     * @param args
     *            the args to set
     */
    public void setArgs(List<String> args) {
        this.args = args;
    }

    /**
     * getter method
     * 
     * @see MrParam#properties
     * @return the properties
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * setter method
     * 
     * @see MrParam#properties
     * @param properties
     *            the properties to set
     */
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    /**
     * getter method
     * 
     * @see MrParam#jarsResInfos
     * @return the jarsResInfos
     */
    public List<ResInfo> getJarsResInfos() {
        return jarsResInfos;
    }

    /**
     * setter method
     * 
     * @see MrParam#jarsResInfos
     * @param jarsResInfos
     *            the jarsResInfos to set
     */
    public void setJarsResInfos(List<ResInfo> jarsResInfos) {
        this.jarsResInfos = jarsResInfos;
    }

    /**
     * getter method
     * 
     * @see MrParam#filesResInfos
     * @return the filesResInfos
     */
    public List<ResInfo> getFilesResInfos() {
        return filesResInfos;
    }

    /**
     * setter method
     * 
     * @see MrParam#filesResInfos
     * @param filesResInfos
     *            the filesResInfos to set
     */
    public void setFilesResInfos(List<ResInfo> filesResInfos) {
        this.filesResInfos = filesResInfos;
    }

    /**
     * getter method
     * 
     * @see MrParam#archivesResInfos
     * @return the archivesResInfos
     */
    public List<ResInfo> getArchivesResInfos() {
        return archivesResInfos;
    }

    /**
     * setter method
     * 
     * @see MrParam#archivesResInfos
     * @param archivesResInfos
     *            the archivesResInfos to set
     */
    public void setArchivesResInfos(List<ResInfo> archivesResInfos) {
        this.archivesResInfos = archivesResInfos;
    }

}
