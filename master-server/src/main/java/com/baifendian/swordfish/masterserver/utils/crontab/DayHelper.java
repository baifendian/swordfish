package com.baifendian.swordfish.masterserver.utils.crontab;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;
import com.cronutils.model.field.expression.Always;
import com.cronutils.model.field.expression.On;
import com.cronutils.model.field.expression.QuestionMark;

/**
 * Created by caojingwei on 2017/5/15.
 */
public class DayHelper extends CycleHelper {

  public DayHelper(Cron cron) {
    super(cron);
  }

  @Override
  protected ScheduleType getCycle() {
    //匹配UDP天调度特征
    if (minField.getExpression() instanceof On
            && hourField.getExpression() instanceof On
            && dayOfMonthField.getExpression() instanceof Always
            && dayOfWeekField.getExpression() instanceof QuestionMark
            && monthField.getExpression() instanceof Always) {
      return ScheduleType.DAY;
    }
    return null;
  }
}
