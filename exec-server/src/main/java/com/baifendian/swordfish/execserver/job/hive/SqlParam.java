/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月30日
 * File Name      : SqlParam.java
 */

package com.baifendian.swordfish.execserver.job.hive;

import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.execserver.job.ResourceInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * sql 节点参数
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月30日
 */
public class SqlParam extends BaseParam {

    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** 原始 sql 语句（多条，内部可能包含换行等符号，执行时需要处理） */
    private String sql;

    private List<UdfsInfo> udfs;

    @Override
    public boolean checkValid() {
        if (StringUtils.isEmpty(sql)) {
            return false;
        }
        try {
            for (String sqlOne : sql.split(";")) {
                sqlOne = sqlOne.replaceAll("\n", " ");
                sqlOne = sqlOne.replaceAll("\r", "");
                if (StringUtils.isNotBlank(sqlOne)) {
                    ParseDriver pd = new ParseDriver();
                    pd.parse(sqlOne);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<UdfsInfo> getUdfs() {
        return udfs;
    }

    public void setUdfs(List<UdfsInfo> udfs) {
        this.udfs = udfs;
    }

    @Override
    public List<String> getResourceFiles(){
        if(udfs != null && !udfs.isEmpty()){
            return udfs.stream().filter(p->p.getLibJar() != null && p.getLibJar().isProjectScope())
                .map(p->p.getLibJar().getRes()).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
