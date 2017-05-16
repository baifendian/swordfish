package com.baifendian.swordfish.masterserver.utils.crontab;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static com.cronutils.model.CronType.QUARTZ;
import static com.baifendian.swordfish.masterserver.utils.crontab.CycleHelperFactory.*;

/**
 * crontab 工具类
 */
public class CrontabUtil {


  private static ZoneId zoneId = ZoneId.of("Asia/Shanghai");
  private static CronParser quartzCronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(QUARTZ));

  /**
   * String crontab转换成为一个cron对象
   *
   * @param crontab
   * @return
   */
  public static Cron parseCron(String crontab) {
    return quartzCronParser.parse(crontab);
  }

  /**
   * 将一个String crontab转化为一个CronExpression对象
   *
   * @param crontab
   * @return
   */
  public static CronExpression parseCronExp(String crontab) {
    CronExpression cronExpression = null;
    try {
      cronExpression = new CronExpression(crontab);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return cronExpression;
  }

  /**
   * 获取一个crontab的周期
   *
   * @param cron
   * @return
   */
  public static ScheduleType getCycle(Cron cron) {
    return min(cron).next(hour(cron)).next(day(cron)).next(week(cron)).next(month(cron)).getCycle();
  }

  public static ScheduleType getCycle(String crontab) {
    return getCycle(parseCron(crontab));
  }

  /**
   * 获取某个时间段的
   *
   * @param startTime
   * @param endTime
   * @param crontab
   * @return
   */
  public static List<Date> getTimeZoneFireDate(Date startTime, Date endTime, String crontab) throws ParseException {
    List<Date> dateList = new ArrayList<>();

    CronExpression cronExpression = new CronExpression(crontab);

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
   * 将date转为ZonedDateTime
   *
   * @param date
   * @return
   */
  public static ZonedDateTime parseDateToZdt(Date date) {
    return ZonedDateTime.ofInstant(date.toInstant(), zoneId);
  }

  public static Date parseZdtToDate(ZonedDateTime zdt) {
    return Date.from(zdt.toInstant());
  }

  /**
   * 计算上一个周期的时间起始时间和结束时间（起始时间 <= 有效时间 < 结束时间） <p>
   *
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
        scheduleEndTime.set(Calendar.SECOND, 0);
        break;

      case HOUR:
        // 上一小时 ~ 当前小时的开始
        scheduleStartTime.add(Calendar.HOUR_OF_DAY, -1);
        scheduleEndTime.set(Calendar.MINUTE, 0);
        scheduleEndTime.set(Calendar.SECOND, 0);
        break;

      case DAY:
        // 上一天 ~ 当前天的开始
        scheduleStartTime.add(Calendar.DAY_OF_MONTH, -1);
        scheduleEndTime.set(Calendar.HOUR_OF_DAY, 0);
        scheduleEndTime.set(Calendar.MINUTE, 0);
        scheduleEndTime.set(Calendar.SECOND, 0);
        break;

      case WEEK:
        // 上一周 ~ 当前周的开始(周一)
        scheduleStartTime.add(Calendar.WEEK_OF_YEAR, -1);
        scheduleEndTime.setTime(scheduleStartTime.getTime());
        scheduleEndTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        scheduleEndTime.set(Calendar.HOUR_OF_DAY, 0);
        scheduleEndTime.set(Calendar.MINUTE, 0);
        scheduleEndTime.set(Calendar.SECOND, 0);

        break;

      case MONTH:
        // 上一月
        scheduleStartTime.add(Calendar.MONTH, -1);
        scheduleEndTime.setTime(scheduleStartTime.getTime());
        scheduleEndTime.set(Calendar.DAY_OF_MONTH, 1);
        scheduleEndTime.set(Calendar.HOUR_OF_DAY, 0);
        scheduleEndTime.set(Calendar.MINUTE, 0);
        scheduleEndTime.set(Calendar.SECOND, 0);
        break;

      default:
        break;
    }
    return new AbstractMap.SimpleImmutableEntry<Date, Date>(scheduleStartTime.getTime(), scheduleEndTime.getTime());
  }
}
