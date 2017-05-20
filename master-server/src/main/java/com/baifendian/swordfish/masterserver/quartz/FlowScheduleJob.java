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
package com.baifendian.swordfish.masterserver.quartz;

import com.baifendian.swordfish.common.mail.EmailManager;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.dao.model.flow.DepWorkflow;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.masterserver.master.ExecFlowInfo;
import com.baifendian.swordfish.masterserver.utils.crontab.CrontabUtil;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Workflow 调度 Job <p>
 */
public class FlowScheduleJob implements Job {

  /**
   * logger
   */
  private final Logger logger = LoggerFactory.getLogger(FlowScheduleJob.class);

  /**
   * 名称分割符
   */
  private static final String NAME_SEPARATOR = "_";

  /**
   * FlowScheduleJob 名称前缀
   */
  private static final String FLOW_SCHEDULE_JOB_NAME_PRIFIX = "Job_Flow";

  /**
   * FlowScheduleJob 组名称前缀
   */
  private static final String FLOW_SCHEDULE_JOB_GROUP_NAME_PRIFIX = "JobGroup_Flow";

  /**
   * projectId
   */
  public static final String PARAM_PROJECT_ID = "projectId";

  /**
   * flowId
   */
  public static final String PARAM_FLOW_ID = "flowId";

  /**
   * schedule
   */
  private static final String PARAM_SCHEDULE = "schedule";

  /**
   * worker rpc client
   */
  private static BlockingQueue<ExecFlowInfo> executionFlowQueue;

  /**
   * {@link FlowDao}
   */
  private static FlowDao flowDao;

  /**
   * 检测依赖的等待时间，默认 30 s
   */
  private static long checkInterval = 30 * 1000;

  /**
   * 初始化 Job （使用该调度 Job 前，必须先调用该函数初始化） <p>
   *
   * @param executionFlowQueue
   * @param flowDao
   */
  public static void init(BlockingQueue<ExecFlowInfo> executionFlowQueue, FlowDao flowDao) {
    FlowScheduleJob.executionFlowQueue = executionFlowQueue;
    FlowScheduleJob.flowDao = flowDao;
  }

  /**
   * @param context
   * @throws JobExecutionException
   */
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    logger.info("trigger at:" + context.getFireTime());

    // 1. 获取参数
    JobDataMap dataMap = context.getJobDetail().getJobDataMap();
    int projectId = dataMap.getInt(PARAM_PROJECT_ID);
    int flowId = dataMap.getInt(PARAM_FLOW_ID);

    // Schedule schedule =
    // JsonUtil.parseObject(dataMap.getString(PARAM_SCHEDULE),
    // Schedule.class);

    Date scheduledFireTime = context.getScheduledFireTime();

    // 起始时间 (ms)
    long startTime = System.currentTimeMillis();

    ProjectFlow flow = flowDao.projectFlowFindById(flowId);

    // 若 workflow 被删除，那么直接删除当前 job
    if (flow == null) {
      deleteJob(projectId, flowId);
      logger.warn("workflow not exist，delete scheduler task of projectId:{}, flowId:{}", projectId, flowId);
      return;
    }

    // 获取依赖的 workflow 的调度信息，判断当前 workflow 是否可以执行
    Schedule schedule = flowDao.querySchedule(flowId);
    if (schedule == null) {
      deleteJob(projectId, flowId);
      logger.warn("workflow scheduler information not exist，delete scheduler task of projectId:{}, flowId:{}", projectId, flowId);
      return;
    }

    // 插入 ExecutionFlow
    ExecutionFlow executionFlow;

    try {
      executionFlow = flowDao.scheduleFlowToExecution(projectId, flowId, flow.getOwnerId(), scheduledFireTime,
              ExecType.SCHEDULER, schedule.getMaxTryTimes(), null, null, schedule.getNotifyType(), schedule.getNotifyMails(), schedule.getTimeout());
    } catch (Exception e) {
      throw new JobExecutionException(e);
    }

    executionFlow.setProjectId(projectId);
    executionFlow.setProjectName(flow.getProjectName());
    executionFlow.setWorkflowName(flow.getName());

