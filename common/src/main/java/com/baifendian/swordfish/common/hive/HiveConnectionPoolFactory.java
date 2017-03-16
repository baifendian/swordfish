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
package com.baifendian.swordfish.common.hive;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.hive.jdbc.HiveConnection;

import java.sql.SQLException;

/**
 * Created by wenting on 9/8/16.
 */
public class HiveConnectionPoolFactory extends BaseKeyedPoolableObjectFactory {
  /**
   * 生成对象
   */
  @Override
  public HiveConnection makeObject(Object object) throws Exception {
    // 生成client对象
    if (object != null) {
      ConnectionInfo connectionInfo = (ConnectionInfo) object;
      java.util.Properties info = new java.util.Properties();
      if (connectionInfo.getUser() != null) {
        info.put("user", connectionInfo.getUser());
      }
      if (connectionInfo.getPassword() != null) {
        info.put("password", connectionInfo.getPassword());
      }
      try {
        HiveConnection connection = new HiveConnection(connectionInfo.getUri(), info);
        return connection;
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * 销毁对象
   */
  @Override
  public void destroyObject(Object key, Object obj) throws Exception {
    HiveConnection hiveConnection = (HiveConnection) obj;
    ((HiveConnection) obj).close();
  }

  @Override
  public boolean validateObject(Object key, Object obj) {
    HiveConnection hiveConnection = (HiveConnection) obj;
    try {
      boolean isClosed = hiveConnection.isClosed();
      return !isClosed;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

}
