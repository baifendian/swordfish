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
import com.baifendian.swordfish.common.hadoop.YarnRestClient;
import com.baifendian.swordfish.common.job.utils.node.storm.StormRestUtil;
import com.baifendian.swordfish.common.utils.DateUtils;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.StreamingDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.StreamingResult;
import com.baifendian.swordfish.execserver.job.AbstractStormProcessJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.AbstractYarnJob;
import com.baifendian.swordfish.execserver.utils.Constants;
import com.baifendian.swordfish.execserver.utils.JobLogger;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.baifendian.swordfish.common.job.struct.node.JobType.*;

public class StreamingRunnerManager {

  private static Logger logger = LoggerFactory.getLogger(StreamingRunnerManager.class.getName());

  private final ExecutorService streamingExecutorService;

  private StreamingDao streamingDao;

  public StreamingRunnerManager(Configuration conf) {
    streamingDao = DaoFactory.getDaoInstance(StreamingDao.class);

    int threads = conf
            .getInt(Constants.EXECUTOR_STREAMING_THREADS, Constants.defaultStreamingThreadNum);

    ThreadFactory flowThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Exec-Server-StreamingRunner").build();
    streamingExecutorService = Executors.newFixedThreadPool(threads, flowThreadFactory);
  }

  /**
   * 提交一个流任务
   *
   * @param streamingResult 流任务的结果
   */
  public void submitJob(StreamingResult streamingResult) {

    // 如果是非结束状态, 更新状态为正在运行(防止网络异常)
    if (streamingResult.getStatus().typeIsFinished()) {
      return;
    }

    String jobId = String.format("STREAMING_JOB_%s_%s", streamingResult.getExecId(),
            DateUtils.now(Constants.DATETIME_FORMAT));

    Date now = new Date();

    streamingResult.setStartTime(now);

    streamingResult.setStatus(FlowStatus.RUNNING);
    streamingResult.setJobId(jobId);

    streamingDao.updateResult(streamingResult);

    // 提交执行
    Logger jobLogger = new JobLogger(jobId);

    StreamingRunner runner = new StreamingRunner(streamingResult, jobLogger);
    streamingExecutorService.submit(runner);
  }

  /**
   * 取消一个流任务, 读取里面的 application, 然后发起关闭请求
   *
   * @param streamingResult 流任务的结果
   */
  public void cancelJob(StreamingResult streamingResult) throws Exception {
    JobProps props = new JobProps();

    props.setWorkDir(BaseConfig
            .getStreamingExecDir(streamingResult.getProjectId(), streamingResult.getStreamingId(),
                    streamingResult.getExecId()));
    props.setProxyUser(streamingResult.getProxyUser());
    props.setEnvFile(BaseConfig.getSystemEnvPath());
    props.setJobAppId(streamingResult.getJobId());

    FlowStatus status = null;

    try {
      switch (streamingResult.getType()) {
        case SPARK_STREAMING: {
          AbstractYarnJob.cancelApplication(streamingResult.getAppLinkList(), props,
                  new JobLogger(streamingResult.getJobId()));

          // 删除后, 需要更新状态
          List<String> appLinkList = streamingResult.getAppLinkList();
          String appId =
                  (CollectionUtils.isEmpty(appLinkList)) ? null : appLinkList.get(appLinkList.size() - 1);

          status = YarnRestClient.getInstance().getApplicationStatus(appId);
          break;

        }
        case STORM: {
          AbstractStormProcessJob.cancelApplication(streamingResult.getAppLinkList().get(0));
          status = FlowStatus.KILL;
          break;
        }
        default: {
          String msg = MessageFormat.format("Not support job type: {0}", streamingResult.getType());
          throw new Exception(msg);
        }
      }

      if (status == null) {
        status = FlowStatus.KILL;
      }

      if (status.typeIsFinished()) {
        streamingResult.setStatus(status);

        streamingResult.setEndTime(new Date());

        streamingDao.updateResult(streamingResult);
      }

    } catch (Exception e) {
      logger.error(String.format("Cancel streaming job exception: %d", streamingResult.getExecId()),
              e);
      throw e;
    }
  }

  /**
   * 激活一个暂停的Job
   *
   * @param streamingResult
   */
  public void activateJob(StreamingResult streamingResult) throws Exception {
    try {
      switch (streamingResult.getType()) {
        case STORM:
          AbstractStormProcessJob.activateApplication(streamingResult.getAppLinks());
        default:
          String msg = MessageFormat.format("Not support job type: {0}", streamingResult.getType());
          throw new Exception(msg);
      }
    } catch (Exception e) {
      logger.error(String.format("Activate streaming job exception: %d", streamingResult.getExecId()),
              e);
      throw e;
    }

  }

  /**
   * 恢复一个暂停的Job
   *
   * @param streamingResult
   * @throws Exception
   */
  public void deactivate(StreamingResult streamingResult) throws Exception {
    try {
      switch (streamingResult.getType()) {
        case STORM:
          AbstractStormProcessJob.dedeactivate(streamingResult.getAppLinks());
        default:
          String msg = MessageFormat.format("Not support job type: {0}", streamingResult.getType());
          throw new Exception(msg);
      }
    } catch (Exception e) {
      logger.error(String.format("Deactivate streaming job exception: %d", streamingResult.getExecId()),
              e);
      throw e;
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