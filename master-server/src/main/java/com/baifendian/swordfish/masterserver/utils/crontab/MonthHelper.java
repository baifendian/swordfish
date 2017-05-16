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
public class MonthHelper extends CycleHelper {

  public MonthHelper(Cron cron) {
    super(cron);
  }

  @Override
  protected ScheduleType getCycle() {
    //根据UDP的月周期特征进行匹配
    if (minField.getExpression() instanceof On
            && hourField.getExpression() instanceof On
            && (dayOfMonthField.getExpression() instanceof On || dayOfMonthField.getExpression() instanceof Between)
            && dayOfWeekField.getExpression() instanceof QuestionMark
            && minField.getExpression() instanceof Always) {
      return ScheduleType.MONTH;
    }
    return null;
  }
}
