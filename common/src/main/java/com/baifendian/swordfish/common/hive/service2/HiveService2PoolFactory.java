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
package com.baifendian.swordfish.common.hive.service2;

import java.sql.SQLException;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.hive.jdbc.HiveConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveService2PoolFactory extends BaseKeyedPoolableObjectFactory {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 生成对象
   *
   * @param object
   * @return
   * @throws Exception
   */
  @Override
  public HiveConnection makeObject(Object object) throws Exception {
    // 生成 client 对象
    if (object != null) {
      HiveService2ConnectionInfo hiveService2ConnectionInfo = (HiveService2ConnectionInfo) object;
      Properties info = new Properties();

      if (StringUtils.isNotEmpty(hiveService2ConnectionInfo.getUser())) {
        info.put("user", hiveService2ConnectionInfo.getUser());
      }

      if (StringUtils.isNotEmpty(hiveService2ConnectionInfo.getPassword())) {
        info.put("password", hiveService2ConnectionInfo.getPassword());
      }

      try {
        HiveConnection connection = new HiveConnection(hiveService2ConnectionInfo.getUri(), info);
        return connection;
      } catch (SQLException e) {
        logger.error("Connection hive exception", e);
      }
    }

    return null;
  }

  /**
   * 销毁对象
   *
   * @param key
   * @param obj
   * @throws Exception
   */
  @Override
  public void destroyObject(Object key, Object obj) throws Exception {
    HiveConnection hiveConnection = (HiveConnection) obj;
    hiveConnection.close();
  }

  /**
   * 校验对象
   *
   * @param key
   * @param obj
   * @return
   */
  @Override
  public boolean validateObject(Object key, Object obj) {
    HiveConnection hiveConnection = (HiveConnection) obj;

    try {
      boolean isClosed = hiveConnection.isClosed();
      return !isClosed;
    } catch (SQLException e) {
      logger.error("hive close exception", e);
    }

    return false;
  }
}