    // 自动依赖上一调度周期才能结束
    boolean isNotUpdateWaitingDep = true;

    if (schedule.getDepPolicy() == DepPolicyType.DEP_PRE) {
      Date previousFireTime = context.getPreviousFireTime();

      // 存在上一调度周期
      if (previousFireTime != null) {
        // 需要更新状态为 WAITING_DEP
        if (isNotUpdateWaitingDep) {
          updateWaitingDepFlowStatus(executionFlow);
          isNotUpdateWaitingDep = false;
        }

        // 如果自依赖的上一个调度周期失败，那么本次也失败
        if (!checkDepWorkflowStatus(flowId, previousFireTime, startTime, schedule.getTimeout())) {
          executionFlow.setStatus(FlowStatus.DEP_FAILED);
          executionFlow.setEndTime(new Date());
          flowDao.updateExecutionFlow(executionFlow);
          logger.error("Self dependence last cycle execution failed!");
          // 发送邮件
          if (executionFlow.getNotifyType().typeIsSendFailureMail()) {
            EmailManager.sendEmail(executionFlow);
          }
          return;
        }
      }
    }

    List<DepWorkflow> deps = JsonUtil.parseObjectList(schedule.getDepWorkflowsStr(), DepWorkflow.class);
    if (deps != null) {
      // 需要更新状态为 WAITING_DEP
      if (isNotUpdateWaitingDep) {
        updateWaitingDepFlowStatus(executionFlow);
      }

      // 检测依赖
      boolean isSuccess = checkDeps(schedule, scheduledFireTime, deps, startTime, schedule.getTimeout());

      // 依赖失败，则当前任务也失败
      if (!isSuccess) {
        executionFlow.setStatus(FlowStatus.DEP_FAILED);
        executionFlow.setEndTime(new Date());

        flowDao.updateExecutionFlow(executionFlow);

        logger.error("depended workflow execution failed");

        // 发送邮件
        if (executionFlow.getNotifyType().typeIsSendFailureMail()) {
          EmailManager.sendEmail(executionFlow);
        }

        return;
      }
    }

