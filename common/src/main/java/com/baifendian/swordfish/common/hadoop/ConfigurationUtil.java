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
package com.baifendian.swordfish.common.hadoop;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * 配置实例获取工具 <p>
 */
public class ConfigurationUtil {
  /**
   * logger
   */
  private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtil.class);

  /**
   * {@link Configuration}
   */
  private static volatile Configuration configuration;

  /**
   * {@link Properties}
   */
  private static final Properties PROPERTIES = new Properties();

  private static PropertiesConfiguration STORM_PROPERTIES;

  static {
    InputStream is = null;
    try {
      File dataSourceFile = ResourceUtils.getFile("classpath:common/hadoop/hadoop.properties");
      STORM_PROPERTIES = new PropertiesConfiguration("common/storm.properties");
      is = new FileInputStream(dataSourceFile);
      PROPERTIES.load(is);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } catch (ConfigurationException e) {
      logger.error(e.getMessage(), e);
      System.exit(-1);
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  /**
   * 获取 Configuration <p>
   *
   * @return {@link Configuration}
   */
  public static Configuration getConfiguration() {
    init();
    return configuration;
  }

  /**
   * 获取 web app 地址
   *
   * @return
   */
  public static String getWebappAddress(String appId) {
    init();
    return String.format(configuration.get("yarn.resourcemanager.webapp.address"), appId);
  }

  /**
   * 获取 Storm web app 地址
   * @param appId
   * @return
   */
  public static String getStormAppAddress(String appId) {
    init();
    return MessageFormat.format("{0}/{1}{2}", configuration.get("storm.rest.url"), configuration.get("Storm.rest.topolog"), appId);
  }


  /**
   * 根据 appid 生成一个 url
   *
   * @param appId
   * @return
   */
  public static String getApplicationStatusAddress(String appId) {
    init();
    return String.format(configuration.get("yarn.application.status.address"), appId);
  }

  /**
   * 初始化配置
   */
  private static void init() {
    if (configuration == null) {
      synchronized (ConfigurationUtil.class) {
        if (configuration == null) {
          configuration = new Configuration();
          initConfiguration();
        }
      }
    }
  }

  /**
   * 初始化配置 <p>
   */
  private static void initConfiguration() {
    configuration.setBoolean("mapreduce.app-submission.cross-platform", Boolean.parseBoolean(PROPERTIES.getProperty("mapreduce.app-submission.cross-platform")));
    configuration.set("fs.defaultFS", PROPERTIES.getProperty("fs.defaultFS"));
    configuration.set("mapreduce.framework.name", PROPERTIES.getProperty("mapreduce.framework.name"));
    configuration.set("yarn.resourcemanager.address", PROPERTIES.getProperty("yarn.resourcemanager.address"));
    configuration.set("yarn.resourcemanager.scheduler.address", PROPERTIES.getProperty("yarn.resourcemanager.scheduler.address"));
    configuration.set("mapreduce.jobhistory.address", PROPERTIES.getProperty("mapreduce.jobhistory.address"));
    configuration.set("yarn.resourcemanager.webapp.address", PROPERTIES.getProperty("yarn.resourcemanager.webapp.address"));
    configuration.set("yarn.application.status.address", PROPERTIES.getProperty("yarn.application.status.address"));
    configuration.set("storm.rest.url", STORM_PROPERTIES.getString("storm.rest.url"));
    configuration.set("Storm.rest.topology", STORM_PROPERTIES.getString("Storm.rest.topology"));
  }
}
