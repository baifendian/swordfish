package com.baifendian.swordfish.dao.hive.model;

import java.util.List;

/**
 * 一个数据库对应的所有表名
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月8日
 */
public class TablesData {

    private List<String> tables;

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }
}
