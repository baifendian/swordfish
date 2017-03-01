package com.baifendian.swordfish.dao.mysql.model.flow.params.shorts;

import com.baifendian.swordfish.dao.mysql.enums.DbType;
import com.baifendian.swordfish.dao.mysql.enums.WriterModeType;
import com.baifendian.swordfish.dao.mysql.model.flow.params.BaseParam;

import java.util.List;

public class DBImportParam extends BaseParam {
    // 导入请求接口名
    private String action;

    //rdb源类型
    private DbType originType;

    //rdb源id
    private int originId;

    private String originName;

    //rdb表名
    private String originTableName;

    private List<ColumnData> sourceColumnData;

    private String domainId;

    private String domainName;

    //目标物理实体id
    private int targetId;

    private String targetName;

    //目标字段列表
    private List<ColumnData> targetColumnData;

    //字段映射关系
    private List<MappingRelation> mappingRelation;

    //导入过滤条件
    private String filter;

    //导入写入模式
    private WriterModeType writerMode;

    //导入并行切分字段
    private String splitBy;

    //导入并行数
    private int mapNum;

    public DbType getOriginType() {
        return originType;
    }

    public void setOriginType(DbType originType) {
        this.originType = originType;
    }

    public WriterModeType getWriterMode() {
        return writerMode;
    }

    public void setWriterMode(WriterModeType writerMode) {
        this.writerMode = writerMode;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
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

    public int getOriginId() {
        return originId;
    }

    public void setOriginId(int originId) {
        this.originId = originId;
    }

    public String getOriginTableName() {
        return originTableName;
    }

    public void setOriginTableName(String originTableName) {
        this.originTableName = originTableName;
    }

    public List<ColumnData> getSourceColumnData() {
        return sourceColumnData;
    }

    public void setSourceColumnData(List<ColumnData> sourceColumnData) {
        this.sourceColumnData = sourceColumnData;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public List<ColumnData> getTargetColumnData() {
        return targetColumnData;
    }

    public void setTargetColumnData(List<ColumnData> targetColumnData) {
        this.targetColumnData = targetColumnData;
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

    public String getSplitBy() {
        return splitBy;
    }

    public void setSplitBy(String splitBy) {
        this.splitBy = splitBy;
    }

    public int getMapNum() {
        return mapNum;
    }

    public void setMapNum(int mapNum) {
        this.mapNum = mapNum;
    }
}
