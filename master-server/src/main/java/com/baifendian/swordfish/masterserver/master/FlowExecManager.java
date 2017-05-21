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

import com.baifendian.swordfish.common.mail.EmailManager;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 工作流的执行管理
 */
public class FlowExecManager {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * {@link ExecutorService}
   */
  private final ExecutorService appendFlowExecutorService;

  /**
   * execution flow queue
   **/
  private final JobExecManager jobExecManager;

  /**
   * {@link FlowDao}
   */
  private final FlowDao flowDao;

  /**
   * 检测依赖的等待时间，默认 30 s
   */
  private static long checkInterval = 30 * 1000;


  /**
   * @param jobExecManager
   * @param flowDao
   */
  public FlowExecManager(JobExecManager jobExecManager, FlowDao flowDao) {
    this.jobExecManager = jobExecManager;
    this.flowDao = flowDao;

    ThreadFactory flowThreadFactory = new ThreadFactoryBuilder().setNameFormat("Scheduler-JobExecManager-AddData").build();
    appendFlowExecutorService = Executors.newCachedThreadPool(flowThreadFactory);
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
    // 提交任务去执行
    appendFlowExecutorService.submit(() -> {
      Date scheduleDate = cron.getTimeAfter(DateUtils.addSeconds(startDateTime, -1));

      try {
        // 是否已经失败
        boolean isFailed = false;
        List<Map.Entry<Date, Boolean>> resultList = new ArrayList<>();

        while (scheduleDate.before(endDateTime) || scheduleDate.equals(endDateTime)) {
          Boolean execStatus = null;

          if (!isFailed) {
            // 插入 ExecutionFlow
            Schedule schedule = flowDao.querySchedule(flow.getId());

            // 默认最大重试 2 次, 即最多运行 3 次
            Integer maxTryTimes = 2;
            Integer timeout = 10 * 3600;

            if (schedule != null) {
              maxTryTimes = schedule.getMaxTryTimes();
              timeout = schedule.getTimeout();
            }

            ExecutionFlow executionFlow = flowDao.scheduleFlowToExecution(flow.getProjectId(), flow.getId(),
                flow.getOwnerId(), scheduleDate, ExecType.COMPLEMENT_DATA, maxTryTimes, null, null, schedule.getNotifyType(), schedule.getNotifyMails(), timeout);
            executionFlow.setProjectId(flow.getProjectId());

            ExecFlowInfo execFlowInfo = new ExecFlowInfo();
            execFlowInfo.setExecId(executionFlow.getId());

            // 发送请求到 executor server 中执行
            jobExecManager.addExecFlow(execFlowInfo);

            // 如果当前任务补数据任务失败，后续任务不再执行
            execStatus = checkExecStatus(executionFlow.getId());
            if (!execStatus) {
              isFailed = true;
            }
          }

          resultList.add(new AbstractMap.SimpleImmutableEntry<>(new Date(scheduleDate.getTime()), execStatus));
          scheduleDate = cron.getTimeAfter(scheduleDate);
        }

        // 发送邮件
        EmailManager.sendAddDataEmail(flow, !isFailed, resultList);
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    });
  }

  /**
   * 检测 workflow 的执行状态 <p>
   *
   * @param execId
   * @return 是否成功
   */
  private boolean checkExecStatus(int execId) {
    while (true) {
      try {
        Thread.sleep(checkInterval);
      } catch (InterruptedException e) {
        logger.error(e.getMessage(), e);
        return false;
      }

      ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
      if (executionFlow.getStatus().typeIsSuccess()) {
        return true;
      } else if (executionFlow.getStatus().typeIsFinished()) {
        return false;
      }
    }
  }

  /**
   * 销毁资源 <p>
   */
  public void destroy() {
    if (!appendFlowExecutorService.isShutdown()) {
      try {
        appendFlowExecutorService.shutdownNow();
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
  }
}
