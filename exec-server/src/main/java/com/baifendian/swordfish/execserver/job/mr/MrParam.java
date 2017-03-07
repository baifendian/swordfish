/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月30日
 * File Name      : MrParam.java
 */

package com.baifendian.swordfish.execserver.job.mr;

import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.dao.mysql.model.flow.params.Property;
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
    private List<String> args;

    /**
     * 配置信息列表
     */
    private List<Property> properties;

    /**
     * 额外的 jar 包，指的是本地的文件，这个可能很长
     */
    private List<String> jars;

    /**
     * 额外的文件，指的是本地的文件，可能是多条配置
     */
    private List<String> files;

    /**
     * 额外的压缩文件，指的是本地的文件，可能是多条配置
     */
    private List<String> archives;

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

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
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


