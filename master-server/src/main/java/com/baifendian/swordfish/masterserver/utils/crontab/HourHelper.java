package com.baifendian.swordfish.masterserver.utils.crontab;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;
import com.cronutils.model.field.expression.Always;
import com.cronutils.model.field.expression.Every;
import com.cronutils.model.field.expression.On;
import com.cronutils.model.field.expression.QuestionMark;

/**
 * 小时周期判断工具
 */
public class HourHelper extends CycleHelper {

  public HourHelper(Cron cron) {
    super(cron);
  }

  @Override
  protected ScheduleType getCycle() {
    //匹配UDP 小时调度特征值
    if (minField.getExpression() instanceof On
            && hourField.getExpression() instanceof Every
            && dayOfMonthField.getExpression() instanceof Always
            && dayOfWeekField.getExpression() instanceof QuestionMark
            && monthField.getExpression() instanceof Always) {
      return ScheduleType.HOUR;
    }
    return null;
  }
}
