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
package com.baifendian.swordfish.masterserver.master;

import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.masterserver.exec.ExecutorServerInfo;
import com.baifendian.swordfish.masterserver.exec.ExecutorServerManager;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * executor server 容错处理服务线程
 */
public class ExecutorCheckThread implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(ExecutorCheckThread.class);

  // 管理 executor 的 server
  private ExecutorServerManager executorServerManager;

  // 执行线程
  private final Submit2ExecutorServerThread submit2ExecutorServerThread;

  // 心跳超时时间
  private int timeoutInterval;

  // 工作流数据库接口
  private FlowDao flowDao;

  public ExecutorCheckThread(ExecutorServerManager executorServerManager, int timeoutInterval,
      Submit2ExecutorServerThread submit2ExecutorServerThread, FlowDao flowDao) {
    this.executorServerManager = executorServerManager;
    this.submit2ExecutorServerThread = submit2ExecutorServerThread;
    this.timeoutInterval = timeoutInterval;
    this.flowDao = flowDao;
  }

  @Override
  public void run() {
    try {
      // 展示当前的 worker 列表信息
      executorServerManager.printServerInfo();

      // 得到超时的工作流列表
      List<ExecutorServerInfo> faultServers = executorServerManager
          .checkTimeoutServer(timeoutInterval);

      if (CollectionUtils.isEmpty(faultServers)) {
        return;
      }

      // 超时重新提交
      for (ExecutorServerInfo executorServerInfo : faultServers) {
        logger.error("get fault servers:{}", executorServerInfo);

        executorServerManager.removeServer(executorServerInfo);

        // 重新提交上面的任务
        submit2ExecutorServerThread.resubmitExecFlow(executorServerInfo);
      }
    } catch (Exception e) {
      logger.error("check thread get error", e);
    }
  }
}
