package com.baifendian.swordfish.dao.hive.model;

import java.util.Map;

/**
 * 序列化反序列化信息
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月10日
 */
public class SerDeInfo {

    private String name;

    private String serializationLib;

    private Map<String,String> parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerializationLib() {
        return serializationLib;
    }

    public void setSerializationLib(String serializationLib) {
        this.serializationLib = serializationLib;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
