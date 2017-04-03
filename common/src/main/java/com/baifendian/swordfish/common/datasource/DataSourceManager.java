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
package com.baifendian.swordfish.common.datasource;

import com.baifendian.swordfish.common.datasource.ftp.FtpHandler;
import com.baifendian.swordfish.common.datasource.hbase.HBaseHandler;
import com.baifendian.swordfish.common.datasource.jdbc.JDBCHandler;
import com.baifendian.swordfish.common.datasource.mongodb.MongoDBHandler;
import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.dao.enums.DbType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class DataSourceManager {

  private static Map<DbType, Class<? extends DataSourceHandler>> dataSourceHandlerMap = new HashMap<>();

  static{
    dataSourceHandlerMap.put(DbType.MYSQL, JDBCHandler.class);
    dataSourceHandlerMap.put(DbType.ORACLE, JDBCHandler.class);
    dataSourceHandlerMap.put(DbType.HBASE11X, HBaseHandler.class);
    dataSourceHandlerMap.put(DbType.FTP, FtpHandler.class);
    dataSourceHandlerMap.put(DbType.MONOGODB, MongoDBHandler.class);
  }

  public static DataSourceHandler getHandler(DbType dbType, String paramStr) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    DataSourceHandler dataSourceHandler = null;
    Class<? extends DataSourceHandler> dataSourceHandlerClass = dataSourceHandlerMap.get(dbType);
    if(dataSourceHandlerClass == null){
      throw new ExecException("unsupport datasource type " + dbType.name());
    }
    Constructor<DataSourceHandler> dataSourceHandlerConstructor = (Constructor<DataSourceHandler>) dataSourceHandlerClass.getConstructor(DbType.class, String.class);
    dataSourceHandler = dataSourceHandlerConstructor.newInstance(dbType, paramStr);
    return dataSourceHandler;
  }
}
