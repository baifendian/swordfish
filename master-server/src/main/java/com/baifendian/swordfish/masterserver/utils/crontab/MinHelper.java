package com.baifendian.swordfish.masterserver.utils.crontab;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;
import com.cronutils.model.field.CronField;
import com.cronutils.model.field.CronFieldName;
import com.cronutils.model.field.expression.Always;
import com.cronutils.model.field.expression.Between;
import com.cronutils.model.field.expression.Every;
import com.cronutils.model.field.expression.FieldExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 判断是否是分钟周期
 */
public class MinHelper extends CycleHelper {


  private static final Logger logger = LoggerFactory.getLogger(MinHelper.class);

  public MinHelper(Cron cron) {
    super(cron);
  }

  @Override
  protected ScheduleType getCycle() {
    //匹配UDP 分钟周期特征
    if (minField.getExpression() instanceof Every
            && hourField.getExpression() instanceof Between
            && dayOfMonthField.getExpression() instanceof Always
            && monthField.getExpression() instanceof Always) {
      return ScheduleType.MINUTE;
    }
    return null;
  }
}
