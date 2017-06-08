package com.baifendian.swordfish.common.job.struct.node.impexp.column;

import com.baifendian.swordfish.common.enums.HiveColumnType;

/**
 * hive Column信息
 */
public class HiveColumn {
  private String name;
  private HiveColumnType type;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public HiveColumnType getType() {
    return type;
  }

  public void setType(HiveColumnType type) {
    this.type = type;
  }
}
