package com.baifendian.swordfish.masterserver.utils.crontab;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;
import com.cronutils.model.field.CronField;
import com.cronutils.model.field.CronFieldName;

/**
 * 判断是否属于某个周期
 */
public abstract class CycleHelper {

  protected Cron cron;

  protected CronField minField;
  protected CronField hourField;
  protected CronField dayOfMonthField;
  protected CronField dayOfWeekField;
  protected CronField monthField;
  protected CronField yearField;

  public Next next(CycleHelper cycleHelper) {
    return new Next(this.cron).next(this).next(cycleHelper);
  }

  public CycleHelper(Cron cron) {
    if (cron == null) {
      throw new IllegalArgumentException("cron must not be null!");
    }
    this.cron = cron;
    this.minField = cron.retrieve(CronFieldName.MINUTE);
    this.hourField = cron.retrieve(CronFieldName.HOUR);
    this.dayOfMonthField = cron.retrieve(CronFieldName.DAY_OF_MONTH);
    this.dayOfWeekField = cron.retrieve(CronFieldName.DAY_OF_WEEK);
    this.monthField = cron.retrieve(CronFieldName.MONTH);
    this.yearField = cron.retrieve(CronFieldName.YEAR);
  }

  /**
   * 获取调度级别
   *
   * @return
   */
  protected abstract ScheduleType getCycle();

}
