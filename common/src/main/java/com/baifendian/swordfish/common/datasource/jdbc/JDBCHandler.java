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
package com.baifendian.swordfish.common.datasource.jdbc;

import com.baifendian.swordfish.common.datasource.DataSourceHandler;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JDBCHandler implements DataSourceHandler {

  private static final Logger logger = LoggerFactory.getLogger(JDBCHandler.class);

  private JDBCParam param;
  private DbType dbType;

  private static Map<DbType, String> dbDriverMap = new HashMap<>();

  static{
    dbDriverMap.put(DbType.MYSQL, "com.mysql.jdbc.Driver");
    dbDriverMap.put(DbType.ORACLE, "oracle.jdbc.driver.OracleDriver");
    dbDriverMap.put(DbType.SQLSERVER, "com.microsoft.jdbc.sqlserver.SQLServerDriver");
  }

  public JDBCHandler(DbType dbType, String paramStr){
    this.dbType = dbType;
    param = JsonUtil.parseObject(paramStr, JDBCParam.class);
  }
  public void isConnectable() throws Exception {
    if(!dbDriverMap.containsKey(dbType)){
      throw new Exception(String.format("can't found db type %s driver info", dbType.name()));
    }

    Connection con = null;
    try {
      Class.forName(dbDriverMap.get(dbType));
      con = DriverManager.getConnection(param.getAddress(), param.getUser(), param.getPassword());
    } finally {
      if(con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
