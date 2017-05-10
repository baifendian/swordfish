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

import com.baifendian.swordfish.common.job.struct.hql.AdHocParam;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.common.utils.DateUtils;
import com.baifendian.swordfish.dao.AdHocDao;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.utils.Constants;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AdHocRunnerManager {
  private static final Logger logger = LoggerFactory.getLogger(AdHocRunnerManager.class);

  private final ExecutorService adHocExecutorService;
  private AdHocDao adHocDao;

  public AdHocRunnerManager(Configuration conf) {
    this.adHocDao = DaoFactory.getDaoInstance(AdHocDao.class);

    int threads = conf.getInt(Constants.EXECUTOR_ADHOCRUNNER_THREADS, 20);

    ThreadFactory flowThreadFactory = new ThreadFactoryBuilder().setNameFormat("Exec-Server-AdHocRunner").build();
    adHocExecutorService = Executors.newFixedThreadPool(threads, flowThreadFactory);
  }

  /**
   * 提交即席查询任务
   *
   * @param adHoc
   */
  public void submitAdHoc(AdHoc adHoc) {
    String jobId = String.format("ADHOC_%s_%s", adHoc.getId(), DateUtils.now(Constants.DATETIME_FORMAT));

    Date now = new Date();

    adHoc.setStartTime(now);

    // 如果是非结束状态, 更新状态为正在运行(防止网络异常)
    if (adHoc.getStatus().typeIsNotFinished()) {
      adHoc.setStatus(FlowStatus.RUNNING);
    }

    adHoc.setJobId(jobId);

    adHocDao.updateAdHoc(adHoc);

    // 将语句进行分差, 插入到相应的表中(需要在这里插入, 防止返回的时候, 语句还没插入)
    AdHocParam param = JsonUtil.parseObject(adHoc.getParameter(), AdHocParam.class);

    if (param != null) {
      adHocDao.initAdHocResult(adHoc.getId(), CommonUtil.sqlSplit(param.getStms()));
    }

    // 提交执行
    AdHocRunner adHocRunner = new AdHocRunner(adHoc, adHocDao);
    adHocExecutorService.submit(adHocRunner);
  }

  /**
   * 停止线程执行
   */
  public void destory() {
    if (!adHocExecutorService.isShutdown()) {
      adHocExecutorService.shutdownNow();
    }
  }
}
