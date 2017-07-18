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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
   * 具体执行一个工作
   *
   * @param context
   * @throws JobExecutionException
   */
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    // 1. 获取参数
    JobDataMap dataMap = context.getJobDetail().getJobDataMap();

    int projectId = dataMap.getInt(PARAM_PROJECT_ID);
    int flowId = dataMap.getInt(PARAM_FLOW_ID);

    logger.info("trigger at:{}, flow id:{}", context.getFireTime(), flowId);

    // Schedule schedule =
    // JsonUtil.parseObject(dataMap.getString(PARAM_SCHEDULE),
    // Schedule.class);

    Date scheduledFireTime = context.getScheduledFireTime();

    // 系统触发的时间
    long systemTime = System.currentTimeMillis();

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
              ExecType.SCHEDULER, schedule.getFailurePolicy(), schedule.getMaxTryTimes(), null, null, schedule.getNotifyType(), schedule.getNotifyMails(), schedule.getTimeout());
    } catch (Exception e) {
      logger.error("insert execution flow error", e);
      throw new JobExecutionException(e);
    }

    executionFlow.setProjectId(projectId);
    executionFlow.setProjectName(flow.getProjectName());
    executionFlow.setWorkflowName(flow.getName());

    // 是否需要标记为等待调度状态
    boolean isNotUpdateWaitingDep = true;

    // 如果需要等待上一个调度周期完成
    if (schedule.getDepPolicy() == DepPolicyType.DEP_PRE) {
      Date previousFireTime = context.getPreviousFireTime();

      // 存在上一调度周期
      if (previousFireTime != null) {
        logger.info("previous fire time is: {}", previousFireTime);

        // 需要更新状态为 WAITING_DEP
        if (isNotUpdateWaitingDep) {
          updateWaitingDepFlowStatus(executionFlow, FlowStatus.WAITING_DEP);
          isNotUpdateWaitingDep = false;
        }

        // 如果自依赖的上一个调度周期失败，那么本次也失败
        if (!checkWorkflowStatus(flowId, previousFireTime, systemTime, schedule.getTimeout())) {

          updateWaitingDepFlowStatus(executionFlow, FlowStatus.DEP_FAILED);

          logger.error("Exec id:{} self dependence last cycle execution failed!", executionFlow.getId());

          // 发送邮件
          if (executionFlow.getNotifyType().typeIsSendFailureMail()) {
            EmailManager.sendMessageOfExecutionFlow(executionFlow);
          }

          return;
        }
      }
    }

    List<DepWorkflow> deps = JsonUtil.parseObjectList(schedule.getDepWorkflowsStr(), DepWorkflow.class);

    if (deps != null) {
      // 需要更新状态为 WAITING_DEP
      if (isNotUpdateWaitingDep) {
        updateWaitingDepFlowStatus(executionFlow, FlowStatus.WAITING_DEP);
      }

      // 检测依赖
      boolean isSuccess = checkDeps(schedule, scheduledFireTime, deps, systemTime, schedule.getTimeout());

      // 依赖失败，则当前任务也失败
      if (!isSuccess) {
        updateWaitingDepFlowStatus(executionFlow, FlowStatus.DEP_FAILED);

        logger.error("Exec id:{} depended workflow execution failed", executionFlow.getId());

        // 发送邮件
        if (executionFlow.getNotifyType().typeIsSendFailureMail()) {
          EmailManager.sendMessageOfExecutionFlow(executionFlow);
        }

        return;
      }
    }

    // 发送执行任务到 worker
    sendToExecution(executionFlow);
  }

  /**
   * 更新 workflow 的执行状态
   *
   * @param executionFlow
   * @param flowStatus
   */
  private void updateWaitingDepFlowStatus(ExecutionFlow executionFlow, FlowStatus flowStatus) {
    Date now = new Date();

    executionFlow.setStatus(flowStatus);
    if (flowStatus != null && flowStatus.typeIsFinished()) {
      executionFlow.setEndTime(now);
    }
    flowDao.updateExecutionFlow(executionFlow);
  }

  /**
   * 检测一个指定时间的任务是否完成
   *
   * @param flowId     指定的任务ID
   * @param dataTime   指定时间
   * @param systemTime 当前系统触发时间，用于判断超时
   * @param timeout    等待任务完成的超时
   */
  private boolean checkWorkflowStatus(int flowId, Date dataTime, long systemTime, Integer timeout) {
    // 循环检测，直到检测到依赖是否成功
    while (true) {
      boolean isNotFinshed = false;
      // 看上一个调度周期是否有成功的
      ExecutionFlow executionFlow = flowDao.queryExecutionFlowByScheduleTime(flowId, dataTime);

      if (executionFlow == null) {
        return false;
      }

      FlowStatus flowStatus = executionFlow.getStatus();
      if (flowStatus != null && flowStatus.typeIsSuccess()) {
        return true;
      } else if (flowStatus == null || flowStatus.typeIsNotFinished()) {
        isNotFinshed = true;
      }

      if (isNotFinshed) {
        // 如果超时
        if (checkTimeout(systemTime, timeout)) {
          logger.error("Wait for last cycle timeout");
          return false;
        }

        // 等待一定的时间，再进行下一次检测
        try {
          Thread.sleep(checkInterval);
        } catch (InterruptedException e) {
          logger.error(e.getMessage(), e);
          return false;
        }
      } else {
        return false;
      }

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
   * 检测依赖的工作流是否正常运行
   *
   * @param schedule          当前工作流调度信息
   * @param scheduledFireTime 当前调度应该触发的时间
   * @param deps              依赖工作流列表
   * @param systemTime        系统提交的实际时间戳
   * @param timeout           等待依赖工作流的超时
   * @return
   */
  private boolean checkDeps(Schedule schedule, Date scheduledFireTime, List<DepWorkflow> deps, long systemTime, Integer timeout) {
    for (DepWorkflow depWorkflow : deps) {
      int depFlowId = depWorkflow.getWorkflowId();
      Schedule depSchedule = flowDao.querySchedule(depFlowId);
      if (depSchedule != null) {

        // 识别周期
        ScheduleType cycle = CrontabUtil.getCycle(schedule.getCrontab());
        ScheduleType depCycle = CrontabUtil.getCycle(depSchedule.getCrontab());

        // 如果不能识别周期采用不能识别周期策略
        if (cycle == null || depCycle == null) {
          if (!checkExecutionFlowStatus(scheduledFireTime, depFlowId, systemTime, timeout)) {
            return false;
          }
        }

        boolean depStatus;
        // 如果被依赖工作流的调度级别比较小
        if (cycle.ordinal() > depCycle.ordinal()) {
          Map.Entry<Date, Date> cycleDate = CrontabUtil.getPreCycleDate(scheduledFireTime, depCycle);

          // 生成工作流的调度信息
          CronExpression cronExpression;
          try {
            cronExpression = CrontabUtil.parseCronExp(schedule.getCrontab());
          } catch (Exception e) {
            logger.error("flow {} crontab parse error", depFlowId);
            return false;
          }
          //检测本次调度级别最早的依赖调度有没有执行完成。
          depStatus = checkCycleWorkflowStatus(cycleDate.getValue(), scheduledFireTime, depFlowId, cronExpression, systemTime, timeout, true);
          //如果本次调度级别最早的依赖调度没有完成，那么再看本周起最后的依赖调度有没有完成。
          if (!depStatus) {
            depStatus = checkCycleWorkflowStatus(cycleDate.getKey(), cycleDate.getValue(), depFlowId, cronExpression, systemTime, timeout, false);
          }
        } else {
          depStatus = checkExecutionFlowStatus(scheduledFireTime, depFlowId, systemTime, timeout);
        }

        if (!depStatus) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * 检测相对时间最近前一次executionFlow执行的结果。
   *
   * @param relativeTime 检测的相对时间
   * @param flowId       工作流的ID
   * @param systemTime   系统当前提交运行的时间戳
   * @param timeout      等待依赖工作流运行的超时时间
   * @return
   */
  private boolean checkExecutionFlowStatus(Date relativeTime, int flowId, long systemTime, int timeout) {
    while (true) {
      // 是否没有完成
      boolean isNotFinshed = false;
      ExecutionFlow executionFlow = flowDao.executionFlowPreDate(flowId, relativeTime);

      // 系统没有触发调度直接认为依赖失败
      if (executionFlow == null) {
        return false;
      }

      FlowStatus flowStatus = executionFlow.getStatus();
      if (flowStatus != null && flowStatus.typeIsSuccess()) {
        return true;
      } else if (flowStatus == null || flowStatus.typeIsNotFinished()) {
        isNotFinshed = true;
      }

      if (isNotFinshed) {
        // 如果没有启动时间, 也没有调度真实时间，直接算超时，如果有就计算超时
        if (checkTimeout(systemTime, timeout)) {
          logger.error("Wait for dependence workflow timeout");
          return false;
        }

        try {
          Thread.sleep(checkInterval);
        } catch (InterruptedException e) {
          logger.error(e.getMessage(), e);
          return false;
        }
      } else {
        // 完成了, 返回
        return false;
      }
    }
  }

  /**
   * 检测一段时间内调度最后一个工作流，或第一个工作流是否完成
   *
   * @param startTime      周期起始时间
   * @param endTime        周期结束时间
   * @param flowId         工作流ID
   * @param cronExpression 工作流的调度信息
   * @param systemTime     当前系统触发时间，用于判断超时
   * @param timeout        等待工作流执行完成的超时
   * @Param firstOrLast    检测第一个还是最后一个任务 如果true是第一个任务，如果false是最后一个任务
   */
  private boolean checkCycleWorkflowStatus(Date startTime, Date endTime, int flowId, CronExpression cronExpression, long systemTime, Integer timeout, boolean firstOrLast) {

    Date fireTime = null;
    List<Date> dateList = CrontabUtil.getCycleFireDate(startTime, endTime, cronExpression);
    if (CollectionUtils.isNotEmpty(dateList)) {
      fireTime = dateList.get(firstOrLast ? 0 : dateList.size() - 1);
    }

    // 如果在时间段内没有找到触发的时间点，就返回失败。
    if (fireTime == null) {
      return false;
    }
    // 如果有就检测指定调度时间点的任务是否执行了
    return checkWorkflowStatus(flowId, fireTime, systemTime, timeout);
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
   */
  private void sendToExecution(ExecutionFlow executionFlow) {
    ExecFlowInfo execFlowInfo = new ExecFlowInfo();
    execFlowInfo.setExecId(executionFlow.getId());

    logger.info("scheduler to execution, exec id:{}, schedule time:{}", executionFlow.getId(), executionFlow.getScheduleTime());

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
