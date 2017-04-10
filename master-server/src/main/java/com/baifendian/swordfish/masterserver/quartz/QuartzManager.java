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

import com.baifendian.swordfish.masterserver.exception.QuartzException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Quartz 调度管理 <p>
 */
public class QuartzManager {

  /**
   * LOGGER
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(QuartzManager.class);

  /**
   * 默认的 Job Group 名称
   */
  private static final String DEFAULT_JOB_GROUP_NAME = "DEFAULT_JOBGROUP_NAME";

  /**
   * 默认的 Trigger Group 名称
   */
  private static final String DEFAULT_TRIGGER_GROUP_NAME = "DEFAULT_TRIGGERGROUP_NAME";

  /**
   * 同步对象
   */
  private static final Object SYN_OBJ = new Object();

  /**
   * {@link SchedulerFactory}
   */
  private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();

  /**
   * {@link Scheduler}
   */
  private static Scheduler scheduler;

  static {
    try {
      scheduler = schedulerFactory.getScheduler();
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  /**
   * 启动所有定时任务（程序启动时调用） <p>
   */
  public static void start() throws SchedulerException {
    if (scheduler == null) {
      throw new QuartzException("调度器初始化失败，请重新初始化！");
    }
    scheduler.start();
  }

  /**
   * 关闭所有定时任务（程序shutdown时调用）
   */
  public static void shutdown() throws SchedulerException {
    if (scheduler != null && !scheduler.isShutdown()) {
      scheduler.shutdown(); // 不等待任务执行完成
    }
  }

  /**
   * 添加对象到上下文中（若已存在相同key，则覆盖） <p>
   */
  public static void putObjectToContext(String key, Object object) {
    if (scheduler == null) {
      throw new QuartzException("调度器初始化失败，请重新初始化！");
    }
    try {
      scheduler.getContext().put(key, object);
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
      throw new QuartzException("添加对象到上下文失败", e);
    }
  }

  /**
   * 添加一个任务和触发器（若已经存在，则直接返回任务，并更新任务的触发器） <p>
   *
   * @param jobName        任务名
   * @param jobGroupName   任务组名
   * @param jobClass       任务类
   * @param startDate      开始日期
   * @param endDate        结束日期
   * @param cronExpression "cron 表达式"
   * @param dataMap        传递的数据
   * @return {@link JobDetail}
   */
  public static JobDetail addJobAndTrigger(String jobName, String jobGroupName, Class<? extends Job> jobClass, Date startDate, Date endDate, String cronExpression,
                                           Map<String, Object> dataMap) {
    checkStatus();

    try {
      JobDetail jobDetail = addJob(jobName, jobGroupName, jobClass, dataMap);
      // 这里触发器名和任务名一致，保证唯一性
      addCronTrigger(jobDetail, DEFAULT_TRIGGER_GROUP_NAME, jobName, startDate, endDate, cronExpression, true);

      return jobDetail;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new QuartzException("添加调度 Job 失败", e);
    }
  }

  /**
   * 添加一个任务（若已经存在，则直接返回） <p>
   *
   * @param jobName      任务名
   * @param jobGroupName 任务组名
   * @param jobClass     任务类
   * @param dataMap      传递的数据
   * @return {@link JobDetail}
   */
  private static JobDetail addJob(String jobName, String jobGroupName, Class<? extends Job> jobClass, Map<String, Object> dataMap) {
    checkStatus();

    try {
      JobKey jobKey = new JobKey(jobName, jobGroupName);
      JobDetail jobDetail;
      synchronized (SYN_OBJ) {
        if (scheduler.checkExists(jobKey)) {
          jobDetail = scheduler.getJobDetail(jobKey);
          if (dataMap != null) {
            jobDetail.getJobDataMap().putAll(dataMap);
          }
        } else {
          jobDetail = newJob(jobClass).withIdentity(jobKey).build();
          if (dataMap != null) {
            jobDetail.getJobDataMap().putAll(dataMap);
          }
          scheduler.addJob(jobDetail, false, true);
        }
      }
      return jobDetail;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new QuartzException("添加调度 Job 失败", e);
    }
  }

  /**
   * 给指定 Job 添加一个 "cron 表达式" 的 触发器 <p>
   *
   * @param jobDetail      {@link JobDetail}
   * @param triggerName    触发器名
   * @param startDate      开始日期
   * @param endDate        结束日期
   * @param cronExpression "cron 表达式"
   * @param replace        如果已经存在，则是否替换
   */
  public static void addCronTrigger(JobDetail jobDetail, String triggerName, Date startDate, Date endDate, String cronExpression, boolean replace) {
    checkStatus();

    addCronTrigger(jobDetail, DEFAULT_TRIGGER_GROUP_NAME, triggerName, startDate, endDate, cronExpression, replace);
  }

  /**
   * 给指定 Job 添加一个 "cron 表达式" 的 触发器 <p>
   *
   * @param jobDetail        {@link JobDetail}
   * @param triggerName      触发器名
   * @param triggerGroupName 触发器组名
   * @param startDate        开始日期
   * @param endDate          结束日期
   * @param cronExpression   "cron 表达式"
   * @param replace          如果已经存在，则是否替换
   */
  private static void addCronTrigger(JobDetail jobDetail, String triggerName, String triggerGroupName, Date startDate, Date endDate, String cronExpression, boolean replace) {
    TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
    try {
      synchronized (SYN_OBJ) {
        CronTrigger trigger = newTrigger().withIdentity(triggerKey).startAt(startDate).endAt(endDate).withSchedule(cronSchedule(cronExpression)).forJob(jobDetail).build();
        if (scheduler.checkExists(triggerKey)) {
          if (replace) { // 允许替换的情况
            // 仅在调度周期发生改变时，才更新调度
            CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            String oldCronExpression = oldTrigger.getCronExpression();
            if (!oldCronExpression.equalsIgnoreCase(cronExpression)) {
              // 重启触发器
              scheduler.rescheduleJob(triggerKey, trigger);
            }
          } else {
            throw new QuartzException("添加调度 CronTrigger 失败：存在相同的CronTrigger");
          }
        } else {
          // 触发器
          scheduler.scheduleJob(trigger);
        }
      }
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
      throw new QuartzException("添加调度 CronTrigger 失败", e);
    }
  }

  /**
   * 删除一个 Job(使用默认的任务组名)
   */
  public static void deleteJob(String jobName) {
    try {
      scheduler.deleteJob(new JobKey(jobName, DEFAULT_JOB_GROUP_NAME));// 删除任务
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
      throw new QuartzException("删除 Job 失败", e);
    }
  }

  /**
   * 删除一个 Job
   */
  public static void deleteJob(String jobName, String jobGroupName) {
    try {
      scheduler.deleteJob(new JobKey(jobName, jobGroupName));// 删除任务
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
      throw new QuartzException("删除 Job 失败", e);
    }
  }

  /**
   * 删除一个 Job Group 的所有任务
   */
  public static void deleteJobs(String jobGroupName) {
    try {
      List<JobKey> jobKeys = new ArrayList<>();
      jobKeys.addAll(scheduler.getJobKeys(GroupMatcher.groupEndsWith(jobGroupName)));
      scheduler.deleteJobs(jobKeys);
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
      throw new QuartzException("删除 Job 失败", e);
    }
  }

  /**
   * 暂停一个 Trigger (使用默认的触发器组名) <p>
   */
  public static void pauseTrigger(String triggerName) {
    TriggerKey triggerKey = new TriggerKey(triggerName, DEFAULT_TRIGGER_GROUP_NAME);

    try {
      scheduler.pauseTrigger(triggerKey);// 终止 trigger
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
      throw new QuartzException("删除 Job 失败", e);
    }
  }

  /**
   * 重置一个 Trigger (使用默认的触发器组名) <p>
   */
  public static void resumeTrigger(String triggerName) {
    TriggerKey triggerKey = new TriggerKey(triggerName, DEFAULT_TRIGGER_GROUP_NAME);

    try {
      scheduler.resumeTrigger(triggerKey);// 终止 trigger
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
      throw new QuartzException("删除 Job 失败", e);
    }
  }

  /**
   * 删除一个 Trigger (使用默认的触发器组名) <p>
   */
  public static void deleteTrigger(String triggerName) {
    TriggerKey triggerKey = new TriggerKey(triggerName, DEFAULT_TRIGGER_GROUP_NAME);

    try {
      scheduler.pauseTrigger(triggerKey);// 终止 trigger
      scheduler.unscheduleJob(triggerKey);// 移除 trigger
    } catch (SchedulerException e) {
      LOGGER.error(e.getMessage(), e);
      throw new QuartzException("删除 Job 失败", e);
    }
  }

  /**
   * 判断当前调度器的状态 <p>
   */
  private static void checkStatus() {
    if (scheduler == null) {
      throw new QuartzException("调度器初始化失败，请重新初始化！");
    }
    try {
      if (scheduler.isShutdown()) {
        throw new QuartzException("调度器已经 shutdown ！");
      }
    } catch (SchedulerException e) {
      throw new QuartzException("调度异常 ！", e);
    }
  }

}
