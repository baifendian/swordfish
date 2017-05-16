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
