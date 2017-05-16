package com.baifendian.swordfish.masterserver.utils.crontab;

import com.cronutils.model.Cron;

/**
 * crontab周期工具类工厂
 */
public class CycleHelperFactory {

  public static CycleHelper min(Cron cron) {
    return new MinHelper(cron);
  }

  public static CycleHelper hour(Cron cron) {
    return new HourHelper(cron);
  }

  public static CycleHelper day(Cron cron) {
    return new DayHelper(cron);
  }

  public static CycleHelper week(Cron cron) {
    return new WeekHelper(cron);
  }

  public static CycleHelper month(Cron cron) {
    return new MonthHelper(cron);
  }
}
