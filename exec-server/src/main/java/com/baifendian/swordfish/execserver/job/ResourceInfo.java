/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.execserver.job;

import org.apache.commons.lang3.StringUtils;

/**
 * @author : liujin
 * @date : 2017-03-09 16:34
 */
public class ResourceInfo {

    private String scope;

    private String res;

    private String alias;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSymbolicRes(){
        if(alias != null && !alias.isEmpty())
            return res + "#" + alias;
        else
            return res;
    }

    /**
     * scope 没有值时默认为project
     * @return
     */
    public boolean isProjectScope() {
        return StringUtils.isEmpty(scope) || scope.equals("project");
    }
}
