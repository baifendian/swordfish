package com.baifendian.swordfish.masterserver.utils.crontab;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caojingwei on 2017/5/16.
 */
public class Next extends CycleHelper {
  private final List<CycleHelper> cycleHelperList = new ArrayList<>();

  public Next(Cron cron) {
    super(cron);
  }

  public Next next(CycleHelper cycleHelper) {
    cycleHelperList.add(cycleHelper);
    return this;
  }

  @Override
  protected ScheduleType getCycle() {
    for (CycleHelper cycleHelper : cycleHelperList) {
      ScheduleType level = cycleHelper.getCycle();
      if (level != null) {
        return level;
      }
    }
    return null;
  }
}
