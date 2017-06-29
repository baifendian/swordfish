/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
