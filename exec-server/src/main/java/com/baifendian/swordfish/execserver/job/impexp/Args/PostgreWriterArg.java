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

import com.baifendian.swordfish.common.enums.MysqlWriteMode;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.MysqlWriter;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.PostgreWriter;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class PostgreWriterArg implements WriterArg {

  private MysqlWriteMode writeMode;

  private String username;

  private String password;

  private List<String> column;

  private List<String> session;

  private List<String> preSql;

  private List<String> postSql;

  private Long batchSize;

  private ArrayNode connection = JsonUtil.createArrayNode();

  public MysqlWriteMode getWriteMode() {
    return writeMode;
  }

  public void setWriteMode(MysqlWriteMode writeMode) {
    this.writeMode = writeMode;
  }

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

  public List<String> getSession() {
    return session;
  }

  public void setSession(List<String> session) {
    this.session = session;
  }

  public List<String> getPreSql() {
    return preSql;
  }

  public void setPreSql(List<String> preSql) {
    this.preSql = preSql;
  }

  public List<String> getPostSql() {
    return postSql;
  }

  public void setPostSql(List<String> postSql) {
    this.postSql = postSql;
  }

  public Long getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(Long batchSize) {
    this.batchSize = batchSize;
  }

  public ArrayNode getConnection() {
    return connection;
  }

  public void setConnection(ArrayNode connection) {
    this.connection = connection;
  }

  public PostgreWriterArg(PostgreWriter postgreWriter) {
    ObjectNode connObject = JsonUtil.createObjectNode();

    List<String> tableList = Arrays.asList(postgreWriter.getTable());
    if (CollectionUtils.isNotEmpty(tableList)) {
      ArrayNode tableJsonList = connObject.putArray("table");
      for (String table : tableList) {
        tableJsonList.add(table);
      }
    }

    preSql = CommonUtil.sqlSplit(postgreWriter.getPreSql());
    postSql = CommonUtil.sqlSplit(postgreWriter.getPostSql());
    column = postgreWriter.getColumn();
    session = postgreWriter.getSession();
    batchSize = postgreWriter.getBatchSize();
    writeMode = postgreWriter.getWriteMode();

    connection.add(connObject);
  }

  @Override
  public String dataxName() {
    return "postgresqlwriter";
  }

}
