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

import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.dao.AdHocDao;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.StreamingDao;
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.enums.NodeDepType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.dao.model.StreamingResult;
import com.baifendian.swordfish.masterserver.config.MasterConfig;
import com.baifendian.swordfish.masterserver.exception.ExecException;
import com.baifendian.swordfish.masterserver.exception.MasterException;
import com.baifendian.swordfish.masterserver.exec.ExecutorClient;
import com.baifendian.swordfish.masterserver.exec.ExecutorServerInfo;
import com.baifendian.swordfish.masterserver.exec.ExecutorServerManager;
import com.baifendian.swordfish.masterserver.quartz.FlowScheduleJob;
import com.baifendian.swordfish.masterserver.quartz.QuartzManager;
import com.baifendian.swordfish.masterserver.utils.ResultDetailHelper;
import com.baifendian.swordfish.masterserver.utils.ResultHelper;
import com.baifendian.swordfish.rpc.ExecInfo;
import com.baifendian.swordfish.rpc.HeartBeatData;
import com.baifendian.swordfish.rpc.MasterService.Iface;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.RetResultInfo;
import com.baifendian.swordfish.rpc.ScheduleInfo;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.thrift.TException;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MasterService 实现 <p>
 */
public class MasterServiceImpl implements Iface {

  /**
   * logger
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 工作流的数据库接口
   */
  private final FlowDao flowDao;

  /**
   * 即席查询的数据库接口
   */
  private final AdHocDao adHocDao;

  /**
   * 流任务的数据库接口
   */
  private final StreamingDao streamingDao;

  /**
   * 执行 server 管理器
   */
  private ExecutorServerManager executorServerManager;

  /**
   * 工作流执行队列
   */
  private final BlockingQueue<ExecFlowInfo> executionFlowQueue;

  /**
   * 具备定时调度运行的 service, 当前检测的是 exec-service 服务, 也可以检测其它的服务
   */
  private ScheduledExecutorService checkService;

  /**
   * 执行具体的任务的线程, 会从任务队列获取任务, 然后调用 executor 执行
   */
  private Submit2ExecutorServerThread flowSubmit2ExecutorThread;

  /**
   * flow 执行的管理器
   */
  private FlowExecManager flowExecManager;

  /**
   * executor server 服务检查线程
   **/
  private ExecutorCheckThread executorCheckThread;

  /**
   * streaming 任务检查线程
   */
  private StreamingCheckThread streamingCheckThread;

  public MasterServiceImpl() {
    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    this.adHocDao = DaoFactory.getDaoInstance(AdHocDao.class);
    this.streamingDao = DaoFactory.getDaoInstance(StreamingDao.class);

    this.executorServerManager = new ExecutorServerManager();
    this.executionFlowQueue = new LinkedBlockingQueue<>(MasterConfig.executionFlowQueueSize);
    this.checkService = Executors.newScheduledThreadPool(5);
  }

