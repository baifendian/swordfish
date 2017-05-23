/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.masterserver.utils.crontab;

import com.baifendian.swordfish.dao.enums.ScheduleType;
import com.cronutils.model.Cron;
import com.cronutils.model.field.expression.*;

/**
 * 周周期判断工具类
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
            && (dayOfWeekField.getExpression() instanceof And || dayOfWeekField.getExpression() instanceof On)
            && monthField.getExpression() instanceof Always){
      return ScheduleType.WEEK;
    }
    return null;
  }
}
