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
package com.baifendian.swordfish.common.job.struct.datasource.conn;

import com.baifendian.swordfish.common.job.struct.datasource.MysqlParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * mysql连接测试类
 */
public class MysqlTryConn extends TryConn<MysqlParam> {

  private static Logger logger = LoggerFactory.getLogger(MysqlTryConn.class.getName());

  public MysqlTryConn(MysqlParam param) {
    super(param);
  }

  @Override
  public void isConnectable() throws Exception {
    Connection con = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");

      String address = param.getAddress();
      String database = param.getDatabase();
      if (address.lastIndexOf("/") != address.length()) {
        database += "/";
      }
      String url = param.getAddress() + database;

      con = DriverManager.getConnection(url, param.getUser(), param.getPassword());
    }finally {
      if (con!=null){
        try {
          con.close();
        } catch (SQLException e) {
          logger.error("Mysql datasource try conn close conn error",e);
          throw e;
        }
      }
    }
  }
}
