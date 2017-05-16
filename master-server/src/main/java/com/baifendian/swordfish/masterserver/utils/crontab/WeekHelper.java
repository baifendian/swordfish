package com.baifendian.swordfish.masterserver.utils.crontab;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;
import com.cronutils.model.field.expression.Always;
import com.cronutils.model.field.expression.Between;
import com.cronutils.model.field.expression.On;
import com.cronutils.model.field.expression.QuestionMark;

/**
 * Created by caojingwei on 2017/5/16.
 */
public class WeekHelper extends CycleHelper {
  public WeekHelper(Cron cron) {
    super(cron);
  }

  @Override
  protected ScheduleType getCycle() {
    if (minField.getExpression() instanceof On
            && hourField.getExpression() instanceof On
            && dayOfMonthField.getExpression() instanceof QuestionMark
            && (dayOfWeekField.getExpression() instanceof Between || dayOfWeekField.getExpression() instanceof On)
            && monthField.getExpression() instanceof Always){
      return ScheduleType.WEEK;
    }
    return null;
  }
}
