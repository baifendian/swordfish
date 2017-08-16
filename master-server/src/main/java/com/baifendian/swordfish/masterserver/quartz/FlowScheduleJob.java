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
import java.text.MessageFormat;
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
   */
  public static void init(BlockingQueue<ExecFlowInfo> executionFlowQueue, FlowDao flowDao) {
    FlowScheduleJob.executionFlowQueue = executionFlowQueue;
    FlowScheduleJob.flowDao = flowDao;
  }

  /**
   * 具体执行一个工作
   */
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    // 1. 获取参数
    JobDataMap dataMap = context.getJobDetail().getJobDataMap();

    int projectId = dataMap.getInt(PARAM_PROJECT_ID);
    int flowId = dataMap.getInt(PARAM_FLOW_ID);

    // 任务调度预计触发的时间
    Date scheduledFireTime = context.getScheduledFireTime();

    // 任务实际的触发时间
    Date fireTime = context.getFireTime();

    logger.info("trigger at:{}, flow id:{}", fireTime, flowId);

    ProjectFlow flow = flowDao.projectFlowFindById(flowId);
    // 若 workflow 被删除，那么直接删除当前 job
    if (flow == null) {
      deleteJob(projectId, flowId);
      logger.warn("workflow not exist，delete scheduler task of projectId:{}, flowId:{}", projectId,
          flowId);
      return;
    }

    // 获取依赖的 workflow 的调度信息，判断当前 workflow 是否可以执行
    Schedule schedule = flowDao.querySchedule(flowId);
    if (schedule == null) {
      deleteJob(projectId, flowId);
      logger.warn(
          "workflow scheduler information not exist，delete scheduler task of projectId:{}, flowId:{}",
          projectId, flowId);
      return;
    }

    // 插入 ExecutionFlow
    ExecutionFlow executionFlow;

    try {
      executionFlow = flowDao
          .scheduleFlowToExecution(projectId, flowId, flow.getOwnerId(), scheduledFireTime,
              ExecType.SCHEDULER, schedule.getFailurePolicy(), schedule.getMaxTryTimes(), null,
              null, schedule.getNotifyType(), schedule.getNotifyMails(), schedule.getTimeout());
      executionFlow.setProjectId(projectId);
      executionFlow.setProjectName(flow.getProjectName());
      executionFlow.setWorkflowName(flow.getName());
    } catch (Exception e) {
      logger.error("insert execution flow error", e);
      throw new JobExecutionException(e);
    }

    // 获取所有依赖
    List<DepWorkflow> deps = JsonUtil
        .parseObjectList(schedule.getDepWorkflowsStr(), DepWorkflow.class);

    boolean preCheck = true;

    // 存在依赖情况，开始依赖判断
    if (CollectionUtils.isNotEmpty(deps) || schedule.getDepPolicy() == DepPolicyType.DEP_PRE) {
      logger.info("job: {} has dep need to check!", flowId);
      updateWaitingDepFlowStatus(executionFlow, FlowStatus.WAITING_DEP);
    }

    // 先检测自依赖
    if (schedule.getDepPolicy() == DepPolicyType.DEP_PRE) {
      logger.info("job: {} start check self dep ...", flowId);
      if (!checkSelfDep(flowId, schedule, fireTime, scheduledFireTime)) {
        logger.warn("job: {} check self dep  no pass!", flowId);
        preCheck = false;
        updateWaitingDepFlowStatus(executionFlow, FlowStatus.DEP_FAILED);
      }
      logger.info("job: {} pass check self dep!", flowId);
    }

    // 检测工作流之间的依赖
    if (preCheck && CollectionUtils.isNotEmpty(deps)) {
      logger.info("job: {} start check workflow dep, dep size: {} ...", flowId, deps.size());
      if (!checkWorkflowsDep(deps, fireTime, scheduledFireTime, schedule.getTimeout())) {
        logger.warn("job: {} check workflow dep no pass!", flowId);
        preCheck = false;
        updateWaitingDepFlowStatus(executionFlow, FlowStatus.DEP_FAILED);
      }

    }

    if (preCheck) {
      sendToExecution(executionFlow);
    } else if (executionFlow.getNotifyType().typeIsSendFailureMail()) {
      EmailManager.sendMessageOfExecutionFlow(executionFlow);
    }
  }


  /**
   * 判断自依赖是否完成
   */
  private boolean checkSelfDep(int flowId,
      Schedule schedule, Date fireTime, Date scheduleFireTime) {

    CronExpression cronExpression;
    try {
      cronExpression = CrontabUtil.parseCronExp(schedule.getCrontab());
    } catch (Exception e) {
      String msg = MessageFormat
          .format("Self flow: {0} crontab: {1} parse error", flowId, schedule.getCrontab());
      logger.error(msg, e);
      return false;
    }

    ExecutionFlow executionFlow = flowDao
        .executionFlowPreDate(flowId, scheduleFireTime);

    while (true) {
      // 是否没有完成
      boolean isNotFinshed = false;
      // 是否需要刷新执行记录
      boolean flushExecFlow = false;

      //如果存在记录就处理，否则等待到超时。
      if (executionFlow != null) {

        // 检测周期特征是否符合
        Date nextDate = cronExpression.getTimeAfter(executionFlow.getScheduleTime());

        // 没有下一次了,这种情况理论不存在，返回失败
        if (nextDate == null) {
          return false;
        }

        // 如果下次执行结果就是当前调度时间，那么就看这次，否则说明依赖的工作流没有调用。
        if (nextDate.getTime() == scheduleFireTime.getTime()) {
          FlowStatus flowStatus = executionFlow.getStatus();
          if (flowStatus != null && flowStatus.typeIsSuccess()) {
            return true;
          } else if (flowStatus == null || flowStatus.typeIsNotFinished()) {
            isNotFinshed = true;
          }
          // 否则的话重新尝试获取依赖纪录。
        } else {
          //需要刷新重新查
          flushExecFlow = true;
          isNotFinshed = true;
        }

      } else {
        // 如果没有依赖纪录也尝试重新获取
        isNotFinshed = true;
        flushExecFlow = true;
      }

      if (isNotFinshed) {
        // 如果没有启动时间, 也没有调度真实时间，直接算超时，如果有就计算超时
        if (checkTimeout(fireTime.getTime(), schedule.getTimeout())) {
          logger.error("Wait for dependence workflow timeout");
          return false;
        }

        try {
          Thread.sleep(checkInterval);
          if (flushExecFlow) {
            executionFlow = flowDao.executionFlowPreDate(flowId, scheduleFireTime);
          }
        } catch (InterruptedException e) {
          logger.error(e.getMessage(), e);
          return false;
        }
      } else {
        // 完成了但是没有成功, 返回失败。
        return false;
      }

    }
  }

  /**
   * 检测所有依赖工作流是否完成
   */
  private boolean checkWorkflowsDep(List<DepWorkflow> deps, Date fireTime,
      Date scheduleFireTime, int timeout) {
    for (DepWorkflow depWorkflow : deps) {
      ProjectFlow depFlow = flowDao
          .projectFlowFindByPorjectNameAndName(depWorkflow.getProjectName(),
              depWorkflow.getWorkflowName());

      // 如果这个依赖工作流不存在，就判断下一个
      if (depFlow == null) {
        continue;
      }

      Schedule depSchedule = flowDao.querySchedule(depFlow.getId());

      CronExpression depCron;

      try {
        depCron = CrontabUtil.parseCronExp(depSchedule.getCrontab());
      } catch (Exception e) {
        // 如果不能识别依赖工作流的crontab，那么我们直接判断下一个。
        logger.error("dep flow {} crontab parse error", depFlow.getId());
        continue;
      }

      if (!checkWorkflowDep(depFlow.getId(), depCron, fireTime, scheduleFireTime, timeout)) {
        return false;
      }
    }

    return true;
  }

  /**
   * 检测某个依赖工作流是否完成
   */
  private boolean checkWorkflowDep(int flowId,
      CronExpression cronExpression, Date fireTime, Date scheduleFireTime, int timeout) {
    ExecutionFlow executionFlow = flowDao.executionFlowPreDate(flowId, scheduleFireTime);
    while (true) {
      // 是否没有完成
      boolean isNotFinshed = false;
      // 是否需要刷新纪录
      boolean flushExecFlow = false;

      //如果存在记录就处理，否则等待到超时。
      if (executionFlow != null) {

        // 检测周期特征是否符合
        Date nextDate = cronExpression.getTimeAfter(executionFlow.getScheduleTime());

        // 没有下一次了直接算通过
        if (nextDate == null) {
          return true;
        }

        // 如果依赖的下一个词调度比当前迟，那我们就看这次依赖的结果
        if (nextDate.getTime() > scheduleFireTime.getTime()) {
          FlowStatus flowStatus = executionFlow.getStatus();
          if (flowStatus != null && flowStatus.typeIsSuccess()) {
            return true;
          } else if (flowStatus == null || flowStatus.typeIsNotFinished()) {
            isNotFinshed = true;
          }
        } else {
          // 需要刷新重查记录
          flushExecFlow = true;
          isNotFinshed = true;
        }

      } else {
        // 如果没有依赖纪录也尝试重新获取
        isNotFinshed = true;
        flushExecFlow = true;
      }

      if (isNotFinshed) {
        // 如果没有启动时间, 也没有调度真实时间，直接算超时，如果有就计算超时
        if (checkTimeout(fireTime.getTime(), timeout)) {
          logger.error("Wait for dependence workflow timeout");
          return false;
        }

        try {
          Thread.sleep(checkInterval);
          if (flushExecFlow) {
            executionFlow = flowDao.executionFlowPreDate(flowId, scheduleFireTime);
          }
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
   * 更新 workflow 的执行状态
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
   * 检测是否超时 <p>
   *
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
   * 删除 job <p>
   */
  private void deleteJob(int projectId, int flowId) {
    String jobName = genJobName(flowId);
    String jobGroupName = genJobGroupName(projectId);
    QuartzManager.deleteJob(jobName, jobGroupName);
  }

  /**
   * 发送执行任务到 worker <p>
   */
  private void sendToExecution(ExecutionFlow executionFlow) {
    ExecFlowInfo execFlowInfo = new ExecFlowInfo();
    execFlowInfo.setExecId(executionFlow.getId());

    logger.info("scheduler to execution, exec id:{}, schedule time:{}", executionFlow.getId(),
        executionFlow.getScheduleTime());

    executionFlowQueue.add(execFlowInfo);
  }

  /**
   * 生成 workflow 调度任务名称 <p>
   */
  public static String genJobName(int flowId) {
    StringBuilder builder = new StringBuilder(FLOW_SCHEDULE_JOB_NAME_PRIFIX);
    appendParam(builder, flowId);

    return builder.toString();
  }

  /**
   * 生成 workflow 调度任务组名称 <p>
   */
  public static String genJobGroupName(int projectId) {
    StringBuilder builder = new StringBuilder(FLOW_SCHEDULE_JOB_GROUP_NAME_PRIFIX);
    appendParam(builder, projectId);

    return builder.toString();
  }

  /**
   * 生成参数映射（用于参数传递） <p>
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
   */
  private static void appendParam(StringBuilder builder, Object object) {
    builder.append(NAME_SEPARATOR);
    builder.append(object);
  }
}
