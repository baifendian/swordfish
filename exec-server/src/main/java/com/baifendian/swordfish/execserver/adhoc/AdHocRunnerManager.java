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

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.baifendian.swordfish.common.utils.BFDDateUtils;
import com.baifendian.swordfish.dao.AdHocDao;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.execserver.Constants;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AdHocRunnerManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocRunnerManager.class);

  private final ExecutorService adHocExecutorService;
  private AdHocDao adHocDao;
  private Configuration conf;

  public AdHocRunnerManager(Configuration conf){
    this.conf = conf;
    this.adHocDao = DaoFactory.getDaoInstance(AdHocDao.class);
    int threads = conf.getInt(Constants.EXECUTOR_ADHOCRUNNER_THREADS, 20);
    ThreadFactory flowThreadFactory = new ThreadFactoryBuilder().setNameFormat("Exec-Server-AdHocRunner").build();
    adHocExecutorService = Executors.newFixedThreadPool(threads, flowThreadFactory);

  }

  public void submitAdHoc(AdHoc adHoc){
    String jobId = "ADHOC_" + adHoc.getId() + "_" + BFDDateUtils.now(Constants.DATETIME_FORMAT);
    adHoc.setStartTime(new Date());
    adHoc.setStatus(FlowStatus.INIT);
    adHoc.setJobId(jobId);
    adHocDao.updateAdHoc(adHoc);

    AdHocRunner adHocRunner = new AdHocRunner(adHoc, adHocDao);
    adHocExecutorService.submit(adHocRunner);
  }

  public void destory(){
    if(!adHocExecutorService.isShutdown()){
      adHocExecutorService.shutdownNow();
    }
  }
}
