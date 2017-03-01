/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月30日
 * File Name      : BaseParam.java
 */

package com.baifendian.swordfish.dao.mysql.model.flow.params;

import java.util.List;

/**
 * 参数基类(需要校验参数和获取资源的子类需要 @Override 对应的方法，可参考：{@link MrParam})
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月30日
 */
public class BaseParam {

    /**
     * 校验参数是否合法（需要校验的子类 @Override 该方法）
     * <p>
     *
     * @return 是否合法
     */
    public boolean checkValid() {
        return true;
    }

    /**
     * 获取所有的资源 id 列表（需要获取资源的子类 @Override 该方法）
     * <p>
     *
     * @return 资源 id 列表
     */
    public List<Integer> findAllResourceIds() {
        return null;
    }

    /**
     * 添加资源 id 到列表中
     * <p>
     *
     * @param resInfos
     * @param resourceIds
     */
    protected void addToResourceIds(List<ResInfo> resInfos, List<Integer> resourceIds) {
        for (ResInfo resInfo : resInfos) {
            resourceIds.add(resInfo.getId());
        }
    }

}
