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
package com.baifendian.swordfish.execserver.runner.adhoc;

import com.baifendian.swordfish.dao.AdHocDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.execserver.job.JobProps;
import org.slf4j.Logger;

import java.util.Date;

public class AdHocRunner implements Runnable {

  /**
   * 即席查询结构
   */
  private AdHoc adHoc;

  /**
   * 即席查询数据库接口
   */
  private AdHocDao adHocDao;

  /**
   * 用于记录日志, 会封装 job id
   */
  private Logger logger;

  public AdHocRunner(AdHoc adHoc, AdHocDao adHocDao, Logger logger) {
    this.adHocDao = adHocDao;
    this.adHoc = adHoc;
    this.logger = logger;
  }

  @Override
  public void run() {
    JobProps props = new JobProps();

    props.setJobParams(adHoc.getParameter());
    props.setProxyUser(adHoc.getProxyUser());
    props.setQueue(adHoc.getQueue());
    props.setProjectId(adHoc.getProjectId());
    props.setAdHocId(adHoc.getId());
    props.setCycTime(adHoc.getCreateTime());

    FlowStatus status;

    try {
      AdHocSqlJob job = new AdHocSqlJob(props, logger);
      status = job.process();
    } catch (Exception e) {
      logger.error("run adHoc job error", e);
      status = FlowStatus.FAILED;
    }

    Date now = new Date();

    adHoc.setStatus(status);
    adHoc.setEndTime(now);

    adHocDao.updateAdHoc(adHoc);
  }
}
