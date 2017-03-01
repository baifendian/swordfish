/*
 * Create Author  : dsfan
 * Create Date    : 2017年1月3日
 * File Name      : FlowErrorCode.java
 */

package com.baifendian.swordfish.dao.mysql.enums;

/**
 * workflow 运行失败时的详细错误码
 * <p>
 * 
 * @author : dsfan
 * @date : 2017年1月3日
 */
public enum FlowErrorCode {
    /** 0(成功) 1(依赖自身调度的上一周期运行失败) 2(依赖的工作流运行失败) 3(运行失败) 4(被人工 KILL) **/
    SUCCESS, DEP_PRE_FAILED, DEP_FAILED, EXEC_FAILED, KILL;
    
    public Integer getType() {
        return ordinal();
    }

    /**
     * 通过 type 获取枚举对象
     * <p>
     *
     * @param type
     * @return {@link FlowErrorCode}
     * @throws IllegalArgumentException
     */
    public static FlowErrorCode valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return FlowErrorCode.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + FlowErrorCode.class.getSimpleName() + " .", ex);
        }
    }
}
