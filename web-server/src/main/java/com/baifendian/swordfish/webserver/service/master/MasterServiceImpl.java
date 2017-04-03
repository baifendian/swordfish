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
import com.baifendian.swordfish.dao.AdHocDao;
import com.baifendian.swordfish.dao.enums.FlowRunType;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.enums.FlowType;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.dao.model.flow.ScheduleMeta;
import com.baifendian.swordfish.rpc.HeartBeatData;
import com.baifendian.swordfish.rpc.MasterService.Iface;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.ScheduleInfo;
import com.baifendian.swordfish.webserver.ExecutorClient;
import com.baifendian.swordfish.webserver.ExecutorServerInfo;
import com.baifendian.swordfish.webserver.ExecutorServerManager;
import com.baifendian.swordfish.webserver.config.MasterConfig;
import com.baifendian.swordfish.webserver.exception.MasterException;
import com.baifendian.swordfish.webserver.quartz.FlowScheduleJob;
import com.baifendian.swordfish.webserver.quartz.QuartzManager;
import com.baifendian.swordfish.webserver.utils.ResultHelper;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * MasterService 实现 <p>
 */
public class MasterServiceImpl implements Iface {

  /**
   * LOGGER
   */
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  /**
   * {@link FlowDao}
   */
  private final FlowDao flowDao;

  private final AdHocDao adHocDao;

  /**
   * {@link FlowExecManager}
   */

  private final Master master;

  public MasterServiceImpl(FlowDao flowDao, Master master) {
    this.flowDao = flowDao;
    this.adHocDao = DaoFactory.getDaoInstance(AdHocDao.class);
    this.master = master;

  }

  @Override
  public RetInfo execFlow(int execId) throws TException {
    try {
      ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
      if (executionFlow == null) {
        LOGGER.error("execId is not exists");
        return ResultHelper.createErrorResult("execId is not exists");
      }
      flowDao.updateExecutionFlowStatus(execId, FlowStatus.INIT);
      ExecFlowInfo execFlowInfo = new ExecFlowInfo();
      execFlowInfo.setExecId(executionFlow.getId());

      master.addExecFlow(execFlowInfo);
    } catch (Exception e){
      LOGGER.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
    return ResultHelper.SUCCESS;
  }

  @Override
  public RetInfo execAdHoc(int adHocId) {
    try {
      LOGGER.debug("receive exec ad hoc request, id:{}", adHocId);
      AdHoc adHoc = adHocDao.getAdHoc(adHocId);
      if (adHoc == null) {
        LOGGER.error("adhoc id {} not exists", adHocId);
        return ResultHelper.createErrorResult("adhoc id not exists");
      }
      master.execAdHoc(adHocId);
    } catch (TException | ExecException e) {
      LOGGER.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
    return ResultHelper.SUCCESS;
  }

  /**
   * 设置调度信息, 最终设置的是 Crontab 表达式(其实是按照 Quartz 的语法)
   */
  @Override
  public RetInfo setSchedule(int projectId, int flowId, ScheduleInfo scheduleInfo) throws TException {
    LOGGER.info("set schedule {} {}", projectId, flowId);

    if (scheduleInfo == null || StringUtils.isEmpty(scheduleInfo.getCronExpression())) {
      return ResultHelper.createErrorResult("scheduleInfo 参数内容不能为空");
    }

    try {
      Schedule schedule = flowDao.querySchedule(flowId);
      if (schedule == null) {
        return ResultHelper.createErrorResult("schedule 元数据为空");
      }

      // 解析参数
      Date startDate = new Date(scheduleInfo.getStartDate());
      Date endDate = new Date(scheduleInfo.getEndDate());

      String jobName = FlowScheduleJob.genJobName(flowId);
      String jobGroupName = FlowScheduleJob.genJobGroupName(projectId);
      Map<String, Object> dataMap = FlowScheduleJob.genDataMap(projectId, flowId, schedule);
      QuartzManager.addJobAndTrigger(jobName, jobGroupName, FlowScheduleJob.class, startDate, endDate, scheduleInfo.getCronExpression(), dataMap);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 删除调度信息
   */
  @Override
  public RetInfo deleteSchedule(int projectId, int flowId) throws TException {

    try {
      String jobName = FlowScheduleJob.genJobName(flowId);
      String jobGroupName = FlowScheduleJob.genJobGroupName(projectId);
      QuartzManager.deleteJob(jobName, jobGroupName);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 删除一个项目的所有调度信息
   */
  @Override
  public RetInfo deleteSchedules(int projectId) throws TException {
    try {
      String jobGroupName = FlowScheduleJob.genJobGroupName(projectId);
      QuartzManager.deleteJobs(jobGroupName);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 补数据
   */
  @Override
  public RetInfo appendWorkFlow(int projectId, int flowId, String scheduleMeta) throws TException {
    ScheduleMeta meta = null;
    try {
      ProjectFlow flow = flowDao.queryFlow(flowId);
      // 若 workflow 被删除
      if (flow == null) {
        LOGGER.error("projectId:{},flowId:{} 的工作流不存在", projectId, flowId);
        return ResultHelper.createErrorResult("当前workflow 不存在");
      }

      meta = JsonUtil.parseObject(scheduleMeta, ScheduleMeta.class);
      String crontabStr = meta.getCrontab();
      CronExpression cron = new CronExpression(crontabStr);

      Date startDateTime = meta.getStartDate();
      Date endDateTime = meta.getEndDate();

      // 提交补数据任务
      master.submitAddData(flow, cron, startDateTime, endDateTime);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    if (meta == null) {
      return ResultHelper.createErrorResult("scheduleMeta 信息不正确");
    }

    return ResultHelper.SUCCESS;
  }

  @Override
  public RetInfo registerExecutor(String host, int port, long registerTime) throws TException {
    try {
      master.registerExecutor(host, port, registerTime);
    } catch (MasterException e) {
      LOGGER.warn("executor register error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
    return ResultHelper.SUCCESS;
  }

  /**
   * execServer汇报心跳 host : host地址 port : 端口号
   */
  @Override
  public RetInfo executorReport(String host, int port, HeartBeatData heartBeatData) throws org.apache.thrift.TException {
    try {
      master.executorReport(host, port, heartBeatData);
    } catch (MasterException e) {
      LOGGER.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
    return ResultHelper.SUCCESS;
  }

  @Override
  public RetInfo cancelExecFlow(int execId) throws org.apache.thrift.TException {
    try {
      return master.cancelExecFlow(execId);
    } catch (MasterException e) {
      LOGGER.warn("executor report error", e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
  }

}
