/*
 * Create Author  : dsfan
 * Create Date    : 2016年9月7日
 * File Name      : NodeTypeHandler.java
 */

package com.baifendian.swordfish.dao.mysql.enums;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * NodeType类型的自定义处理
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年9月7日
 */
public class NodeTypeHandler extends BaseTypeHandler<NodeType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, NodeType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getType());
    }

    @Override
    public NodeType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int i = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            return NodeType.valueOfType(i);
        }
    }

    @Override
    public NodeType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int i = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        } else {
            return NodeType.valueOfType(i);
        }
    }

    @Override
    public NodeType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int i = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        } else {
            return NodeType.valueOfType(i);
        }
    }

}
