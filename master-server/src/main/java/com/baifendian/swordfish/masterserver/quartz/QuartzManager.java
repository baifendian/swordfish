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

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import com.baifendian.swordfish.masterserver.exception.QuartzException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quartz 调度管理 <p>
 */
public class QuartzManager {

  /**
   * logger
   */
  private static final Logger logger = LoggerFactory.getLogger(QuartzManager.class);

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
  private static SchedulerFactory schedulerFactory;

  /**
   * {@link Scheduler}
   */
  private static Scheduler scheduler;

  static {
    try {
      schedulerFactory = new StdSchedulerFactory(/*"quartz.properties"*/);
      scheduler = schedulerFactory.getScheduler();
    } catch (SchedulerException e) {
      logger.error(e.getMessage(), e);
      System.exit(1);
    }
  }

  /**
   * 启动所有定时任务（程序启动时调用） <p>
   */
  public static void start() throws SchedulerException {
    if (scheduler == null) {
      throw new QuartzException("Scheduler init failed, please init again!");
    }

    logger.info("start scheduler");

    scheduler.start();
  }

  /**
   * 关闭所有定时任务（程序shutdown时调用）
   */
  public static void shutdown() throws SchedulerException {
    if (scheduler != null && !scheduler.isShutdown()) {
      logger.info("shutdown scheduler");

      // 不等待任务执行完成
      scheduler.shutdown();
    }
  }
//
//  /**
//   * 添加对象到上下文中（若已存在相同 key，则覆盖）
//   */
//  public static void putObjectToContext(String key, Object object) {
//    if (scheduler == null) {
//      throw new QuartzException("Scheduler init failed, please init again!");
//    }
//
//    try {
//      scheduler.getContext().put(key, object);
//    } catch (SchedulerException e) {
//      logger.error(e.getMessage(), e);
//      throw new QuartzException("Add object to context failed", e);
//    }
//  }

  /**
   * 添加一个任务和触发器（若已经存在，则直接返回任务，并更新任务的触发器） <p>
   *
   * @param jobName 任务名
   * @param jobGroupName 任务组名
   * @param jobClass 任务类
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @param cronExpression "cron 表达式"
   * @param dataMap 传递的数据
   * @see JobDetail
   */
  public static JobDetail addJobAndTrigger(String jobName,
      String jobGroupName,
      Class<? extends Job> jobClass,
      Date startDate, Date endDate,
      String cronExpression,
      Map<String, Object> dataMap) {
    checkStatus();

    try {
      JobDetail jobDetail = addJob(jobName, jobGroupName, jobClass, dataMap);

      // 这里触发器名和任务名一致，保证唯一性
      addCronTrigger(jobDetail, DEFAULT_TRIGGER_GROUP_NAME, jobName, startDate, endDate,
          cronExpression, true);

      return jobDetail;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new QuartzException("Add schedule Job failed", e);
    }
  }

  /**
   * 添加一个任务（若已经存在，则直接返回） <p>
   *
   * @param jobName 任务名
   * @param jobGroupName 任务组名
   * @param jobClass 任务类
   * @param dataMap 传递的数据
   * @see JobDetail
   */
  private static JobDetail addJob(String jobName, String jobGroupName,
      Class<? extends Job> jobClass, Map<String, Object> dataMap) {
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

          logger.info("Add job, job name: {}, group name: {}",
              jobName, jobGroupName);
        }
      }

      return jobDetail;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new QuartzException("Add schedule Job failed", e);
    }
  }
