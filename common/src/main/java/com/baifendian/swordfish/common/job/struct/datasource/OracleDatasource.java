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

import oracle.jdbc.pool.OracleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Oracle数据源的参数配置
 */
public class OracleDatasource extends Datasource {

  private static Logger logger = LoggerFactory.getLogger(OracleDatasource.class.getName());

  private String driverType = "oci";
  private String serverName;
  private String networkProtocol = "tcp";
  private String databaseName;
  private int portNumber;
  private String user;
  private String password;

  public String getDriverType() {
    return driverType;
  }

  public void setDriverType(String driverType) {
    this.driverType = driverType;
  }

  public String getServerName() {
    return serverName;
  }

  public void setServerName(String serverName) {
    this.serverName = serverName;
  }

  public String getNetworkProtocol() {
    return networkProtocol;
  }

  public void setNetworkProtocol(String networkProtocol) {
    this.networkProtocol = networkProtocol;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public int getPortNumber() {
    return portNumber;
  }

  public void setPortNumber(int portNumber) {
    this.portNumber = portNumber;
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
      OracleDataSource ods = new OracleDataSource();

      ods.setDriverType(this.driverType);
      ods.setServerName(this.serverName);
      ods.setNetworkProtocol(this.networkProtocol);
      ods.setDataSourceName(this.databaseName);
      ods.setPortNumber(this.portNumber);
      ods.setUser(this.user);
      ods.setPassword(this.password);

      con = ods.getConnection();
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          logger.error("Oracle datasource try conn close conn error", e);
          throw e;
        }
      }
    }
  }
}
