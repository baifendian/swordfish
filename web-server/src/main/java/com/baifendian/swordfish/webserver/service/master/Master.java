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
package com.baifendian.swordfish.webserver.service.master;

import com.baifendian.swordfish.common.job.exception.ExecException;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.MasterDao;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.rpc.HeartBeatData;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.webserver.ExecutorClient;
import com.baifendian.swordfish.webserver.ExecutorServerInfo;
import com.baifendian.swordfish.webserver.ExecutorServerManager;
import com.baifendian.swordfish.webserver.config.MasterConfig;
import com.baifendian.swordfish.webserver.exception.MasterException;
import com.baifendian.swordfish.webserver.quartz.FlowScheduleJob;
import com.baifendian.swordfish.webserver.utils.ResultHelper;

import org.apache.derby.iapi.sql.execute.ExecutionContext;
import org.apache.thrift.TException;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Master {

  private static final Logger logger = LoggerFactory.getLogger(Master.class);

  private ExecutorServerManager executorServerManager;

  private FlowExecManager flowExecManager;

  /**
   * workflow 执行队列
   */
  private final BlockingQueue<ExecFlowInfo> executionFlowQueue;
  private final FlowDao flowDao;

  /**
   * {@link Submit2ExecutorServerThread}
   */
  private Submit2ExecutorServerThread retryToWorkerThread;

  /**
   * executor server服务检查线程
   **/
  private ExecutorCheckThread executorCheckThread;

  private ScheduledExecutorService executorService;

  public Master(FlowDao flowDao){
    this.flowDao = flowDao;
    executorServerManager = new ExecutorServerManager();
    executorService = Executors.newScheduledThreadPool(5);
    executionFlowQueue = new LinkedBlockingQueue<>(MasterConfig.failRetryQueueSize);
  }

  public void run(){
    flowExecManager = new FlowExecManager(this, flowDao);
    // 初始化调度作业
    FlowScheduleJob.init(executionFlowQueue, flowDao);

    // 启动请求 executor server的处理线程
    retryToWorkerThread = new Submit2ExecutorServerThread(executorServerManager, flowDao, executionFlowQueue);
    retryToWorkerThread.setDaemon(true);
    retryToWorkerThread.start();

    executorCheckThread = new ExecutorCheckThread(executorServerManager, MasterConfig.heartBeatTimeoutInterval,
            executionFlowQueue, flowDao);
    executorService.scheduleAtFixedRate(executorCheckThread, 10, MasterConfig.heartBeatCheckInterval, TimeUnit.SECONDS);

    recoveryExecFlow();
  }

  public void stop(){
    if(!executorService.isShutdown()){
      executorService.shutdownNow();
    }
    flowExecManager.destroy();
  }

  /**
   * 从调度信息表中恢复在运行的workflow信息
   */
  private void recoveryExecFlow() {
    List<ExecutionFlow> executionFlowList = flowDao.queryAllNoFinishFlow();
    Map<String, ExecutorServerInfo> executorServerInfoMap = new HashMap<>();
    if (executionFlowList != null) {
      for (ExecutionFlow executionFlow : executionFlowList) {
        String worker = executionFlow.getWorker();
        if (worker != null && worker.contains(":")) {
          logger.info("recovery execId:{} executor server:{}", executionFlow.getId(), executionFlow.getWorker());
          String[] workerInfo = worker.split(":");
          if (executorServerInfoMap.containsKey(worker)) {
            executorServerInfoMap.get(worker).getHeartBeatData().getExecIds().add(executionFlow.getId());
          } else {
            ExecutorServerInfo executorServerInfo = new ExecutorServerInfo();
            executorServerInfo.setHost(workerInfo[0]);
            executorServerInfo.setPort(Integer.parseInt(workerInfo[1]));
            HeartBeatData heartBeatData = new HeartBeatData();
            heartBeatData.setReportDate(System.currentTimeMillis());
            heartBeatData.setExecIds(new ArrayList<>());
            heartBeatData.getExecIds().add(executionFlow.getId());
            executorServerInfo.setHeartBeatData(heartBeatData);
            executorServerInfoMap.put(worker, executorServerInfo);
          }
        } else {
          // 没有worker信息，提交到executionFlowQueue队列
          logger.info("no worker info, add execution flow[execId:{}] to queue", executionFlow.getId());
          ExecFlowInfo execFlowInfo = new ExecFlowInfo();
          execFlowInfo.setExecId(executionFlow.getId());
          executionFlowQueue.add(execFlowInfo);
        }
      }
      executorServerManager.initServers(executorServerInfoMap);
    }

  }

  public void addExecFlow(ExecFlowInfo execFlowInfo){
    executionFlowQueue.add(execFlowInfo);
  }

  public void submitAddData(ProjectFlow flow, CronExpression cron, Date startDateTime, Date endDateTime) {
    flowExecManager.submitAddData(flow, cron, startDateTime, endDateTime);
  }

  public void execAdHoc(int id) throws TException {
    ExecutorServerInfo executorServerInfo = executorServerManager.getExecutorServer();
    if(executorServerInfo == null){
      throw new ExecException("can't found active executor server");
    }
    logger.info("exec adhoc {} on server {}:{}", id, executorServerInfo.getHost(), executorServerInfo.getPort());
    ExecutorClient executorClient = new ExecutorClient(executorServerInfo);
    executorClient.execAdHoc(id);
  }

  public void registerExecutor(String host, int port, long registerTime) {
    logger.info("register executor server[{}:{}]", host, port);
    String key = String.format("%s:%d", host, port);
    /** 时钟差异检查 **/
    long nowTime = System.currentTimeMillis();
    if (registerTime > nowTime + 10000) {
      throw new MasterException("executor master clock time diff then 10 seconds");
    }

    ExecutorServerInfo executorServerInfo = new ExecutorServerInfo();
    executorServerInfo.setHost(host);
    executorServerInfo.setPort(port);
    HeartBeatData heartBeatData = new HeartBeatData();
    heartBeatData.setReportDate(registerTime);
    executorServerInfo.setHeartBeatData(heartBeatData);

    executorServerManager.addServer(key, executorServerInfo);
  }

  public void executorReport(String host, int port, HeartBeatData heartBeatData){
    logger.debug("executor server[{}:{}] report info {}", host, port, heartBeatData);
    String key = String.format("%s:%d", host, port);

    ExecutorServerInfo executorServerInfo = new ExecutorServerInfo();
    executorServerInfo.setHost(host);
    executorServerInfo.setPort(port);
    executorServerInfo.setHeartBeatData(heartBeatData);

    executorServerManager.updateServer(key, executorServerInfo);
  }

  public RetInfo cancelExecFlow(int execId) throws TException {
    ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
    if(executionFlow == null) {
      throw new MasterException("execId is not exists");
    }
    String worker = executionFlow.getWorker();
    if(worker == null) {
      throw new MasterException("worker is not exists");
    }
    String[] workerInfo = worker.split(":");
    if(workerInfo.length < 2) {
      throw new MasterException("worker is not validate format " + worker);
    }
    logger.info("cancel exec flow {} on worker {}", execId, worker);
    ExecutorClient executionClient = new ExecutorClient(workerInfo[0], Integer.valueOf(workerInfo[1]));
    return executionClient.cancelExecFlow(execId);
  }

}
