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
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        try {
            for (String sql : value.split(";")) {
                sql = sql.replaceAll("\n", " ");
                sql = sql.replaceAll("\r", "");
                if (StringUtils.isNotBlank(sql)) {
                    ParseDriver pd = new ParseDriver();
                    pd.parse(sql);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     * getter method
     * 
     * @see
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * setter method
     * 
     * @see
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
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
            return udfs.stream().flatMap(p->p.getLibJar().stream().map(q->q.getRes())).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
