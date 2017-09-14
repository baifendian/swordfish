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

import static com.baifendian.swordfish.masterserver.utils.crontab.CrontabUtil.getCycle;
import static com.baifendian.swordfish.masterserver.utils.crontab.CrontabUtil.parseCron;
import static junit.framework.TestCase.assertEquals;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.quartz.CronExpression;

/**
 * 调度工具类单元测试
 */
public class TestCrontabUtil {
  @Test
  public void testGetCycle() {
    // 准备数据
    String minCrontab = "0 0/2 0-23 * * ? *";
    String hourCrontab = "0 0 2-22/2 * * ? *";
    String dayCrontab = "0 0 0 * * ? *";
    String weekCrontab1 = "0 0 0 ? * SUN,SAT *";
    String weekCrontab2 = "0 0 0 ? * SUN *";
    String monthCrontab1 = "0 0 0 1 * ? *";
    String monthCrontab2 = "0 0 0 1,3 * ? *";

    Cron minCron = parseCron(minCrontab);
    Cron hourCron = parseCron(hourCrontab);
    Cron dayCron = parseCron(dayCrontab);
    Cron weekCron1 = parseCron(weekCrontab1);
    Cron weekCron2 = parseCron(weekCrontab2);
    Cron monthCron1 = parseCron(monthCrontab1);
    Cron monthCron2 = parseCron(monthCrontab2);

    ScheduleType minType = getCycle(minCron);
    ScheduleType hourType = getCycle(hourCron);
    ScheduleType dayType = getCycle(dayCron);
    ScheduleType weekType1 = getCycle(weekCron1);
    ScheduleType weekType2 = getCycle(weekCron2);
    ScheduleType monthType1 = getCycle(monthCron1);
    ScheduleType monthType2 = getCycle(monthCron2);

    assertEquals(minType, ScheduleType.MINUTE);
    assertEquals(hourType, ScheduleType.HOUR);
    assertEquals(dayType, ScheduleType.DAY);
    assertEquals(weekType1, ScheduleType.WEEK);
    assertEquals(weekType2, ScheduleType.WEEK);
    assertEquals(monthType1, ScheduleType.MONTH);
    assertEquals(monthType2, ScheduleType.MONTH);
  }

  @Test
  public void testGetCycleFireDate() throws ParseException {
    //准备数据
    String hourCrontab = "0 0 2-22/2 * * ? *";
    CronExpression cronExpression = CrontabUtil.parseCronExp(hourCrontab);
    Date startTime = new Date(1495555200000L);
    Date endTime = new Date(1495814400000L);

    List<Date> dateList = CrontabUtil.getCycleFireDate(startTime, endTime, cronExpression);

    for (Date date : dateList) {
      System.out.println(date);
    }
  }

  @Test
  public void testGetPreCycleDate() {
    Date now = new Date();
    System.out.println("now: " + now);

    Map.Entry<Date, Date> minMap = CrontabUtil.getPreCycleDate(now, ScheduleType.MINUTE);
    System.out.println("min level is " + minMap.getKey() + " - " + minMap.getValue());

    Map.Entry<Date, Date> hourMap = CrontabUtil.getPreCycleDate(now, ScheduleType.HOUR);
    System.out.println("hour level is " + hourMap.getKey() + " - " + hourMap.getValue());

    Map.Entry<Date, Date> dayMap = CrontabUtil.getPreCycleDate(now, ScheduleType.DAY);
    System.out.println("day level is " + dayMap.getKey() + " - " + dayMap.getValue());

    Map.Entry<Date, Date> weekMap = CrontabUtil.getPreCycleDate(now, ScheduleType.WEEK);
    System.out.println("week level is " + weekMap.getKey() + " - " + weekMap.getValue());

    Map.Entry<Date, Date> monthMap = CrontabUtil.getPreCycleDate(now, ScheduleType.MONTH);
    System.out.println("month level is " + monthMap.getKey() + " - " + monthMap.getValue());
  }
}
