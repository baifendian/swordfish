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

import com.baifendian.swordfish.common.enums.MongoWriteMode;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.MongoColumn;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.MongoWriter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * mongo wirter DataX 配置
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoWriterArg implements WriterArg {
  private List<String> address;
  private String userName;
  private String userPassword;
  private String dbName;
  private String collectionName;
  private List<MongoColumn> column;
  private String upsertKey;
  private MongoWriteMode writeMode;

  public List<String> getAddress() {
    return address;
  }

  public void setAddress(List<String> address) {
    this.address = address;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public String getCollectionName() {
    return collectionName;
  }

  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

  public List<MongoColumn> getColumn() {
    return column;
  }

  public void setColumn(List<MongoColumn> column) {
    this.column = column;
  }

  public String getUpsertKey() {
    return upsertKey;
  }

  public void setUpsertKey(String upsertKey) {
    this.upsertKey = upsertKey;
  }

  public MongoWriteMode getWriteMode() {
    return writeMode;
  }

  public void setWriteMode(MongoWriteMode writeMode) {
    this.writeMode = writeMode;
  }

  public MongoWriterArg(MongoWriter mongoWriter) {
    this.upsertKey = mongoWriter.getUpsetKey();
    this.writeMode = mongoWriter.getWirteMode();
    this.collectionName = mongoWriter.getTable();
    this.column = mongoWriter.getColumn();
  }

  @Override
  public String dataxName() {
    return "mongodbwriter";
  }
}
