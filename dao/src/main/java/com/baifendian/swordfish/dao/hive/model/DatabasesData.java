package com.baifendian.swordfish.dao.hive.model;

import java.util.List;

/**
 * hive 中所有的数据库
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月8日
 */
public class DatabasesData {

    private List<String> databases;

    public List<String> getDatabases() {
        return databases;
    }

    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }
}
