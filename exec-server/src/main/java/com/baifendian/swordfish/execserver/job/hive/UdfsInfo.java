/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.execserver.job.hive;

import com.baifendian.swordfish.execserver.job.ResourceInfo;

import java.util.List;

/**
 * @author : liujin
 * @date : 2017-03-10 8:54
 */
public class UdfsInfo {
    private String func;

    private String className;

    private List<ResourceInfo> libJar;

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ResourceInfo> getLibJar() {
        return libJar;
    }

    public void setLibJar(List<ResourceInfo> libJar) {
        this.libJar = libJar;
    }
}
