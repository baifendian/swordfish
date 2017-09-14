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

import com.baifendian.swordfish.common.enums.MongoWriteMode;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.MongoColumn;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Mongo writer
 */
public class MongoWriter implements Writer {

  private String datasource;
  private String table;
  private MongoWriteMode writeMode;
  private List<MongoColumn> column;
  private String upsetKey;

  public String getDatasource() {
    return datasource;
  }

  public void setDatasource(String datasource) {
    this.datasource = datasource;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public MongoWriteMode getWriteMode() {
    return writeMode;
  }

  public void setWriteMode(MongoWriteMode writeMode) {
    this.writeMode = writeMode;
  }

  public List<MongoColumn> getColumn() {
    return column;
  }

  public void setColumn(List<MongoColumn> column) {
    this.column = column;
  }

  public String getUpsetKey() {
    return upsetKey;
  }

  public void setUpsetKey(String upsetKey) {
    this.upsetKey = upsetKey;
  }

  @Override
  public boolean checkValid() {
    return StringUtils.isNotEmpty(datasource) &&
        StringUtils.isNotEmpty(table) &&
        writeMode != null &&
        CollectionUtils.isNotEmpty(column) &&
        !(writeMode.hasUpsetKey() && StringUtils.isEmpty(upsetKey));
  }
}
