package com.baifendian.swordfish.masterserver.utils.crontab;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.baifendian.swordfish.dao.model.Schedule;
import com.cronutils.model.Cron;
import org.junit.Test;

import static com.baifendian.swordfish.masterserver.utils.crontab.CrontabUtil.*;
import static junit.framework.TestCase.assertEquals;

/**
 * 调度工具类单元测试
 */
public class TestCrontabUtil {
  @Test
  public void testGetCycle(){
    //准备数据
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

    assertEquals(minType,ScheduleType.MINUTE);
    assertEquals(hourType,ScheduleType.HOUR);
    assertEquals(dayType,ScheduleType.DAY);
    assertEquals(weekType1,ScheduleType.WEEK);
    assertEquals(weekType2,ScheduleType.WEEK);
    assertEquals(monthType1,ScheduleType.MONTH);
    assertEquals(monthType2,ScheduleType.MONTH);
  }
}
