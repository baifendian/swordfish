package com.baifendian.swordfish.execserver.job.impexp.Args;

import com.baifendian.swordfish.common.job.struct.node.impexp.column.HiveColumn;
import org.apache.commons.lang.StringUtils;

/**
 * Hive sql中字段参数
 */
public class HqlColumn {
  private String name;
  private String type;

  public HqlColumn() {
  }

  public HqlColumn(String name, String type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  /**
   * 判断一个HqlColumn 和一个 hiveColumn 是否相同
   * @param hiveColumn
   * @return
   */
  public boolean equals(HiveColumn hiveColumn) {
    return StringUtils.equalsIgnoreCase(name, hiveColumn.getName()) &&
            StringUtils.containsIgnoreCase(type, hiveColumn.getType().name());
  }
}
