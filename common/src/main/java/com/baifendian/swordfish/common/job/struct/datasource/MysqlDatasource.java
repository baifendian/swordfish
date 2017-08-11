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
package com.baifendian.swordfish.common.job.struct.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * mysql数据源的配置参数
 */
public class MysqlDatasource extends Datasource {

  private static Logger logger = LoggerFactory.getLogger(MysqlDatasource.class.getName());

  private String address;

  private String database;

  private String user;

  private String password;

  private String autoReconnect;

  private int maxReconnect;

  private int initialTimeout;

  private boolean useUnicode;

  private String characterEncoding;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAutoReconnect() {
    return autoReconnect;
  }

  public void setAutoReconnect(String autoReconnect) {
    this.autoReconnect = autoReconnect;
  }

  public int getMaxReconnect() {
    return maxReconnect;
  }

  public void setMaxReconnect(int maxReconnect) {
    this.maxReconnect = maxReconnect;
  }

  public int getInitialTimeout() {
    return initialTimeout;
  }

  public void setInitialTimeout(int initialTimeout) {
    this.initialTimeout = initialTimeout;
  }

  public boolean isUseUnicode() {
    return useUnicode;
  }

  public void setUseUnicode(boolean useUnicode) {
    this.useUnicode = useUnicode;
  }

  public String getCharacterEncoding() {
    return characterEncoding;
  }

  public void setCharacterEncoding(String characterEncoding) {
    this.characterEncoding = characterEncoding;
  }

  /**
   * 获取 url
   * @return
   */
  public String getJdbcUrl() {
    String address = this.address;
    if (address.lastIndexOf("/") != (address.length() - 1)) {
      address += "/";
    }
    return address + this.database;
  }

  @Override
  public void isConnectable() throws Exception {
    Connection con = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      con = DriverManager.getConnection(getJdbcUrl(), this.user, this.password);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          logger.error("Mysql datasource try conn close conn error", e);
          throw e;
        }
      }
    }
  }
}
