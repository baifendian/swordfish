/*
 * Create Author  : dsfan
 * Create Date    : 2016年10月10日
 * File Name      : ResInfo.java
 */

package com.baifendian.swordfish.dao.mysql.model.flow.params;

/**
 * 资源信息
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年10月10日
 */
public class ResInfo {
    /** 资源 id */
    private int id;

    /** 资源别名 */
    private String alias;

    /** 资源的相对路径(用于第三方接口) */
    private String path;

    /**
     * getter method
     * 
     * @see ResInfo#id
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * setter method
     * 
     * @see ResInfo#id
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter method
     * 
     * @see ResInfo#alias
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * setter method
     * 
     * @see ResInfo#alias
     * @param alias
     *            the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * getter method
     * 
     * @see ResInfo#path
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * setter method
     * 
     * @see ResInfo#path
     * @param path
     *            the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

}
