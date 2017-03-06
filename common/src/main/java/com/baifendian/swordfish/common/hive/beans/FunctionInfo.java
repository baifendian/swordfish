package com.baifendian.swordfish.common.hive.beans;

import com.google.common.base.Objects;

/**
 * Created by wenting on 11/15/16.
 */
public class FunctionInfo {

    private String functionName;

    private String dbName;

    private boolean ifDefaultDb;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public boolean isIfDefaultDb() {
        return ifDefaultDb;
    }

    public void setIfDefaultDb(boolean ifDefaultDb) {
        this.ifDefaultDb = ifDefaultDb;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof FunctionInfo) {
            FunctionInfo that = (FunctionInfo) other;
            return Objects.equal(this.functionName, that.functionName) && Objects.equal(this.dbName, that.dbName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(functionName, dbName);
    }

    @Override
    public String toString() {
        return "FunctionInfo{" +
                "functionName='" + functionName + '\'' +
                ", dbName='" + dbName + '\'' +
                '}';
    }
}
