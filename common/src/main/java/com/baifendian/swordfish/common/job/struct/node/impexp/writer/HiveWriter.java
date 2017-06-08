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
package com.baifendian.swordfish.common.job.struct.node.impexp.writer;

import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.HiveColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * hive 写入对象
 */
public class HiveWriter implements Writer {

  private String database;
  private String table;
  private WriteMode writerMode;
  private List<HiveColumn> column;

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public WriteMode getWriterMode() {
    return writerMode;
  }

  public void setWriterMode(WriteMode writerMode) {
    this.writerMode = writerMode;
  }

  public List<HiveColumn> getColumn() {
    return column;
  }

  public void setColumn(List<HiveColumn> column) {
    this.column = column;
  }

  @Override
  public boolean checkValid() {
    return StringUtils.isNotEmpty(database) && StringUtils.isNotEmpty(table);
  }
}
