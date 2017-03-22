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
package com.baifendian.swordfish.webserver;

import com.baifendian.swordfish.webserver.exception.MasterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExecutorServerManager {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private Map<String, ExecutorServerInfo> executorServers = new ConcurrentHashMap<>();

  public synchronized ExecutorServerInfo addServer(String key, ExecutorServerInfo executorServerInfo) throws MasterException {
    if (executorServers.containsKey(key)) {
      throw new MasterException("executor is register");
    }
    return executorServers.put(key, executorServerInfo);
  }

  public synchronized ExecutorServerInfo updateServer(String key, ExecutorServerInfo executorServerInfo) throws MasterException {
    if (!executorServers.containsKey(key)) {
      throw new MasterException("executor is not register");
    }
    return executorServers.put(key, executorServerInfo);
  }

  /**
   * 获取一个可用的executor server, 选取执行的workflow最少的那个excutorserver
   */
  public synchronized ExecutorServerInfo getExecutorServer() {
    logger.debug("executor servers:{}", executorServers.toString());
    ExecutorServerInfo result = null;
    for (ExecutorServerInfo executorServerInfo : executorServers.values()) {
      if (executorServerInfo.getHeartBeatData() == null) {
        continue;
      }
      if (result == null) {
        result = executorServerInfo;
      } else {
        if (result.getHeartBeatData().getExecIdsSize() > executorServerInfo.getHeartBeatData().getExecIdsSize()) {
          result = executorServerInfo;
        }
      }
    }
    return result;
  }

  public synchronized List<ExecutorServerInfo> checkTimeoutServer(long timeoutInterval) {
    List<ExecutorServerInfo> faultServers = new ArrayList<>();
    logger.debug("{} ", executorServers);
    for (Map.Entry<String, ExecutorServerInfo> entry : executorServers.entrySet()) {
      logger.debug("{} {}", entry.getKey(), entry.getValue().getHeartBeatData());
      long nowTime = System.currentTimeMillis();
      long diff = nowTime - entry.getValue().getHeartBeatData().getReportDate();
      if (diff > timeoutInterval) {
        logger.warn("executor server time out {}", entry.getKey());
        executorServers.remove(entry.getKey());
        faultServers.add(entry.getValue());
      }
    }
    return faultServers;
  }

  public synchronized ExecutorServerInfo removeServer(ExecutorServerInfo executorServerInfo) {
    String key = executorServerInfo.getHost() + ":" + executorServerInfo.getPort();
    return executorServers.remove(key);
  }

  public synchronized void initServers(Map<String, ExecutorServerInfo> executorServerInfoMap) {
    for (Map.Entry<String, ExecutorServerInfo> entry : executorServerInfoMap.entrySet()) {
      executorServers.put(entry.getKey(), entry.getValue());
    }
  }

}
