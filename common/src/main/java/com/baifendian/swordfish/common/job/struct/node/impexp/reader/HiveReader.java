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
package com.baifendian.swordfish.common.job.struct.node.impexp.reader;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Hive reader
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HiveReader implements Reader {
  private String database;
  private String table;
  private String where;
  private String querySql;
  private List<String> column;

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

  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  public String getQuerySql() {
    return querySql;
  }

  public void setQuerySql(String querySql) {
    this.querySql = querySql;
  }

  public List<String> getColumn() {
    return column;
  }

  public void setColumn(List<String> column) {
    this.column = column;
  }

  @Override
  public boolean checkValid() {
    return StringUtils.isNotEmpty(database) &&
            (StringUtils.isNotEmpty(table) || StringUtils.isNotEmpty(querySql)) &&
            CollectionUtils.isNotEmpty(column);
  }
}
