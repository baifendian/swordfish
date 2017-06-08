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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * mysql 读取对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MysqlReader implements Reader {

  private String datasource;

  private List<String> table;

  private String splitPk;

  private String where;

  private String querySql;

  private List<String> column;

  public String getDatasource() {
    return datasource;
  }

  public void setDatasource(String datasource) {
    this.datasource = datasource;
  }

  public List<String> getTable() {
    return table;
  }

  public void setTable(List<String> table) {
    this.table = table;
  }

  public void setSplitPk(String splitPk) {
    this.splitPk = splitPk;
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
    return StringUtils.isNotEmpty(datasource) && (StringUtils.isNotEmpty(querySql) || CollectionUtils.isNotEmpty(table));
  }
}
