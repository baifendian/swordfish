package com.baifendian.swordfish.dao.hive.model;

import java.util.Date;
import java.util.List;

/**
 * author: smile8
 * date:   03/11/2016
 * desc:
 */
public class TableLocationData {
  private String dbName;

  private String tableName;

  private String owner;

  private Date createTime;

  private List<Field> partitionKeys;

  private String location;

  private List<PartitionData> partitionDatas;

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

  public List<Field> getPartitionKeys() {
    return partitionKeys;
  }

  public void setPartitionKeys(List<Field> partitionKeys) {
    this.partitionKeys = partitionKeys;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public List<PartitionData> getPartitionDatas() {
    return partitionDatas;
  }

  public void setPartitionDatas(List<PartitionData> partitionDatas) {
    this.partitionDatas = partitionDatas;
  }

  @Override
  public String toString() {
    return "TableLocationData{" +
        "dbName='" + dbName + '\'' +
        ", tableName='" + tableName + '\'' +
        ", owner='" + owner + '\'' +
        ", createTime=" + createTime +
        ", partitionKeys=" + partitionKeys +
        ", location='" + location + '\'' +
        ", partitionDatas=" + partitionDatas +
        '}';
  }
}
