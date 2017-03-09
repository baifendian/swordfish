/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月30日
 * File Name      : MrParam.java
 */

package com.baifendian.swordfish.execserver.job.mr;

import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.dao.mysql.model.flow.params.Property;
import com.baifendian.swordfish.execserver.job.ResourceInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MR 节点的参数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月30日
 */
public class MrParam extends BaseParam {

    /**
     * 主jar包资源信息
     */
    private String mainJar;

    /**
     * 主程序
     */
    private String mainClass;

    /**
     * 参数信息
     */
    private String args;

    /**
     * 配置信息列表
     */
    private List<Property> properties;

    /**
     * 额外的 jar 包，指的是本地的文件，这个可能很长
     */
    private List<ResourceInfo> libJars;

    /**
     * 额外的文件，指的是本地的文件，可能是多条配置
     */
    private List<ResourceInfo> files;

    /**
     * 额外的压缩文件，指的是本地的文件，可能是多条配置
     */
    private List<ResourceInfo> archives;

    /** 执行队列 */
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

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
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

    public void setLibJars(List<ResourceInfo> libJars) {
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

    public List<String> getDArgs(){
        return this.properties.stream().map(prop ->  prop.getProp() + "=" + prop.getValue())
                .collect(Collectors.toList());
    }

    public void setDArgs(){

    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}


