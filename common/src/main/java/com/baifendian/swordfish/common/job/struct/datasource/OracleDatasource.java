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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Oracle数据源的参数配置
 */
public class OracleDatasource extends Datasource {

  private static Logger logger = LoggerFactory.getLogger(OracleDatasource.class.getName());

  private String host;

  private int port;

  private String service;

  private String user;
  private String password;

  public static Logger getLogger() {
    return logger;
  }

  public static void setLogger(Logger logger) {
    OracleDatasource.logger = logger;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
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

  @Override
  public void isConnectable() throws Exception {
    Connection con = null;
    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");

      String address = MessageFormat.format("jdbc:oracle:thin:@//{0}:{1}/{2} ", this.host, String.valueOf(this.port), this.service);

      con = DriverManager.getConnection(address, this.user, this.password);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          logger.error("Orcale datasource try conn close conn error", e);
          throw e;
        }
      }
    }
  }
}
