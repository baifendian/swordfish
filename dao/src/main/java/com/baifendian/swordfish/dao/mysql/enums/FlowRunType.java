package com.baifendian.swordfish.dao.mysql.enums;

/**
 *  workflow 等运行的类型
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月30日
 */
public enum FlowRunType {

    /**0(开发测试) 1(调度的任务) 2(补数据的任务) 3(流式)**/
    DIRECT_RUN, DISPATCH, ADD_DATA, STREAMING;

    public Integer getType() {
        return ordinal();
    }

    /**
     * 通过 type 获取枚举对象
     * <p>
     *
     * @param type
     * @return {@link FlowStatus}
     * @throws IllegalArgumentException
     */
    public static FlowRunType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return FlowRunType.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + FlowRunType.class.getSimpleName() + " .", ex);
        }
    }
}
