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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterConfig {

  private static final Logger logger = LoggerFactory.getLogger(MasterConfig.class);

  /**
   * 失败重试次数, 默认为 2 次, 即失败后还会运行 2 次
   */
  public static int failRetryCount;

  /**
   * execution flow 队列大小
   */
  public static int executionFlowQueueSize;

  /**
   * executor 向 master 发送心跳检测的间隔
   */
  public static int heartBeatCheckInterval;

  /**
   * 心跳超时后, 发送的间隔时间
   */
  public static int heartBeatTimeoutInterval;

  /**
   * 流任务的检测间隔
   */
  public static int streamingCheckInterval;

  /**
   * 流任务的超时时间, 提交后没执行的超时时间
   */
  public static int streamingTimeoutThreshold;

  /**
   * master 的最小线程数
   */
  public static int masterMinThreads;

  /**
   * master 的最大线程数
   */
  public static int masterMaxThreads;

  /**
   * master 端口
   */
  public static int masterPort;

  private static final String MASTER_MIN_THREADS = "master.min.threads";
  private static final String MASTER_MAX_THREADS = "master.max.threads";
  private static final String MASTER_PORT = "master.port";

  static {
    Configuration conf = null;

    try {
      conf = new PropertiesConfiguration("master.properties");
    } catch (ConfigurationException e) {
      logger.error("Configuration error", e);
    }

    failRetryCount = conf.getInt("masterToWorker.failRetry.count", 2);
    executionFlowQueueSize = conf.getInt("masterToWorker.executionFlow.queueSize", 10000);
    heartBeatTimeoutInterval = conf.getInt("master.heartbeat.timeout.interval", 60) * 1000;
    heartBeatCheckInterval = conf.getInt("master.heartbeat.check.interval", 30);
    streamingCheckInterval = conf.getInt("streaming.check.interval", 30);
    streamingTimeoutThreshold = conf.getInt("streaming.timeout.threshold", 1800);
    masterMinThreads = conf.getInt(MASTER_MIN_THREADS, 50);
    masterMaxThreads = conf.getInt(MASTER_MAX_THREADS, 200);
    masterPort = conf.getInt(MASTER_PORT, 9999);
  }
}
