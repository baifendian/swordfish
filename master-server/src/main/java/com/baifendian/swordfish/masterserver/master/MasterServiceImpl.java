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

import com.baifendian.swordfish.dao.AdHocDao;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.StreamingDao;
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.enums.NodeDepType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.masterserver.quartz.FlowScheduleJob;
import com.baifendian.swordfish.masterserver.quartz.QuartzManager;
import com.baifendian.swordfish.masterserver.utils.ResultDetailHelper;
import com.baifendian.swordfish.masterserver.utils.ResultHelper;
import com.baifendian.swordfish.rpc.*;
import com.baifendian.swordfish.rpc.MasterService.Iface;
import org.apache.thrift.TException;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

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
   * 任务执行的主程序
   */
  private final JobExecManager jobExecManager;

  public MasterServiceImpl(JobExecManager jobExecManager) {
    this.jobExecManager = jobExecManager;

    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    this.adHocDao = DaoFactory.getDaoInstance(AdHocDao.class);
    this.streamingDao = DaoFactory.getDaoInstance(StreamingDao.class);
  }

  /**
   * 设置调度信息, 最终设置的是 Crontab 表达式(其实是按照 Quartz 的语法)
   *
   * @param projectId
   * @param flowId
   * @return
   * @throws TException
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
      QuartzManager.addJobAndTrigger(jobName, jobGroupName, FlowScheduleJob.class, startDate, endDate, schedule.getCrontab(), dataMap);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 删除调度信息
   *
   * @param projectId
   * @param flowId
   * @return
   * @throws TException
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
   *
   * @param projectId
   * @return
   * @throws TException
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
   * @param projectId
   * @param flowId
   * @param runTime   执行该工作流的时刻
   * @param execInfo
   * @return
   * @throws TException
   */
  @Override
  public RetResultInfo execFlow(int projectId, int flowId, long runTime, ExecInfo execInfo) throws TException {
    logger.info("exec flow project id:{}, flow id:{}, run time:{}, exec info:{}", projectId, flowId, runTime, execInfo);

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
          0, // 默认不重复执行
          execInfo.getNodeName(),
          NodeDepType.valueOfType(execInfo.getNodeDep()),
          NotifyType.valueOfType(execInfo.getNotifyType()),
          execInfo.getNotifyMails(),
          execInfo.timeout);

      ExecFlowInfo execFlowInfo = new ExecFlowInfo();
      execFlowInfo.setExecId(executionFlow.getId());

      logger.info("insert a flow to execution, exec id:{}, flow id:{}", executionFlow.getId(), flowId);

      jobExecManager.addExecFlow(execFlowInfo);
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
      return jobExecManager.cancelExecFlow(execId);
    } catch (Exception e) {
      logger.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
  }

  /**
   * 执行某个流任务
   * <p>
   * execId : 执行 id
   *
   * @param execId
   */
  public RetInfo execStreamingJob(int execId) throws TException {
    logger.info("receive exec streaming job request, id: {}", execId);

    try {
      return jobExecManager.execStreamingJob(execId);
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
   * 取消在执行的指定流任务
   * <p>
   * execId : 执行 id
   *
   * @param execId
   */
  public RetInfo cancelStreamingJob(int execId) throws TException {
    logger.info("receive cancel streaming job request, id: {}", execId);

    try {
      return jobExecManager.cancelStreamingJob(execId);
    } catch (Exception e) {
      logger.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
  }

  @Override
  public RetInfo activateStreamingJob(int execId) throws TException {
    logger.info("receive activate streaming job request, id: {}", execId);

    try {
      return jobExecManager.activateStreamingJob(execId);
    } catch (Exception e) {
      logger.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
  }

  @Override
  public RetInfo deactivateStreamingJob(int execId) throws TException {
    logger.info("receive deactivate streaming job request, id: {}", execId);

    try {
      return jobExecManager.deactivateStreamingJob(execId);
    } catch (Exception e) {
      logger.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
  }

  /**
   * 补数据
   *
   * @param projectId
   * @param flowId
   * @param scheduleInfo
   * @return
   * @throws TException
   */
  @Override
  public RetResultInfo appendWorkFlow(int projectId, int flowId, ScheduleInfo scheduleInfo) throws TException {
    logger.info("append workflow projectId:{}, flowId:{}, scheduleMeta:{}", projectId, flowId, scheduleInfo);

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
      jobExecManager.submitAddData(flow, cron, startDateTime, endDateTime);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultDetailHelper.createErrorResult(e.getMessage());
    }

    return ResultDetailHelper.createSuccessResult(Collections.emptyList());
  }

  /**
   * 根据即席查询的执行 id 执行即席查询
   *
   * @param adHocId
   * @return
   */
  @Override
  public RetInfo execAdHoc(int adHocId) {
    try {
      logger.info("receive exec ad hoc request, id: {}", adHocId);

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

      jobExecManager.execAdHoc(adHocId);
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
   * 接受 executor 注册的接口
   *
   * @param host         executor 注册的 host 地址
   * @param port         executor 注册的 port
   * @param registerTime
   * @return
   * @throws TException
   */
  @Override
  public RetInfo registerExecutor(String host, int port, long registerTime) throws TException {
    try {
      jobExecManager.registerExecutor(host, port, registerTime);
    } catch (Exception e) {
      logger.warn("executor register error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 接受 executor 汇报心跳的接口
   *
   * @param host          executor 的 host 地址
   * @param port          executor 的 port
   * @param heartBeatData
   * @return
   * @throws TException
   */
  @Override
  public RetInfo executorReport(String host, int port, HeartBeatData heartBeatData) throws TException {
    try {
      jobExecManager.executorReport(host, port, heartBeatData);
    } catch (Exception e) {
      logger.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }
}
