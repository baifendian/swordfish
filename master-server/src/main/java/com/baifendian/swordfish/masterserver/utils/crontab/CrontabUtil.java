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
package com.baifendian.swordfish.masterserver.utils.crontab;

import static com.baifendian.swordfish.masterserver.utils.crontab.CycleHelperFactory.day;
import static com.baifendian.swordfish.masterserver.utils.crontab.CycleHelperFactory.hour;
import static com.baifendian.swordfish.masterserver.utils.crontab.CycleHelperFactory.min;
import static com.baifendian.swordfish.masterserver.utils.crontab.CycleHelperFactory.month;
import static com.baifendian.swordfish.masterserver.utils.crontab.CycleHelperFactory.week;
import static com.cronutils.model.CronType.QUARTZ;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.quartz.CronExpression;

/**
 * crontab 工具类
 */
public class CrontabUtil {

  private static ZoneId zoneId = ZoneId.of("Asia/Shanghai");

  private static CronParser quartzCronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(QUARTZ));

  /**
   * String crontab 转换成为一个 cron 对象
   *
   * @param crontab
   * @return
   */
  public static Cron parseCron(String crontab) {
    return quartzCronParser.parse(crontab);
  }

  /**
   * 把 String crontab 转换为一个 CronExpression 对象
   *
   * @param crontab
   * @return
   * @throws ParseException
   */
  public static CronExpression parseCronExp(String crontab) throws ParseException {
    return new CronExpression(crontab);
  }

  /**
   * 获取一个 crontab 的周期
   *
   * @param cron
   * @return
   */
  public static ScheduleType getCycle(Cron cron) {
    return min(cron).next(hour(cron)).next(day(cron)).next(week(cron)).next(month(cron)).getCycle();
  }

  /**
   * 根据 crontab 得到调度的类型
   *
   * @param crontab
   * @return
   */
  public static ScheduleType getCycle(String crontab) {
    return getCycle(parseCron(crontab));
  }

  /**
   * 获取某个时间段的调度时间节点
   *
   * @param startTime
   * @param endTime
   * @param cronExpression
   * @return
   */
  public static List<Date> getCycleFireDate(Date startTime, Date endTime, CronExpression cronExpression) {
    List<Date> dateList = new ArrayList<>();

    while (true) {
      startTime = cronExpression.getNextValidTimeAfter(startTime);
      if (startTime.after(endTime)) {
        break;
      }
      dateList.add(startTime);
    }

    return dateList;
  }

  /**
   * 将 date 转为 ZonedDateTime
   *
   * @param date
   * @return
   */
  public static ZonedDateTime parseDateToZdt(Date date) {
    return ZonedDateTime.ofInstant(date.toInstant(), zoneId);
  }

  /**
   * @param zdt
   * @return
   */
  public static Date parseZdtToDate(ZonedDateTime zdt) {
    return Date.from(zdt.toInstant());
  }

  /**
   * 计算上一个周期的时间起始时间和结束时间（起始时间 <= 有效时间 < 结束时间） <p>
   *
   * @param scheduledFireTime
   * @param scheduleType
   * @return 周期的起始和结束时间
   */
  public static Map.Entry<Date, Date> getPreCycleDate(Date scheduledFireTime, ScheduleType scheduleType) {
    // 起始时间
    Calendar scheduleStartTime = Calendar.getInstance();
    scheduleStartTime.setTime(scheduledFireTime);

    // 介绍时间
    Calendar scheduleEndTime = Calendar.getInstance();
    scheduleEndTime.setTime(scheduledFireTime);


    switch (scheduleType) {
      case MINUTE:
        // 上一分钟 ~ 当前分钟的开始
        scheduleStartTime.add(Calendar.MINUTE, -1);
        scheduleStartTime.set(Calendar.SECOND, 0);
        scheduleEndTime.set(Calendar.SECOND, 0);
        break;

      case HOUR:
        // 上一小时 ~ 当前小时的开始
        scheduleStartTime.add(Calendar.HOUR_OF_DAY, -1);
        scheduleStartTime.set(Calendar.MINUTE, 0);
        scheduleStartTime.set(Calendar.SECOND, 0);
        scheduleEndTime.set(Calendar.MINUTE, 0);
        scheduleEndTime.set(Calendar.SECOND, 0);
        break;

      case DAY:
        // 上一天 ~ 当前天的开始
        scheduleStartTime.add(Calendar.DAY_OF_MONTH, -1);
        scheduleStartTime.set(Calendar.HOUR_OF_DAY, 0);
        scheduleStartTime.set(Calendar.MINUTE, 0);
        scheduleStartTime.set(Calendar.SECOND, 0);
        scheduleEndTime.set(Calendar.HOUR_OF_DAY, 0);
        scheduleEndTime.set(Calendar.MINUTE, 0);
        scheduleEndTime.set(Calendar.SECOND, 0);
        break;

      case WEEK:
        // 上一周 ~ 当前周的开始(周一)
        scheduleStartTime.setFirstDayOfWeek(Calendar.MONDAY);
        scheduleEndTime.setFirstDayOfWeek(Calendar.MONDAY);

        scheduleStartTime.add(Calendar.WEEK_OF_YEAR, -1);
        scheduleStartTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        scheduleStartTime.set(Calendar.HOUR_OF_DAY, 0);
        scheduleStartTime.set(Calendar.MINUTE, 0);
        scheduleStartTime.set(Calendar.SECOND, 0);
        scheduleEndTime.setTime(scheduleStartTime.getTime());
        scheduleEndTime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        scheduleEndTime.set(Calendar.HOUR_OF_DAY, 0);
        scheduleEndTime.set(Calendar.MINUTE, 0);
        scheduleEndTime.set(Calendar.SECOND, 0);

        break;

      case MONTH:
        // 上一月
        scheduleStartTime.add(Calendar.MONTH, -1);
        scheduleStartTime.set(Calendar.DAY_OF_MONTH, 1);
        scheduleStartTime.set(Calendar.HOUR_OF_DAY, 0);
        scheduleStartTime.set(Calendar.MINUTE, 0);
        scheduleStartTime.set(Calendar.SECOND, 0);
        scheduleEndTime.setTime(scheduleStartTime.getTime());
        scheduleEndTime.set(Calendar.DAY_OF_MONTH, scheduleEndTime.getActualMaximum(Calendar.DAY_OF_MONTH));
        scheduleEndTime.set(Calendar.HOUR_OF_DAY, 0);
        scheduleEndTime.set(Calendar.MINUTE, 0);
        scheduleEndTime.set(Calendar.SECOND, 0);
        break;

      default:
    }

    return new AbstractMap.SimpleImmutableEntry<>(scheduleStartTime.getTime(), scheduleEndTime.getTime());
  }
}
