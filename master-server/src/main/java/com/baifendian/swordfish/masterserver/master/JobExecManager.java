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

import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.StreamingDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.StreamingResult;
import com.baifendian.swordfish.masterserver.config.MasterConfig;
import com.baifendian.swordfish.masterserver.exception.ExecException;
import com.baifendian.swordfish.masterserver.exception.MasterException;
import com.baifendian.swordfish.masterserver.exec.ExecutorClient;
import com.baifendian.swordfish.masterserver.exec.ExecutorServerInfo;
import com.baifendian.swordfish.masterserver.exec.ExecutorServerManager;
import com.baifendian.swordfish.masterserver.quartz.FlowScheduleJob;
import com.baifendian.swordfish.masterserver.utils.ResultHelper;
import com.baifendian.swordfish.rpc.HeartBeatData;
import com.baifendian.swordfish.rpc.RetInfo;
import org.apache.thrift.TException;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 任务执行管理器
 */
public class JobExecManager {

  private static final Logger logger = LoggerFactory.getLogger(JobExecManager.class);

  /**
   * 执行 server 管理器
   */
  private ExecutorServerManager executorServerManager;

  /**
   * flow 执行的管理器
   */
  private FlowExecManager flowExecManager;

  /**
   * 工作流执行队列
   */
  private final BlockingQueue<ExecFlowInfo> executionFlowQueue;

  /**
   * flow 的数据库接口
   */
  private final FlowDao flowDao;

  /**
   * 流任务的数据库接口
   */
  private final StreamingDao streamingDao;

  /**
   * 执行具体的任务的线程, 会从任务队列获取任务, 然后调用 executor 执行
   */
  private Submit2ExecutorServerThread flowSubmit2ExecutorThread;

  /**
   * executor server 服务检查线程
   **/
  private ExecutorCheckThread executorCheckThread;

  /**
   * streaming 任务检查线程
   */
  private StreamingCheckThread streamingCheckThread;

  /**
   * 具备定时调度运行的 service, 当前检测的是 exec-service 服务, 也可以检测其它的服务
   */
  private ScheduledExecutorService checkService;

  public JobExecManager() {
    flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    streamingDao = DaoFactory.getDaoInstance(StreamingDao.class);

    executorServerManager = new ExecutorServerManager();
    executionFlowQueue = new LinkedBlockingQueue<>(MasterConfig.executionFlowQueueSize);
    checkService = Executors.newScheduledThreadPool(5);
  }

  /**
   * 运行 flow exec
   */
  public void run() {
    flowExecManager = new FlowExecManager(this, flowDao);

    // 初始化调度作业
    FlowScheduleJob.init(executionFlowQueue, flowDao);

    // 启动请求 executor server 的处理线程
    flowSubmit2ExecutorThread = new Submit2ExecutorServerThread(executorServerManager, flowDao, executionFlowQueue);
    flowSubmit2ExecutorThread.setDaemon(true);
    flowSubmit2ExecutorThread.start();

    // 检测 executor 的线程
    executorCheckThread = new ExecutorCheckThread(executorServerManager, MasterConfig.heartBeatTimeoutInterval,
        executionFlowQueue, flowDao);

    // 固定执行 executor 的检测
    checkService.scheduleAtFixedRate(executorCheckThread, 10, MasterConfig.heartBeatCheckInterval, TimeUnit.SECONDS);

    // 查看流任务的状态
    streamingCheckThread = new StreamingCheckThread(streamingDao);

    // 固定执行 executor 的检测
    checkService.scheduleAtFixedRate(streamingCheckThread, 10, MasterConfig.streamingCheckInterval, TimeUnit.SECONDS);

    recoveryExecFlow();
  }

  /**
   * 停止 executor 的执行
   */
  public void stop() {
    if (!checkService.isShutdown()) {
      checkService.shutdownNow();
    }

    flowSubmit2ExecutorThread.disable();

    try {
      flowSubmit2ExecutorThread.interrupt();
      flowSubmit2ExecutorThread.join();
    } catch (InterruptedException e) {
      logger.error("join thread exception", e);
    }

    flowExecManager.destroy();
  }

  /**
   * 从调度信息表中恢复在运行的 workflow 信息
   */
  private void recoveryExecFlow() {
    List<ExecutionFlow> executionFlowList = flowDao.queryAllNoFinishFlow();
    Map<String, ExecutorServerInfo> executorServerInfoMap = new HashMap<>();

    if (executionFlowList != null) {
      for (ExecutionFlow executionFlow : executionFlowList) {
        String worker = executionFlow.getWorker();
        if (worker != null && worker.contains(":")) {
          logger.info("recovery execId:{}, executor server:{}", executionFlow.getId(), executionFlow.getWorker());

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
          // 没有 worker 信息，提交到 executionFlowQueue 队列
          logger.info("no worker info, add execution flow[execId:{}] to queue", executionFlow.getId());

          ExecFlowInfo execFlowInfo = new ExecFlowInfo();
          execFlowInfo.setExecId(executionFlow.getId());

          executionFlowQueue.add(execFlowInfo);
        }
      }

      executorServerManager.initServers(executorServerInfoMap.values());
    }
  }

