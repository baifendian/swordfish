package com.baifendian.swordfish.dao.mysql.enums;

/**
 *  workflow 依赖执行的策略
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月22日
 */
public enum DepPolicyType {

    /**0.不依赖上一调度周期  1.自动依赖等待上一周期结束才能继续**/
    NO_DEP_PRE, DEP_PRE;
    /**
     * getter method
     *
     * @see DepPolicyType
     * @return the type
     */
    public Integer getType() {
        return ordinal();
    }

    /**
     * 通过 type 获取枚举对象
     * <p>
     *
     * @param type
     * @return {@link DepPolicyType}
     * @throws IllegalArgumentException
     */
    public static DepPolicyType valueOfType(Integer type) throws IllegalArgumentException {
        if (type == null) {
            return null;
        }
        try {
            return DepPolicyType.values()[type];
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cannot convert " + type + " to " + DepPolicyType.class.getSimpleName() + " .", ex);
        }
    }
}