  /**
   * 运行 flow exec
   */
  public void run() {
    flowExecManager = new FlowExecManager(this, flowDao);

    // 初始化调度作业
    FlowScheduleJob.init(executionFlowQueue, flowDao);

    // 启动请求 executor server 的处理线程
    flowSubmit2ExecutorThread = new Submit2ExecutorServerThread(executorServerManager, flowDao,
        executionFlowQueue);
    flowSubmit2ExecutorThread.setDaemon(true);
    flowSubmit2ExecutorThread.start();

    // 检测 executor 的线程
    executorCheckThread = new ExecutorCheckThread(executorServerManager,
        MasterConfig.heartBeatTimeoutInterval,
        flowSubmit2ExecutorThread, flowDao);

    // 固定执行 executor 的检测
    checkService.scheduleAtFixedRate(executorCheckThread, 10, MasterConfig.heartBeatCheckInterval,
        TimeUnit.SECONDS);

    // 查看流任务的状态
    streamingCheckThread = new StreamingCheckThread(streamingDao);

    // 固定执行 executor 的检测
    checkService.scheduleAtFixedRate(streamingCheckThread, 10, MasterConfig.streamingCheckInterval,
        TimeUnit.SECONDS);

    // 启动的时候, 等待一定的时间, 然后重新分配未完成的任务
    Thread t = new Thread(() -> {
      try {
        // 这里等待一定的时间之后, 避免有心跳继续发过来
        Thread.sleep(MasterConfig.heartBeatTimeoutInterval + 1000);

        List<ExecutionFlow> executionFlowList = flowDao.queryAllNoFinishFlow();

        logger.info("recovery exec flows: {}", executionFlowList.size());

        for (ExecutionFlow executionFlow : executionFlowList) {
          Pair<String, Integer> pair = CommonUtil.parseWorker(executionFlow.getWorker());

          ExecFlowInfo execFlowInfo = (pair == null) ? new ExecFlowInfo(executionFlow.getId())
              : new ExecFlowInfo(pair.getLeft(), pair.getRight(), executionFlow.getId());

          executionFlowQueue.add(execFlowInfo);
        }
      } catch (Exception e) {
        logger.error("Catch an exception", e);
      }
    });

    t.start();
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
   * 添加任务到队列中
   */
  public void addExecFlow(ExecFlowInfo execFlowInfo) {
    executionFlowQueue.add(execFlowInfo);
  }

  /**
   * 设置调度信息, 最终设置的是 Crontab 表达式(其实是按照 Quartz 的语法)
   *
   * @see CronExpression
   */
  @Override
  public RetInfo setSchedule(int projectId, int flowId) throws TException {
    logger.info("set schedule, project id: {}, flow id: {}", projectId, flowId);

    try {
      Schedule schedule = flowDao.querySchedule(flowId);
      if (schedule == null) {
        return ResultHelper.createErrorResult("flow schedule info not exists");
      }

      // 解析参数
      Date startDate = schedule.getStartDate();
      Date endDate = schedule.getEndDate();

      String jobName = FlowScheduleJob.genJobName(flowId);
      String jobGroupName = FlowScheduleJob.genJobGroupName(projectId);

      Map<String, Object> dataMap = FlowScheduleJob.genDataMap(projectId, flowId, schedule);

      QuartzManager
          .addJobAndTrigger(jobName, jobGroupName, FlowScheduleJob.class, startDate, endDate,
              schedule.getCrontab(), dataMap);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 删除调度信息
   */
  @Override
  public RetInfo deleteSchedule(int projectId, int flowId) throws TException {
    logger.info("delete schedules of project id:{}, flow id:{}", projectId, flowId);

    try {
      String jobName = FlowScheduleJob.genJobName(flowId);
      String jobGroupName = FlowScheduleJob.genJobGroupName(projectId);

      QuartzManager.deleteJob(jobName, jobGroupName);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 删除一个项目的所有调度信息
   */
  @Override
  public RetInfo deleteSchedules(int projectId) throws TException {
    logger.info("delete schedules of project id:{}", projectId);

    try {
      String jobGroupName = FlowScheduleJob.genJobGroupName(projectId);

      QuartzManager.deleteJobs(jobGroupName);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 运行一个工作流, 是指直接运行的方式
   *
   * @param runTime 执行该工作流的时刻
   */
  @Override
  public RetResultInfo execFlow(int projectId, int flowId, long runTime, ExecInfo execInfo)
      throws TException {
    logger.info("exec flow project id:{}, flow id:{}, run time:{}, exec info:{}", projectId, flowId,
        runTime, execInfo);

    ExecutionFlow executionFlow;

    try {
      ProjectFlow flow = flowDao.projectFlowFindById(flowId);

      if (flow == null) {
        logger.error("flow: {} is not exists", flowId);
        return new RetResultInfo(ResultHelper.createErrorResult("flow is not exists"), null);
      }

      // 构建一个用于执行的工作流
      executionFlow = flowDao.scheduleFlowToExecution(projectId,
          flowId,
          flow.getOwnerId(),
          new Date(runTime),
          ExecType.DIRECT,
          FailurePolicyType.valueOfType(execInfo.failurePolicy),
          0, // 默认不重复执行
          execInfo.getNodeName(),
          NodeDepType.valueOfType(execInfo.getNodeDep()),
          NotifyType.valueOfType(execInfo.getNotifyType()),
          execInfo.getNotifyMails(),
          execInfo.timeout);

      ExecFlowInfo execFlowInfo = new ExecFlowInfo(executionFlow.getId());

      logger.info("insert a flow to execution, exec id:{}, flow id:{}", executionFlow.getId(),
          flowId);

      // 添加到任务队列中
      executionFlowQueue.add(execFlowInfo);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return new RetResultInfo(ResultHelper.createErrorResult(e.getMessage()), null);
    }

    return new RetResultInfo(ResultHelper.SUCCESS, Arrays.asList(executionFlow.getId()));
  }

  /**
   * @param execId
   * @return
   * @throws TException
   */
  @Override
  public RetInfo cancelExecFlow(int execId) throws TException {
    logger.info("receive exec workflow request, id: {}", execId);

    try {
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

      ExecutorClient executionClient = new ExecutorClient(workerInfo[0],
          Integer.valueOf(workerInfo[1]));

      return executionClient.cancelExecFlow(execId);
    } catch (Exception e) {
      logger.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
  }

  /**
   * 补数据
   */
  @Override
  public RetResultInfo appendWorkFlow(int projectId, int flowId, ScheduleInfo scheduleInfo,
      ExecInfo execInfo) throws TException {
    logger.info("append workflow projectId:{}, flowId:{}, scheduleMeta:{}, execInfo:{}", projectId,
        flowId,
        scheduleInfo, execInfo);

    try {
      ProjectFlow flow = flowDao.projectFlowFindById(flowId);

      // 若 workflow 被删除
      if (flow == null) {
        logger.error("projectId:{}, flowId:{} workflow not exists", projectId, flowId);
        return ResultDetailHelper.createErrorResult("current workflow not exists");
      }

      String crontabStr = scheduleInfo.getCrontab();
      CronExpression cron = new CronExpression(crontabStr);

      Date startDateTime = new Date(scheduleInfo.getStartDate());
      Date endDateTime = new Date(scheduleInfo.getEndDate());

      // 提交补数据任务
      flowExecManager.submitAddData(flow, cron, startDateTime, endDateTime, execInfo);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultDetailHelper.createErrorResult(e.getMessage());
    }

    return ResultDetailHelper.createSuccessResult(Collections.emptyList());
  }

  /**
   * 根据即席查询的执行 id 执行即席查询
   */
  @Override
  public RetInfo execAdHoc(int adHocId) {
    logger.info("receive exec ad hoc request, id: {}", adHocId);

    try {
      AdHoc adHoc = adHocDao.getAdHoc(adHocId);

      if (adHoc == null) {
        logger.error("ad hoc id {} not exists", adHocId);
        return ResultHelper.createErrorResult("ad hoc id not exists");
      }

      // 接收到了, 会更新状态, 在依赖资源中
      if (adHoc.getStatus().typeIsFinished()) {
        logger.error("ad hoc id {} finished unexpected", adHocId);
        return ResultHelper.createErrorResult("task finished unexpected");
      }

      adHoc.setStatus(FlowStatus.WAITING_RES);
      adHocDao.updateAdHocStatus(adHoc);

      ExecutorServerInfo executorServerInfo = executorServerManager.getExecutorServer();

      if (executorServerInfo == null) {
        throw new ExecException("can't found active executor server");
      }

      logger.info("exec ad hoc {} on server {}:{}", adHocId, executorServerInfo.getHost(),
          executorServerInfo.getPort());

      ExecutorClient executorClient = new ExecutorClient(executorServerInfo);
      executorClient.execAdHoc(adHocId);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);

      // 如果是依赖资源中的状态, 更新为失败, 因为调用失败了
      AdHoc adHoc = adHocDao.getAdHoc(adHocId);

      if (adHoc != null && adHoc.getStatus() == FlowStatus.WAITING_RES) {
        adHoc.setStatus(FlowStatus.FAILED);
        adHoc.setEndTime(new Date());

        adHocDao.updateAdHocStatus(adHoc);
      }

      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 执行某个流任务 <p>
   *
   * @param execId : 执行 id
   */
  public RetInfo execStreamingJob(int execId) throws TException {
    logger.info("receive exec streaming job request, id: {}", execId);

    try {
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
      streamingResult.setWorker(
          String.format("%s:%s", executorServerInfo.getHost(), executorServerInfo.getPort()));

      streamingDao.updateResult(streamingResult);

      logger.info("exec streaming job {} on server {}:{}", execId, executorServerInfo.getHost(),
          executorServerInfo.getPort());

      ExecutorClient executionClient = new ExecutorClient(executorServerInfo.getHost(),
          executorServerInfo.getPort());

      return executionClient.execStreamingJob(execId);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);

      // 如果是依赖资源中的状态, 更新为失败, 因为调用失败了
      StreamingResult streamingResult = streamingDao.queryStreamingExec(execId);

      if (streamingResult != null && streamingResult.getStatus() == FlowStatus.WAITING_RES) {
        streamingResult.setStatus(FlowStatus.FAILED);
        streamingResult.setEndTime(new Date());

        streamingDao.updateResult(streamingResult);
      }

      return ResultHelper.createErrorResult(e.getMessage());
    }
  }

  /**
   * 取消在执行的指定流任务 <p>
   *
   * @param execId : 执行 id
   */
  public RetInfo cancelStreamingJob(int execId) throws TException {
    logger.info("receive cancel streaming job request, id: {}", execId);

    try {
      StreamingResult streamingResult = streamingDao.queryStreamingExec(execId);

      if (streamingResult == null) {
        throw new MasterException("streaming exec id is not exists");
      }

      String worker = streamingResult.getWorker();
      if (worker == null) {
        throw new MasterException("worker is not exists");
      }

      Pair<String, Integer> pair = CommonUtil.parseWorker(worker);
      if (pair == null) {
        throw new MasterException("worker is not validate format " + worker);
      }

      logger.info("cancel exec streaming {} on worker {}", execId, worker);

      ExecutorClient executionClient = new ExecutorClient(pair.getLeft(), pair.getRight());

      return executionClient.cancelStreamingJob(execId);
    } catch (Exception e) {
      logger.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
  }

  /**
   * 激活流任务执行, 由停止状态转为激活状态
   */
  @Override
  public RetInfo activateStreamingJob(int execId) throws TException {
    logger.info("receive activate streaming job request, id: {}", execId);

    try {
      StreamingResult streamingResult = streamingDao.queryStreamingExec(execId);

      if (streamingResult == null) {
        throw new MasterException("streaming exec id is not exists");
      }

      String worker = streamingResult.getWorker();
      if (worker == null) {
        throw new MasterException("worker is not exists");
      }

      Pair<String, Integer> pair = CommonUtil.parseWorker(worker);
      if (pair == null) {
        throw new MasterException("worker is not validate format " + worker);
      }

      logger.info("Activate exec streaming {} on worker {}", execId, worker);

      ExecutorClient executionClient = new ExecutorClient(pair.getLeft(),
          pair.getRight());

      return executionClient.activateStreamingJob(execId);
    } catch (Exception e) {
      logger.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
  }

  /**
   * 暂停流任务
   */
  @Override
  public RetInfo deactivateStreamingJob(int execId) throws TException {
    logger.info("receive deactivate streaming job request, id: {}", execId);

    try {
      StreamingResult streamingResult = streamingDao.queryStreamingExec(execId);

      if (streamingResult == null) {
        throw new MasterException("streaming exec id is not exists");
      }

      String worker = streamingResult.getWorker();
      if (worker == null) {
        throw new MasterException("worker is not exists");
      }

      Pair<String, Integer> pair = CommonUtil.parseWorker(worker);
      if (pair == null) {
        throw new MasterException("worker is not validate format " + worker);
      }

      logger.info("Deactivate exec streaming {} on worker {}", execId, worker);

      ExecutorClient executionClient = new ExecutorClient(pair.getLeft(), pair.getRight());

      return executionClient.deactivateStreamingJob(execId);
    } catch (Exception e) {
      logger.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
  }

  /**
   * 接受 executor 注册的接口
   *
   * @param host executor 注册的 host 地址
   * @param port executor 注册的 port
   */
  @Override
  public RetInfo registerExecutor(String host, int port, long registerTime) throws TException {
    logger.info("register executor server[{}:{}]", host, port);

    try {
      if (Math.abs(System.currentTimeMillis() - registerTime)
          > MasterConfig.masterExecutorMaxAllowTicketInterval) {
        logger.warn("master and executor clock ticket is more than {}",
            MasterConfig.masterExecutorMaxAllowTicketInterval);

        return ResultHelper.createErrorResult("master and executor clock ticket is more than "
            + MasterConfig.masterExecutorMaxAllowTicketInterval);
      }

      // 设置接收到的时间, 以及心跳发送过来的时间
      HeartBeatData heartBeatData = new HeartBeatData();
      heartBeatData.setReportDate(registerTime);
      heartBeatData.setReceiveDate(System.currentTimeMillis());

      ExecutorServerInfo executorServerInfo = new ExecutorServerInfo();

      executorServerInfo.setHost(host);
      executorServerInfo.setPort(port);
      executorServerInfo.setHeartBeatData(heartBeatData);

      if (executorServerManager.containServer(executorServerInfo)) {
        executorServerManager.updateServer(executorServerInfo);

        // 如果是注册过, 需要重新提交任务
        flowSubmit2ExecutorThread.resubmitExecFlow(executorServerInfo);
      } else {
        // 直接添加即可, 新的 server
        executorServerManager.addServer(executorServerInfo);
      }
    } catch (Exception e) {
      logger.warn("executor register error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  // 告知 master 已经关闭
  @Override
  public RetInfo downExecutor(String host, int port) throws TException {
    logger.info("down executor server[{}:{}]", host, port);

    try {
      ExecutorServerInfo executorServerInfo = new ExecutorServerInfo();

      executorServerInfo.setHost(host);
      executorServerInfo.setPort(port);

      executorServerManager.removeServer(executorServerInfo);

      // 重新提交上面的任务
      flowSubmit2ExecutorThread.resubmitExecFlow(executorServerInfo);
    } catch (Exception e) {
      logger.warn("executor down error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 接受 executor 汇报心跳的接口
   *
   * @param host executor 的 host 地址
   * @param port executor 的 port
   */
  @Override
  public RetInfo executorReport(String host, int port, HeartBeatData heartBeatData)
      throws TException {
    logger.info("executor server[{}:{}] report info {}", host, port, heartBeatData);

    try {
      // 设置接收到的时间
      heartBeatData.setReceiveDate(System.currentTimeMillis());

      ExecutorServerInfo executorServerInfo = new ExecutorServerInfo();
      executorServerInfo.setHost(host);
      executorServerInfo.setPort(port);
      executorServerInfo.setHeartBeatData(heartBeatData);

      executorServerManager.updateServer(executorServerInfo);
    } catch (Exception e) {
      logger.warn(String.format("executor report error, [%s:%d]", host, port), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }
}
