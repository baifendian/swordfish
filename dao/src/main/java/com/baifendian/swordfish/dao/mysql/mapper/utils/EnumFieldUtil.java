/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月26日
 * File Name      : EnumFieldUtil.java
 */

package com.baifendian.swordfish.dao.mysql.mapper.utils;

import com.baifendian.swordfish.dao.mysql.enums.NodeType;
import com.baifendian.swordfish.dao.mysql.enums.NodeTypeHandler;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

/**
 * mybatis enum类型字段的工具
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月26日
 */
public class EnumFieldUtil {

    /**
     * 生成enum字段的字符串
     * <p>
     *
     * @param field
     * @param enumClass
     * @return 字段字符串
     */
    public static String genFieldStr(String field, Class<?> enumClass) {
        if (enumClass.equals(NodeType.class)) { // NodeType类型进行特殊处理
            return "#{" + field + ",javaType=" + enumClass.getName() + ",typeHandler=" + NodeTypeHandler.class.getName() + "}";
        }
        return "#{" + field + ",javaType=" + enumClass.getName() + ",typeHandler=" + EnumOrdinalTypeHandler.class.getName() + "}";
    }

    /**
     * 生成enum字段的字符串(MessageFormat特殊字符)
     * <p>
     *
     * @param field
     * @param enumClass
     * @return 字段字符串
     */
    public static String genFieldSpecialStr(String field, Class<?> enumClass) {
        if (enumClass.equals(NodeType.class)) { // NodeType类型进行特殊处理
            return "#'{'" + field + ",javaType=" + enumClass.getName() + ",typeHandler=" + NodeTypeHandler.class.getName() + "}";
        }
        return "#'{'" + field + ",javaType=" + enumClass.getName() + ",typeHandler=" + EnumOrdinalTypeHandler.class.getName() + "}";
    }

}
