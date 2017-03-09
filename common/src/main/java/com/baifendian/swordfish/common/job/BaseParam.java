/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月30日
 * File Name      : BaseParam.java
 */

package com.baifendian.swordfish.common.job;

import java.util.List;

/**
 * 参数基类(需要校验参数和获取资源的子类需要 @Override 对应的方法，可参考：{@link MrParam})
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月30日
 */
public abstract class BaseParam {

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
     * 获取node需要的资源文件清单，用于后续做软链接处理
     * @return
     */
    public abstract List<String> getResourceFiles();

}
