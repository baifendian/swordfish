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

import com.baifendian.swordfish.common.job.struct.node.impexp.reader.PostgreReader;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostgreReaderArg implements ReaderArg {

  private String username;
  private String password;
  private List<String> column;
  private String splitPk;
  private String where;
  private ArrayNode connection = JsonUtil.createArrayNode();
  private Long fetchSize = 1024L;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<String> getColumn() {
    return column;
  }

  public void setColumn(List<String> column) {
    this.column = column;
  }

  public String getSplitPk() {
    return splitPk;
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

  public ArrayNode getConnection() {
    return connection;
  }

  public void setConnection(ArrayNode connection) {
    this.connection = connection;
  }

  public Long getFetchSize() {
    return fetchSize;
  }

  public void setFetchSize(Long fetchSize) {
    this.fetchSize = fetchSize;
  }

  public PostgreReaderArg(PostgreReader postgreReader) {
    ObjectNode connObject = JsonUtil.createObjectNode();

    if (StringUtils.isNotEmpty(postgreReader.getQuerySql())) {
      connObject.put("querySql", postgreReader.getQuerySql());
    }

    List<String> tableList = postgreReader.getTable();
    if (CollectionUtils.isNotEmpty(tableList)) {
      ArrayNode tableJsonList = connObject.putArray("table");
      for (String table : tableList) {
        tableJsonList.add(table);
      }
    }

    if (StringUtils.isNotEmpty(postgreReader.getWhere())) {
      connObject.put("where", postgreReader.getWhere());
    }

    connection.add(connObject);

    if (StringUtils.isNotEmpty(postgreReader.getWhere())) {
      where = postgreReader.getWhere();
    }

    column = postgreReader.getColumn();
  }

  @Override
  public String dataxName() {
    return "postgresqlreader";
  }
}
