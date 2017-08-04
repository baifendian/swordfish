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
package com.baifendian.swordfish.execserver.utils;

public class Constants {

  public static final String DATETIME_FORMAT = "yyyyMMddHHmmss";

  public static final String EXECUTOR_PORT = "executor.port";

  public static final String EXECUTOR_HEARTBEAT_INTERVAL = "executor.heartbeat.interval";

  public static final String EXECUTOR_ADHOCRUNNER_THREADS = "executor.adhocrunner.threads";

  public static final String EXECUTOR_FLOWRUNNER_THREADS = "executor.flowrunner.threads";

  public static final String EXECUTOR_NODERUNNER_THREADS = "executor.noderunner.threads";

  public static final String EXECUTOR_STREAMING_THREADS = "executor.streaming.threads";

  public static final int ADHOC_TIMEOUT = 3600 * 6;

  /**
   * 默认的最大重试次数为 0 次
   */
  public static final int defaultMaxTryTimes = 0;

  /**
   * 默认的最大超时时间是 10 小时
   */
  public static final int defaultMaxTimeout = 10 * 3600;

  /**
   * 连接 master 的最大重试次数
   */
  public static final int defaultThriftRpcRetrites = 3;

  /**
   * 心跳默认间隔
   */
  public static final int defaultExecutorHeartbeatInterval = 60;

  /**
   * 心跳线程数
   */
  public static final int defaultExecutorHeartbeatThreadNum = 5;

  /**
   * server 最小线程处理数
   */
  public static final int defaultServerMinNum = 50;

  /**
   * server 最大线程处理数
   */
  public static final int defaultServerMaxNum = 200;

  /**
   * flow runner 线程数
   */
  public static final int defaultFlowRunnerThreadNum = 20;

  /**
   * node runner 线程数
   */
  public static final int defaultNodeRunnerThreadNum = 100;

  /**
   * 日志累计到一定数目才进行输出
   */
  public static final int defaultLogBufferSize = 4 * 16;

  /**
   * 日志刷新周期, 越快越精确
   */
  public static final int defaultLogFlushInterval = 1000;

  /**
   * 默认的清理 flow 数
   */
  public static final int defaultCleanFinishFlowInterval = 10000;

  /**
   * 默认的 adhoc executor 数目
   */
  public static final int defaultAdhocExecutorNum = 20;

  /**
   * 默认的 streaming thread 数目
   */
  public static final int defaultStreamingThreadNum = 10;
}