    // 发送执行任务到 worker
    sendToExecution(executionFlow, scheduledFireTime);
  }

  /**
   * 更新 workflow 的执行状态为 WAITING_DEP <p>
   *
   * @param executionFlow
   */
  private void updateWaitingDepFlowStatus(ExecutionFlow executionFlow) {
    executionFlow.setStatus(FlowStatus.WAITING_DEP);
    flowDao.updateExecutionFlow(executionFlow);
  }

  /**
   * 检测一个 workflow 的 某一调度时刻的执行状态 <p>
   *
   * @param flowId
   * @param previousFireTime
   * @param startTime
   * @param timeout
   */
  private boolean checkDepWorkflowStatus(int flowId, Date previousFireTime, long startTime, Integer timeout) {
    // 循环检测，直到检测到依赖是否成功
    while (true) {
      boolean isFind = false;
      boolean isNotFinshed = false;
      // 看上一个调度周期是否有成功的
      List<ExecutionFlow> executionFlows = flowDao.queryFlowLastStatus(flowId, previousFireTime);

      if (CollectionUtils.isNotEmpty(executionFlows)) {
        isFind = true;

        for (ExecutionFlow executionFlow : executionFlows) {
          FlowStatus flowStatus = executionFlow.getStatus();
          if (flowStatus != null && flowStatus.typeIsSuccess()) {
            return true;
          } else if (flowStatus == null || !flowStatus.typeIsFinished()) {
            isNotFinshed = true;
          }
        }
      }

      // 没有找到上一调度周期的执行，那么这里可能是上一次执行没有更新到数据库就失败了，那么本次也应该失败
      if (!isFind) {
        return false;
      }

// 没有结束
      if (isNotFinshed) {
        // 如果超时
        if (checkTimeout(startTime, timeout)) {
          logger.error("Wait for last cycle timeout");
          return false;
        }

        // 等待一定的时间，再进行下一次检测
        try {
          Thread.sleep(checkInterval);
        } catch (InterruptedException e) {
          logger.error(e.getMessage(), e);
          return false; // 也认为是执行失败
        }
      }

      return false;
    }
  }

  /**
   * 检测是否超时 <p>
   *
   * @param startTime
   * @param timeout
   * @return 是否超时
   */
  private boolean checkTimeout(long startTime, Integer timeout) {
    if (timeout == null) {
      return false;
    }

    int usedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
    return timeout <= usedTime;
  }

  /**
   * 是否成功 <p>
   *
   * @param schedule
   * @param scheduledFireTime
   * @param deps
   * @param timeout
   */
  private boolean checkDeps(Schedule schedule, Date scheduledFireTime, List<DepWorkflow> deps, long startTime, Integer timeout) {
    for (DepWorkflow depWorkflow : deps) {
      int depFlowId = depWorkflow.getWorkflowId();
      Schedule depSchedule = flowDao.querySchedule(depFlowId);
      if (depSchedule != null) {

        //识别周期
        ScheduleType cycle = CrontabUtil.getCycle(schedule.getCrontab());
        ScheduleType depCycle = CrontabUtil.getCycle(depSchedule.getCrontab());

        //如果不能识别出周期，那么就直接取前依赖
        if (cycle == null || depCycle == null) {
          return !checkDepWorkflowStatus(scheduledFireTime, depFlowId, timeout);

        }

        Map.Entry<Date, Date> cycleDate;
        if (cycle.ordinal() > depCycle.ordinal()) {
          cycleDate = CrontabUtil.getPreCycleDate(scheduledFireTime, depCycle);
        } else {
          cycleDate = CrontabUtil.getPreCycleDate(scheduledFireTime, cycle);
        }

        // 检测依赖的最新状态
        if (!checkDepWorkflowStatus(scheduledFireTime, depFlowId, cycleDate, startTime, timeout)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * 检测非匹配周期模式的依赖workflow是否完成
   *
   * @return
   */
  private boolean checkDepWorkflowStatus(Date scheduledFireTime, int depFlowId, int timeout) {
    ExecutionFlow executionFlow = flowDao.executionFlowPreDate(depFlowId, scheduledFireTime);

    while (true) {
      boolean isNotFinshed = false;

      if (executionFlow == null) {
        return false;
      }

      FlowStatus flowStatus = executionFlow.getStatus();
      if (flowStatus != null && flowStatus.typeIsSuccess()) {
        return true; // 已经执行成功
      } else if (flowStatus == null || !flowStatus.typeIsFinished()) {
        isNotFinshed = true;
      }

      if (isNotFinshed) {

        //获取基准事件，如果有submit时间就取submit 如果还没有submit就取 正常调度时间
        Date startTime = executionFlow.getSubmitTime() != null ? executionFlow.getSubmitTime() : executionFlow.getScheduleTime();
        if (checkTimeout(executionFlow.getSubmitTime().getTime(), timeout)) {
          logger.error("等待依赖的 workflow 任务超时");
          return false; // 也认为是执行失败
        }

        try {
          Thread.sleep(checkInterval);
        } catch (InterruptedException e) {
          logger.error(e.getMessage(), e);
          return false; // 也认为是执行失败
        }

        //重新获取 executionFlow
        executionFlow = flowDao.queryExecutionFlow(executionFlow.getId());
      } else {
        return false;
      }
    }
  }

  /**
   * 检测依赖的 workflow 的状态 <p>
   *
   * @param scheduledFireTime
   * @param depFlowId
   * @param cycleDate
   * @param startTime
   * @param timeout
   * @return
   */
  private boolean checkDepWorkflowStatus(Date scheduledFireTime, int depFlowId, Map.Entry<Date, Date> cycleDate, long startTime, Integer timeout) {
    // 循环检测，直到检测到依赖是否成功
    while (true) {
      boolean isFind = false;
      boolean isNotFinshed = false;
      //TODO 这里可以使用crontab分析出执行的时间点，应该改用crontab分析出的时间点，这样更准确。
      // 看当前周期（月、周、天 等）最开始的任务是不是成功的
      List<ExecutionFlow> executionFlows = flowDao.queryFlowLastStatus(depFlowId, cycleDate.getValue(), scheduledFireTime);
      if (CollectionUtils.isNotEmpty(executionFlows)) {
        isFind = true;
        for (ExecutionFlow executionFlow : executionFlows) {
          FlowStatus flowStatus = executionFlow.getStatus();
          if (flowStatus != null && flowStatus.typeIsSuccess()) {
            return true;
          } else if (flowStatus == null || !flowStatus.typeIsFinished()) {
            isNotFinshed = true;
          }
        }
      }

      // 看上一个周期（月、周、天 等）最后的任务是不是成功的
      executionFlows = flowDao.queryFlowLastStatus(depFlowId, cycleDate.getKey(), cycleDate.getValue());
      if (CollectionUtils.isNotEmpty(executionFlows)) {
        isFind = true;
        for (ExecutionFlow executionFlow : executionFlows) {
          FlowStatus flowStatus = executionFlow.getStatus();
          if (flowStatus != null && flowStatus.typeIsSuccess()) {
            return true;
          } else if (flowStatus == null || !flowStatus.typeIsFinished()) {
            isNotFinshed = true;
          }
        }
      }

      // 没有找到依赖的 workflow 的执行，那么可能是当前 workflow 的调度已经停止，姑且认为是成功
      if (!isFind) {
        return true;
      }

      if (isNotFinshed) { // 没有结束
        // 如果超时
        if (checkTimeout(startTime, timeout)) {
          logger.error("Wait for last cycle timeout");
          return false;
        }

        // 等待一定的时间，再进行下一次检测
        try {
          Thread.sleep(checkInterval);
        } catch (InterruptedException e) {
          logger.error(e.getMessage(), e);
          return false; // 也认为是执行失败
        }
      }

      return false;
    }
  }

  /**
   * 删除 job <p>
   *
   * @param projectId
   * @param flowId
   */
  private void deleteJob(int projectId, int flowId) {
    String jobName = genJobName(flowId);
    String jobGroupName = genJobGroupName(projectId);
    QuartzManager.deleteJob(jobName, jobGroupName);
  }

  /**
   * 发送执行任务到 worker <p>
   *
   * @param executionFlow
   * @param scheduleDate
   */
  private void sendToExecution(ExecutionFlow executionFlow, Date scheduleDate) {
    ExecFlowInfo execFlowInfo = new ExecFlowInfo();
    execFlowInfo.setExecId(executionFlow.getId());
    executionFlowQueue.add(execFlowInfo);
  }

  /**
   * 生成 workflow 调度任务名称 <p>
   *
   * @param flowId
   * @return
   */
  public static String genJobName(int flowId) {
    StringBuilder builder = new StringBuilder(FLOW_SCHEDULE_JOB_NAME_PRIFIX);
    appendParam(builder, flowId);

    return builder.toString();
  }

  /**
   * 生成 workflow 调度任务组名称 <p>
   *
   * @param projectId
   * @return
   */
  public static String genJobGroupName(int projectId) {
    StringBuilder builder = new StringBuilder(FLOW_SCHEDULE_JOB_GROUP_NAME_PRIFIX);
    appendParam(builder, projectId);

    return builder.toString();
  }

  /**
   * 生成参数映射（用于参数传递） <p>
   *
   * @param projectId
   * @param flowId
   * @param schedule
   * @return
   */
  public static Map<String, Object> genDataMap(int projectId, int flowId, Schedule schedule) {
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put(PARAM_PROJECT_ID, projectId);
    dataMap.put(PARAM_FLOW_ID, flowId);
    dataMap.put(PARAM_SCHEDULE, JsonUtil.toJsonString(schedule));

    return dataMap;
  }

  /**
   * 拼接参数 <p>
   *
   * @param builder
   * @param object
   */
  private static void appendParam(StringBuilder builder, Object object) {
    builder.append(NAME_SEPARATOR);
    builder.append(object);
  }
}
