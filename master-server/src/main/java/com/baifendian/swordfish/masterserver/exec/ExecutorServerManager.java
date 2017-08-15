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
package com.baifendian.swordfish.masterserver.exec;

import com.baifendian.swordfish.masterserver.exception.MasterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 管理 executor server, 包括添加, 更新, 删除等功能
 */
public class ExecutorServerManager {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private Map<String, ExecutorServerInfo> executorServers = new ConcurrentHashMap<>();

  /**
   * 添加一个 executor server
   */
  public synchronized ExecutorServerInfo addServer(ExecutorServerInfo executorServerInfo)
      throws MasterException {
    String key = getKey(executorServerInfo);

    // 如果以前没有, 记录一条信息
    if (executorServers.containsKey(key)) {
      logger.error("executor is register already: {}", executorServerInfo);

      throw new MasterException("executor is register already: " + executorServerInfo);
    }

    return executorServers.put(key, executorServerInfo);
  }

  /**
   * 删除具体的 executor server
   */
  public synchronized ExecutorServerInfo updateServer(ExecutorServerInfo executorServerInfo)
      throws MasterException {
    String key = getKey(executorServerInfo);

    // 如果以前没有, 记录一条信息
    if (!executorServers.containsKey(key)) {
      logger.warn("executor is not register, maybe master restart but executor not timeout: {}",
          executorServerInfo);
    }

    // 这里会进行重新注册
    return executorServers.put(key, executorServerInfo);
  }

  /**
   * executor server 是否已经存在
   */
  public synchronized boolean containServer(ExecutorServerInfo executorServerInfo)
      throws MasterException {
    String key = getKey(executorServerInfo);

    if (executorServers.containsKey(key)) {
      return true;
    }

    return false;
  }

  /**
   * 获取一个可用的 executor server, 选取执行的 workflow 最少的那个 executor server
   */
  public synchronized ExecutorServerInfo getExecutorServer() {
    logger.debug("executor servers: {}", executorServers.toString());

    ExecutorServerInfo result = null;

    int size = executorServers.size();

    if (size <= 0) {
      return result;
    }

    int choose = Math.abs(new Random().nextInt()) % size;
    int index = 0;

    logger.info("executor servers size: {}, choose: {}", size, choose);

    for (ExecutorServerInfo executorServerInfo : executorServers.values()) {
      if (index == choose) {
        result = executorServerInfo;
        break;
      }

      ++index;
    }

    return result;
  }

  /**
   * 得到指定的 executor
   */
  public synchronized ExecutorServerInfo getExecutorServer(String host, int port) {
    logger.debug("executor servers: {}", executorServers.toString());

    if (StringUtils.isEmpty(host)) {
      return getExecutorServer();
    }

    String key = host + ":" + port;

    if (executorServers.containsKey(key)) {
      return executorServers.get(key);
    }

    return getExecutorServer();
  }

  /**
   * 检测超时的 executor 并返回
   */
  public synchronized List<ExecutorServerInfo> checkTimeoutServer(long timeoutInterval) {
    List<ExecutorServerInfo> faultServers = null;

    for (Map.Entry<String, ExecutorServerInfo> entry : executorServers.entrySet()) {
      logger.debug("{} {}", entry.getKey(), entry.getValue().getHeartBeatData());

      long nowTime = System.currentTimeMillis();
      long diff = nowTime - entry.getValue().getHeartBeatData().getReceiveDate();

      if (diff > timeoutInterval) {
        logger
            .warn(
                "executor server time out: {}, now time: {}, diff time: {}, timeout interval: {}",
                entry.getValue(), nowTime, diff, timeoutInterval);

        if (faultServers == null) {
          faultServers = new ArrayList<>();
        }

        faultServers.add(entry.getValue());
      }
    }

    return faultServers;
  }

  /**
   * @param executorServerInfo
   * @return
   */
  public synchronized ExecutorServerInfo removeServer(ExecutorServerInfo executorServerInfo) {
    String key = getKey(executorServerInfo);

    return executorServers.remove(key);
  }

  /**
   * 初始化 executor server 信息
   */
  public synchronized void initServers(Collection<ExecutorServerInfo> executorServerInfos) {
    for (ExecutorServerInfo executorServerInfo : executorServerInfos) {
      executorServers.put(getKey(executorServerInfo), executorServerInfo);
    }
  }

  /**
   * 获取 key 信息
   */
  public String getKey(ExecutorServerInfo executorServerInfo) {
    if (executorServerInfo == null) {
      return StringUtils.EMPTY;
    }

    return executorServerInfo.getHost() + ":" + executorServerInfo.getPort();
  }

  /**
   * 打印 server 信息
   */
  public synchronized void printServerInfo() {
    for (Map.Entry<String, ExecutorServerInfo> entry : executorServers.entrySet()) {
      ExecutorServerInfo executorServerInfo = entry.getValue();

      logger.debug("executor information, host: {}, port: {}, heart beat: {}",
          executorServerInfo.getHost(), executorServerInfo.getPort(),
          executorServerInfo.getHeartBeatData());
    }
  }
}
