/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月6日
 * File Name      : AdHocJsonObject.java
 */

package com.baifendian.swordfish.dao.mysql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 即席查询结果的 JsonObject
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月6日
 */
public class AdHocJsonObject {
    /** 返回的表头 */
    private List<String> titles;

    /** 返回的数据 */
    private List<List<String>> values = new ArrayList<List<String>>();

    /**
     * getter method
     * 
     * @see AdHocJsonObject#titles
     * @return the titles
     */
    public List<String> getTitles() {
        return titles;
    }

    /**
     * setter method
     * 
     * @see AdHocJsonObject#titles
     * @param titles
     *            the titles to set
     */
    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    /**
     * getter method
     * 
     * @see AdHocJsonObject#values
     * @return the values
     */
    public List<List<String>> getValues() {
        return values;
    }

    /**
     * setter method
     * 
     * @see AdHocJsonObject#values
     * @param values
     *            the values to set
     */
    public void setValues(List<List<String>> values) {
        this.values = values;
    }

}
