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
package com.baifendian.swordfish.execserver.engine.phoenix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.phoenix.queryserver.client.ThinClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * create connect<p>
 *
 */
public class PhoenixUtil {

  private static final Logger logger = LoggerFactory.getLogger(PhoenixUtil.class);

  /**
   * phoenix Host
   */
  static private String phoenixHost;
  /**
   * phoenix port
   */
  static private int port;

  static {
    try {
      Class.forName("org.apache.phoenix.queryserver.client.Driver");
    } catch (ClassNotFoundException e) {
      logger.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }

    try {
      Properties properties = new Properties();

      File dataSourceFile = ResourceUtils.getFile("classpath:common/phoenix.properties");
      InputStream is = new FileInputStream(dataSourceFile);
      properties.load(is);

      phoenixHost = properties.getProperty("phoenix.host");
      port = Integer.parseInt(properties.getProperty("phoenix.port"));
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

  public static Connection getPhoenixConnection(String userName, long timeout) {
    Properties phoenixConfPro = new Properties();
    phoenixConfPro.put("phoenix.functions.allowUserDefinedFunctions", "true");
    phoenixConfPro.put("phoenix.query.timeoutMs", timeout*1000L);
    phoenixConfPro.put("user", userName);

    try {
      Connection connection = DriverManager
          .getConnection(ThinClientUtil.getConnectionUrl( phoenixHost, port), phoenixConfPro);

      return connection;
    } catch (RuntimeException e) {
      if (e.getCause().getClass().getName().equals("java.net.ConnectException")) {
        throw new RuntimeException("Failed to connect phoenix");
      }
      logger.error("url:" + ThinClientUtil.getConnectionUrl(phoenixHost, port), e);
    } catch (SQLException e) {
      logger.error(e.getMessage(), e);
    }

    return null;
  }
}