  /**
   * 添加执行的工作流
   *
   * @param execFlowInfo
   */
  public void addExecFlow(ExecFlowInfo execFlowInfo) {
    executionFlowQueue.add(execFlowInfo);
  }

  /**
   * 提交补数据任务
   *
   * @param flow
   * @param cron
   * @param startDateTime
   * @param endDateTime
   */
  public void submitAddData(ProjectFlow flow, CronExpression cron, Date startDateTime, Date endDateTime) {
    flowExecManager.submitAddData(flow, cron, startDateTime, endDateTime);
  }

  /**
   * 执行即席查询
   *
   * @param id
   * @throws TException
   */
  public void execAdHoc(int id) throws TException {
    ExecutorServerInfo executorServerInfo = executorServerManager.getExecutorServer();

    if (executorServerInfo == null) {
      throw new ExecException("can't found active executor server");
    }

    logger.info("exec ad hoc {} on server {}:{}", id, executorServerInfo.getHost(), executorServerInfo.getPort());

    ExecutorClient executorClient = new ExecutorClient(executorServerInfo);
    executorClient.execAdHoc(id);
  }

  /**
   * 执行流任务
   *
   * @param execId
   * @throws TException
   */
  public RetInfo execStreamingJob(int execId) throws TException {

    ExecutorServerInfo executorServerInfo = executorServerManager.getExecutorServer();

    if (executorServerInfo == null) {
      throw new ExecException("can't found active executor server");
    }

    StreamingResult streamingResult = streamingDao.queryStreamingExec(execId);

    if (streamingResult == null) {
      logger.error("streaming exec id {} not exists", execId);
      return ResultHelper.createErrorResult("streaming exec id not exists");
    }

    // 接收到了, 会更新状态, 在依赖资源中
    if (streamingResult.getStatus().typeIsFinished()) {
      logger.error("streaming exec id {} finished unexpected", execId);
      return ResultHelper.createErrorResult("task finished unexpected");
    }

    streamingResult.setStatus(FlowStatus.WAITING_RES);
    streamingResult.setWorker(String.format("%s:%s", executorServerInfo.getHost(), executorServerInfo.getPort()));

    streamingDao.updateResult(streamingResult);

    logger.info("exec streaming job {} on server {}:{}", execId, executorServerInfo.getHost(), executorServerInfo.getPort());

    ExecutorClient executionClient = new ExecutorClient(executorServerInfo.getHost(), executorServerInfo.getPort());

    return executionClient.execStreamingJob(execId);
  }

  /**
   * 取消流任务的执行
   *
   * @param execId
   * @return
   * @throws TException
   */
  public RetInfo cancelStreamingJob(int execId) throws TException {
    StreamingResult streamingResult = streamingDao.queryStreamingExec(execId);

    if (streamingResult == null) {
      throw new MasterException("streaming exec id is not exists");
    }

    String worker = streamingResult.getWorker();
    if (worker == null) {
      throw new MasterException("worker is not exists");
    }

    String[] workerInfo = worker.split(":");
    if (workerInfo.length < 2) {
      throw new MasterException("worker is not validate format " + worker);
    }

    logger.info("cancel exec streaming {} on worker {}", execId, worker);

    ExecutorClient executionClient = new ExecutorClient(workerInfo[0], Integer.valueOf(workerInfo[1]));
    return executionClient.cancelStreamingJob(execId);
  }

  /**
   * 注册 executor
   *
   * @param host
   * @param port
   * @param registerTime
   */
  public void registerExecutor(String host, int port, long registerTime) {
    logger.info("register executor server[{}:{}]", host, port);

    // 时钟差异检查
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

    executorServerManager.addServer(executorServerInfo);
  }

  /**
   * 报告 executor server 信息
   *
   * @param host
   * @param port
   * @param heartBeatData
   */
  public void executorReport(String host, int port, HeartBeatData heartBeatData) {
    logger.debug("executor server[{}:{}] report info {}", host, port, heartBeatData);

    ExecutorServerInfo executorServerInfo = new ExecutorServerInfo();
    executorServerInfo.setHost(host);
    executorServerInfo.setPort(port);
    executorServerInfo.setHeartBeatData(heartBeatData);

    executorServerManager.updateServer(executorServerInfo);
  }

  /**
   * 取消工作流执行
   *
   * @param execId
   * @return
   * @throws TException
   */
  public RetInfo cancelExecFlow(int execId) throws TException {
    ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
    if (executionFlow == null) {
      throw new MasterException("workflow exec id is not exists");
    }

    String worker = executionFlow.getWorker();
    if (worker == null) {
      throw new MasterException("worker is not exists");
    }

    String[] workerInfo = worker.split(":");
    if (workerInfo.length < 2) {
      throw new MasterException("worker is not validate format " + worker);
    }

    logger.info("cancel exec flow {} on worker {}", execId, worker);

    ExecutorClient executionClient = new ExecutorClient(workerInfo[0], Integer.valueOf(workerInfo[1]));
    return executionClient.cancelExecFlow(execId);
  }
}
