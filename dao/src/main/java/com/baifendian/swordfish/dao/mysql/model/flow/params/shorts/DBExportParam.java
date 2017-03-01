package com.baifendian.swordfish.dao.mysql.model.flow.params.shorts;

import com.baifendian.swordfish.dao.mysql.enums.DbType;
import com.baifendian.swordfish.dao.mysql.enums.WriterModeType;
import com.baifendian.swordfish.dao.mysql.model.flow.params.BaseParam;

import java.util.List;

public class DBExportParam extends BaseParam{
	//导出api名称
	private String action;

    //导出数据源id
    private int originId;

    private String originName;

    private List<ColumnData> sourceColumnData;

    private String domainId;

    private String domainName;

	//导出目标类型
	private DbType targetType;
	
	//导出数据源id
	private int targetId;

    private String targetName;
	
	//导出目标表名
	private String targetTableName;

    private List<ColumnData> targetColumnData;

    private String preStmt;

    private String postStmt;

    //导出字段关系
    private List<MappingRelation> mappingRelation;

    //导出过滤
    private String filter;

    //
    private WriterModeType writerMode;

    //更新参考列列表
    private List<String> updateKeys;

    // hbase时间戳设置
    private VersionColumnParam versionColumn;

    private List<RowkeyColumnParam> rowkeyColumn;

    // hbase编码设置
    private String encoding;

    public DbType getTargetType() {
        return targetType;
    }

    public void setTargetType(DbType targetType) {
        this.targetType = targetType;
    }

    public WriterModeType getWriterMode() {
        return writerMode;
    }

    public void setWriterMode(WriterModeType writerMode) {
        this.writerMode = writerMode;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public int getOriginId() {
        return originId;
    }

    public void setOriginId(int originId) {
        this.originId = originId;
    }

    public List<ColumnData> getSourceColumnData() {
        return sourceColumnData;
    }

    public void setSourceColumnData(List<ColumnData> sourceColumnData) {
        this.sourceColumnData = sourceColumnData;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public List<ColumnData> getTargetColumnData() {
        return targetColumnData;
    }

    public void setTargetColumnData(List<ColumnData> targetColumnData) {
        this.targetColumnData = targetColumnData;
    }

    public String getPreStmt() {
        return preStmt;
    }

    public void setPreStmt(String preStmt) {
        this.preStmt = preStmt;
    }

    public String getPostStmt() {
        return postStmt;
    }

    public void setPostStmt(String postStmt) {
        this.postStmt = postStmt;
    }

    public List<MappingRelation> getMappingRelation() {
        return mappingRelation;
    }

    public void setMappingRelation(List<MappingRelation> mappingRelation) {
        this.mappingRelation = mappingRelation;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<String> getUpdateKeys() {
        return updateKeys;
    }

    public void setUpdateKeys(List<String> updateKeys) {
        this.updateKeys = updateKeys;
    }

    public VersionColumnParam getVersionColumn() {
        return versionColumn;
    }

    public void setVersionColumn(VersionColumnParam versionColumn) {
        this.versionColumn = versionColumn;
    }

    public List<RowkeyColumnParam> getRowkeyColumn() {
        return rowkeyColumn;
    }

    public void setRowkeyColumn(List<RowkeyColumnParam> rowkeyColumn) {
        this.rowkeyColumn = rowkeyColumn;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
