package com.baifendian.swordfish.common.hive.beans;

import com.google.common.base.Objects;

/**
 * Created by wenting on 11/15/16.
 */
public class TableInfo {

    private String tableName;

    private String dbName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof TableInfo) {
            TableInfo that = (TableInfo) other;
            return Objects.equal(this.tableName, that.tableName) && Objects.equal(this.dbName, that.dbName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tableName, dbName);
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "tableName='" + tableName + '\'' +
                ", dbName='" + dbName + '\'' +
                '}';
    }
}
