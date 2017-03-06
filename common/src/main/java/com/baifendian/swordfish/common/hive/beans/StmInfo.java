package com.baifendian.swordfish.common.hive.beans;

import java.util.Set;

/**
 * Created by wenting on 11/14/16.
 */
public class StmInfo {

    Set<TableInfo> inputTables;

    Set<TableInfo> ouputTables;

    Set<FunctionInfo> functions;

    public Set<TableInfo> getInputTables() {
        return inputTables;
    }

    public void setInputTables(Set<TableInfo> inputTables) {
        this.inputTables = inputTables;
    }

    public Set<TableInfo> getOuputTables() {
        return ouputTables;
    }

    public void setOuputTables(Set<TableInfo> ouputTables) {
        this.ouputTables = ouputTables;
    }

    public Set<FunctionInfo> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<FunctionInfo> functions) {
        this.functions = functions;
    }

    @Override
    public String toString() {
        return "StmInfo{" +
                "inputTables=" + inputTables +
                ", ouputTables=" + ouputTables +
                ", functions=" + functions +
                '}';
    }
}
