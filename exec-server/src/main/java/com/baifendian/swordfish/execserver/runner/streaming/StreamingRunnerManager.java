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
package com.baifendian.swordfish.execserver.runner.streaming;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.utils.DateUtils;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.StreamingDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.StreamingResult;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.yarn.AbstractYarnJob;
import com.baifendian.swordfish.execserver.utils.Constants;
import com.baifendian.swordfish.execserver.utils.JobLogger;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class StreamingRunnerManager {
  private static Logger logger = LoggerFactory.getLogger(StreamingRunnerManager.class.getName());

  private final ExecutorService streamingExecutorService;

  private StreamingDao streamingDao;

  public StreamingRunnerManager(Configuration conf) {
    streamingDao = DaoFactory.getDaoInstance(StreamingDao.class);

    int threads = conf.getInt(Constants.EXECUTOR_STREAMING_THREADS, Constants.defaultStreamingThreadNum);

    ThreadFactory flowThreadFactory = new ThreadFactoryBuilder().setNameFormat("Exec-Server-StreamingRunner").build();
    streamingExecutorService = Executors.newFixedThreadPool(threads, flowThreadFactory);
  }

  /**
   * 提交一个流任务
   *
   * @param streamingResult
   */
  public void submitJob(StreamingResult streamingResult) {

    // 如果是非结束状态, 更新状态为正在运行(防止网络异常)
    if (streamingResult.getStatus().typeIsFinished()) {
      return;
    }

    String jobId = String.format("STREAMING_JOB_%s_%s", streamingResult.getExecId(), DateUtils.now(Constants.DATETIME_FORMAT));

    Date now = new Date();

    streamingResult.setStartTime(now);

    streamingResult.setStatus(FlowStatus.RUNNING);
    streamingResult.setJobId(jobId);

    streamingDao.updateResult(streamingResult);

    // 提交执行
    Logger jobLogger = new JobLogger(jobId, logger);

    StreamingRunner runner = new StreamingRunner(streamingResult, jobLogger);
    streamingExecutorService.submit(runner);
  }

  /**
   * 取消一个流任务, 读取里面的 application, 然后发起关闭请求
   *
   * @param streamingResult
   */
  public void cancelJob(StreamingResult streamingResult) {
    JobProps props = new JobProps();

    props.setWorkDir(BaseConfig.getStreamingExecDir(streamingResult.getProjectId(), streamingResult.getStreamingId(), streamingResult.getExecId()));
    props.setProxyUser(streamingResult.getProxyUser());
    props.setEnvFile(BaseConfig.getSystemEnvPath());
    props.setJobAppId(streamingResult.getJobId());

    try {
      AbstractYarnJob.cancelApplication(streamingResult.getAppLinkList(), props, new JobLogger(streamingResult.getJobId(), logger));
    } catch (Exception e) {
      logger.error(String.format("cancel streaming job exception: %d", streamingResult.getExecId()), e);
    }
  }

  /**
   * 销毁线程池
   */
  public void destory() {
    if (!streamingExecutorService.isShutdown()) {
      streamingExecutorService.shutdownNow();
    }
  }
}