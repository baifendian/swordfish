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
package com.baifendian.swordfish.execserver.adhoc;

import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.job.logger.JobLogger;
import com.baifendian.swordfish.dao.enums.AdHocStatus;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.execserver.job.hive.AdHocSqlJob;
import com.baifendian.swordfish.execserver.parameter.CustomParamManager;
import com.baifendian.swordfish.execserver.parameter.SystemParamManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class AdHocRunner implements Runnable {

  private Logger logger = LoggerFactory.getLogger(AdHocRunner.class);

  private AdHoc adhoc;

  public AdHocRunner(AdHoc adHoc){
    this.adhoc = adHoc;
  }

  @Override
  public void run(){
    JobProps props = new JobProps();
    props.setJobParams(adhoc.getParams());
    props.setProxyUser(adhoc.getProxyUser());
    props.setQueue(adhoc.getQueue());
    /*
    Map<String, String> systemParamMap = SystemParamManager.buildSystemParam(executionFlow, scheduleDate, addDate);

    // 自定义参数
    String cycTimeStr = systemParamMap.get(SystemParamManager.CYC_TIME);
    Map<String, String> customParamMap = CustomParamManager.buildCustomParam(executionFlow, cycTimeStr);
    props.setDefinedParams();
    */

    Logger jobLogger = new JobLogger(adhoc.getJobId(), logger);
    AdHocSqlJob job = null;
    AdHocStatus status = AdHocStatus.SUCCESS;
    try {
      job = new AdHocSqlJob(adhoc.getJobId(), props, logger);
      job.before();
      job.process();
    } catch (Exception e) {
      logger.debug("run adhoc job error", e);
      status = AdHocStatus.FAILED;
    } finally {
      try {
        job.after();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    adhoc.setStatus(status);
    adhoc.setEndTime(new Date());

  }
}
