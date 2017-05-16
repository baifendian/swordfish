package com.baifendian.swordfish.masterserver.utils.crontab;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;

/**
 * Created by caojingwei on 2017/5/15.
 */
public class SecHelper extends CycleHelper {

  public SecHelper(Cron cron) {
    super(cron);
  }

  @Override
  protected ScheduleType getCycle() {
    //TODO 先忽律秒周期
    return null;
  }
}
