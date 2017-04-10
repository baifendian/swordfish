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
package com.baifendian.swordfish.masterserver.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Master 配置信息 <p>
 */
public class MasterConfig {
  /**
   * 失败重试次数,默认为 2 次
   */
  public static int failRetryCount;

  /**
   * 失败重试的队列大小,默认为 10000
   */
  public static int failRetryQueueSize;

  public static int heartBeatCheckInterval;

  public static int heartBeatTimeoutInterval;

  public static int masterMinThreads;

  public static int masterMaxThreads;

  public static int masterPort;

  private static final String MASTER_MIN_THREADS = "master.min.threads";
  private static final String MASTER_MAX_THREADS = "master.max.threads";
  private static final String MASTER_PORT = "master.port";

  static {
    Configuration conf = null;

    try {
      conf = new PropertiesConfiguration("master.properties");
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }

    failRetryCount = conf.getInt("masterToWorker.failRetry.count", 2);
    failRetryQueueSize = conf.getInt("masterToWorker.failRetry.queueSize", 10000);
    heartBeatTimeoutInterval = conf.getInt("master.heartbeat.timeout.interval", 60) * 1000;
    heartBeatCheckInterval = conf.getInt("master.heartbeat.check.interval", 30);
    masterMinThreads = conf.getInt(MASTER_MIN_THREADS, 50);
    masterMaxThreads = conf.getInt(MASTER_MAX_THREADS, 200);
    masterPort = conf.getInt(MASTER_PORT, 9999);
  }
}