//
//  /**
//   * 给指定 Job 添加一个 "cron 表达式" 的 触发器 <p>
//   *
//   * @param jobDetail {@link JobDetail}
//   * @param triggerName 触发器名
//   * @param startDate 开始日期
//   * @param endDate 结束日期
//   * @param cronExpression "cron 表达式"
//   * @param replace 如果已经存在，则是否替换
//   */
//  public static void addCronTrigger(JobDetail jobDetail, String triggerName, Date startDate,
//      Date endDate, String cronExpression, boolean replace) {
//    checkStatus();
//
//    addCronTrigger(jobDetail, DEFAULT_TRIGGER_GROUP_NAME, triggerName, startDate, endDate,
//        cronExpression, replace);
//  }

  /**
   * 给指定 Job 添加一个 "cron 表达式" 的 触发器 <p>
   *
   * @param jobDetail {@link JobDetail}
   * @param triggerName 触发器名
   * @param triggerGroupName 触发器组名
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @param cronExpression "cron 表达式"
   * @param replace 如果已经存在，则是否替换
   */
  private static void addCronTrigger(JobDetail jobDetail,
      String triggerName,
      String triggerGroupName,
      Date startDate,
      Date endDate,
      String cronExpression,
      boolean replace) {
    TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);

    try {
      synchronized (SYN_OBJ) {
        CronTrigger trigger = newTrigger().withIdentity(triggerKey).startAt(startDate)
            .endAt(endDate)
            .withSchedule(cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing())
            .forJob(jobDetail).build();

        if (scheduler.checkExists(triggerKey)) {
          if (replace) { // 允许替换的情况
            // 仅在调度周期发生改变时，才更新调度
            CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            String oldCronExpression = oldTrigger.getCronExpression();

            if (!oldCronExpression.equalsIgnoreCase(cronExpression)) {
              // 重启触发器
              scheduler.rescheduleJob(triggerKey, trigger);
              logger.info(
                  "RescheduleJob trigger, trigger name: {}, group name: {}, crontab: {}, startDate: {}, endDate: {}",
                  triggerName, triggerGroupName, cronExpression, startDate, endDate);
            }
          } else {
            throw new QuartzException(
                "Add schedule CronTrigger failed: exist the same CronTrigger");
          }
        } else {
          // 触发器
          logger.info(
              "Add trigger, trigger name: {}, group name: {}, crontab: {}, startDate: {}, endDate: {}",
              triggerName, triggerGroupName, cronExpression, startDate, endDate);
          scheduler.scheduleJob(trigger);
        }
      }
    } catch (SchedulerException e) {
      logger.error(e.getMessage(), e);
      throw new QuartzException("Add schedule CronTrigger failed", e);
    }
  }
//
//  /**
//   * 删除一个 Job(使用默认的任务组名)
//   */
//  public static void deleteJob(String jobName) {
//    try {
//      scheduler.deleteJob(new JobKey(jobName, DEFAULT_JOB_GROUP_NAME));// 删除任务
//    } catch (SchedulerException e) {
//      logger.error(e.getMessage(), e);
//      throw new QuartzException("Delete Job failed", e);
//    }
//  }

  /**
   * 删除一个 Job
   */
  public static void deleteJob(String jobName, String jobGroupName) {
    try {
      // 删除任务
      scheduler.deleteJob(new JobKey(jobName, jobGroupName));
      logger.info("delete job, job group: {}, job name: {}", jobGroupName, jobName);
    } catch (SchedulerException e) {
      logger.error(e.getMessage(), e);
      throw new QuartzException("Delete Job failed", e);
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
      logger.info("delete group job, job group: {}", jobGroupName);
    } catch (SchedulerException e) {
      logger.error(e.getMessage(), e);
      throw new QuartzException("Delete Job failed", e);
    }
  }
//
//  /**
//   * 暂停一个 Trigger (使用默认的触发器组名) <p>
//   */
//  public static void pauseTrigger(String triggerName) {
//    TriggerKey triggerKey = new TriggerKey(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
//
//    try {
//      // 终止 trigger
//      scheduler.pauseTrigger(triggerKey);
//    } catch (SchedulerException e) {
//      logger.error(e.getMessage(), e);
//      throw new QuartzException("Delete Job failed", e);
//    }
//  }
//
//  /**
//   * 重置一个 Trigger (使用默认的触发器组名) <p>
//   */
//  public static void resumeTrigger(String triggerName) {
//    TriggerKey triggerKey = new TriggerKey(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
//
//    try {
//      // 终止 trigger
//      scheduler.resumeTrigger(triggerKey);
//    } catch (SchedulerException e) {
//      logger.error(e.getMessage(), e);
//      throw new QuartzException("Delete Job failed", e);
//    }
//  }
//
//  /**
//   * 删除一个 Trigger (使用默认的触发器组名) <p>
//   */
//  public static void deleteTrigger(String triggerName) {
//    TriggerKey triggerKey = new TriggerKey(triggerName, DEFAULT_TRIGGER_GROUP_NAME);
//
//    try {
//      // 终止 trigger
//      scheduler.pauseTrigger(triggerKey);
//      // 移除 trigger
//      scheduler.unscheduleJob(triggerKey);
//    } catch (SchedulerException e) {
//      logger.error(e.getMessage(), e);
//      throw new QuartzException("Delete Job failed", e);
//    }
//  }

  /**
   * 判断当前调度器的状态 <p>
   */
  private static void checkStatus() {
    if (scheduler == null) {
      logger.error("Scheduler init failed, please init again!");
      throw new QuartzException("Scheduler init failed, please init again!");
    }

    try {
      if (scheduler.isShutdown()) {
        logger.error("Scheduler had shutdown!");
        throw new QuartzException("Scheduler had shutdown!");
      }
    } catch (SchedulerException e) {
      logger.error("Schedule exception!", e);
      throw new QuartzException("Schedule exception!", e);
    }
  }
}
