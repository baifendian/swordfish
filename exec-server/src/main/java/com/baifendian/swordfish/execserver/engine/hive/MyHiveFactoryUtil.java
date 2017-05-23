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
package com.baifendian.swordfish.execserver.engine.hive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 初始化hiveConf <p>
 */

public class MyHiveFactoryUtil {

  private static final Logger logger = LoggerFactory.getLogger(MyHiveFactoryUtil.class);

  private static final Properties properties = new Properties();

  private static HiveConfig hiveConfig;

  static {
    try {
      File dataSourceFile = ResourceUtils.getFile("classpath:common/hive/hive.properties");
      InputStream is = new FileInputStream(dataSourceFile);
      properties.load(is);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

  /**
   * 构建 hive 配置
   *
   * @param hiveConfig
   */
  public static void buildHiveConfig(HiveConfig hiveConfig) {
    hiveConfig.setMetastoreUris(properties.getProperty("hive.metastore.uris"));
    hiveConfig.setThriftUris(properties.getProperty("hive.thrift.uris"));
  }

  /**
   * 得到 hive 配置实例, 单例模式
   *
   * @return
   */
  public static HiveConfig getInstance() {
    if (hiveConfig == null) {
      synchronized (MyHiveFactoryUtil.class) {
        if (hiveConfig == null) {
          HiveConfig hiveConfigTemp = new HiveConfig();
          buildHiveConfig(hiveConfigTemp);
          hiveConfig = hiveConfigTemp;
        }
      }
    }

    return hiveConfig;
  }
}