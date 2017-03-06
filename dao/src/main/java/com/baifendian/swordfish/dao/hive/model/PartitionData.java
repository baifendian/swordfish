package com.baifendian.swordfish.dao.hive.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * author: smile8
 * date:   03/11/2016
 * desc:
 */
public class PartitionData {
  /**
   * 分区的值
   */
  private List<String> values;

  /**
   * 字段和分区值合并
   */
  private String fvalues;

  /**
   * 分区创建时间
   */
  private Timestamp createTime;

  /**
   * 分区的路径
   */
  private String location;

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  public String getFvalues() {
    return fvalues;
  }

  public void setFvalues(String fvalues) {
    this.fvalues = fvalues;
  }

  public Timestamp getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  @Override
  public String toString() {
    return "PartitionData{" +
        "values=" + values +
        ", fvalues='" + fvalues + '\'' +
        ", createTime=" + createTime +
        ", location='" + location + '\'' +
        '}';
  }
}
