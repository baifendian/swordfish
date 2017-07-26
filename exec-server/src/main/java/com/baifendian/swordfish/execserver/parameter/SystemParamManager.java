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
package com.baifendian.swordfish.execserver.parameter;

import static com.baifendian.swordfish.common.utils.DateUtils.format;
import static org.apache.commons.lang.time.DateUtils.addDays;

import com.baifendian.swordfish.dao.enums.ExecType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * 系统参数管理 <p>
 */
public class SystemParamManager {

  /**
   * yyyyMMdd
   */
  public static final String DATE_FORMAT = "yyyyMMdd";

  /**
   * yyyyMMddHHmmss
   */
  public static final String TIME_FORMAT = "yyyyMMddHHmmss";

  /**
   * 日常调度实例定时的定时时间日期的前一天，格式为 yyyyMMdd
   */
  public static final String BIZ_DATE = "sf.system.bizdate";

  /**
   * 日常调度实例定时的定时时间日期，格式为 yyyymmdd，取值为 ${sf.system.bizdate} + 1
   */
  public static final String BIZ_CUR_DATE = "sf.system.bizcurdate";

  /**
   * 格式 yyyyMMddHHmmss，表示的是日常调度实例定时时间（年月日时分秒）
   */
  public static final String CYC_TIME = "sf.system.cyctime";

  /**
   * 执行 id
   */
  public static final String EXEC_ID = "sf.system.execId";

  /**
   * 日志 id
   */
  public static final String JOB_ID = "sf.system.jobId";

  /**
   * 构造系统参数
   */
  public static Map<String, String> buildSystemParam(ExecType execType, Date time) {
    return buildSystemParam(execType, time, null, null);
  }

  /**
   * 构造系统参数
   *
   * @param execType 执行方式, 比如是直接执行, 还是补数据, 还是调度执行
   * @param time 对于直接执行, 指的是运行的时间, 对于调度执行, 指的是调度时间, 对于补数据, 指业务补数据的时间
   * @param execId 执行的 id
   * @param jobId 日志 id
   */
  public static Map<String, String> buildSystemParam(ExecType execType, Date time, Integer execId,
      String jobId) {
    Date bizDate;

    switch (execType) {
      case COMPLEMENT_DATA:
        bizDate = time; // 补数据的当天
        break;
      case DIRECT:
      case SCHEDULER:
      default:
        bizDate = addDays(time, -1); // 运行日期的前一天
    }

    Date bizCurDate = addDays(bizDate, 1); // bizDate + 1 天

    Map<String, String> valueMap = new HashMap<>();

    valueMap.put(BIZ_DATE, formatDate(bizDate));
    valueMap.put(BIZ_CUR_DATE, formatDate(bizCurDate));
    valueMap.put(CYC_TIME, formatTime(time));

    if (execId != null) {
      valueMap.put(EXEC_ID, Long.toString(execId));
    }

    if (StringUtils.isNotEmpty(jobId)) {
      valueMap.put(JOB_ID, jobId);
    }

    return valueMap;
  }

  /**
   * @param date
   * @return
   */
  private static String formatDate(Date date) {
    return format(date, DATE_FORMAT);
  }

  /**
   * @param date
   * @return
   */
  private static String formatTime(Date date) {
    return format(date, TIME_FORMAT);
  }
}
