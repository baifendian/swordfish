package com.baifendian.swordfish.dao.hive.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * table基本信息
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月8日
 */
public class TableData {

    private String dbName;

    private String tableName;

    private String owner;

    private Date createTime;

    private String tableType;

    private String viewOriginText;

    private String viewExpandedText;

    private Map<String, String> parameters;

    private List<Field> partitionKeys;

    private List<Field> cols;

    private String location;

    private Boolean compressed;

    private String inputFormat;

    private String outputFormat;

    private SerDeInfo serDeInfo;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getViewOriginText() {
        return viewOriginText;
    }

    public void setViewOriginText(String viewOriginText) {
        this.viewOriginText = viewOriginText;
    }

    public String getViewExpandedText() {
        return viewExpandedText;
    }

    public void setViewExpandedText(String viewExpandedText) {
        this.viewExpandedText = viewExpandedText;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public List<Field> getPartitionKeys() {
        return partitionKeys;
    }

    public void setPartitionKeys(List<Field> partitionKeys) {
        this.partitionKeys = partitionKeys;
    }

    public List<Field> getCols() {
        return cols;
    }

    public void setCols(List<Field> cols) {
        this.cols = cols;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getCompressed() {
        return compressed;
    }

    public void setCompressed(Boolean compressed) {
        this.compressed = compressed;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public SerDeInfo getSerDeInfo() {
        return serDeInfo;
    }

    public void setSerDeInfo(SerDeInfo serDeInfo) {
        this.serDeInfo = serDeInfo;
    }
}
