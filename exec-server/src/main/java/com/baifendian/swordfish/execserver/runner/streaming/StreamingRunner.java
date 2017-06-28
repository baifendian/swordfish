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
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.BaseParamFactory;
import com.baifendian.swordfish.common.mail.EmailManager;
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.model.StreamingResult;
import com.baifendian.swordfish.execserver.job.Job;
import com.baifendian.swordfish.execserver.job.JobManager;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.parameter.SystemParamManager;
import com.baifendian.swordfish.execserver.utils.EnvHelper;
import com.baifendian.swordfish.execserver.utils.JobLogger;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class StreamingRunner implements Callable<Boolean> {

  /**
   * 流数据结果查询
   */
  private StreamingResult streamingResult;

  /**
   * 用于记录日志, 会封装 job id
   */
  private Logger logger;

  /**
   * 具体的任务
   */
  private Job job;

  public StreamingRunner(StreamingResult streamingResult, Logger jobLogger) {
    this.streamingResult = streamingResult;
    this.logger = jobLogger;
  }

  @Override
  public Boolean call() {
    // "项目id/streamingId/执行id"
    String jobScriptPath = BaseConfig
        .getStreamingExecDir(streamingResult.getProjectId(), streamingResult.getStreamingId(),
            streamingResult.getExecId());

    logger.info("streaming id:{}, exec id:{}, script path:{}", streamingResult.getStreamingId(),
        streamingResult.getExecId(), jobScriptPath);

    // 作业参数配置
    // 系统参数, 注意 schedule time 是真正调度运行的时刻
    Map<String, String> systemParamMap = SystemParamManager
        .buildSystemParam(ExecType.DIRECT, streamingResult.getScheduleTime(),
            streamingResult.getJobId());

    // 自定义参数
    Map<String, String> customParamMap = streamingResult.getUserDefinedParamMap();

    // 作业参数配置
    Map<String, String> allParamMap = new HashMap<>();

    if (systemParamMap != null) {
      allParamMap.putAll(systemParamMap);
    }

    if (customParamMap != null) {
      allParamMap.putAll(customParamMap);
    }

    JobProps props = new JobProps();

    props.setJobParams(streamingResult.getParameter());
    props.setWorkDir(jobScriptPath);
    props.setProxyUser(streamingResult.getProxyUser());
    props.setDefinedParams(allParamMap);
    props.setProjectId(streamingResult.getProjectId());
    props.setExecJobId(streamingResult.getStreamingId());
    props.setExecId(streamingResult.getExecId());
    props.setEnvFile(BaseConfig.getSystemEnvPath());
    props.setQueue(streamingResult.getQueue());
    props.setExecJobStartTime(streamingResult.getScheduleTime());
    props.setJobAppId(streamingResult.getJobId()); // 这是设置为和 job id 一样

    JobLogger jobLogger = new JobLogger(streamingResult.getJobId(), logger);

    boolean success = false;

    try {
      // 准备工作目录和用户
      EnvHelper.workDirAndUserCreate(jobScriptPath, streamingResult.getProxyUser(), logger);

      // 解析作业参数获取需要的 "项目级资源文件" 清单
      List<String> projectRes = genProjectResFiles();

      // 将 hdfs 资源拷贝到本地
      EnvHelper.copyResToLocal(streamingResult.getProjectId(), jobScriptPath, projectRes, logger);

      // 可以运行了
      job = JobManager.newJob(streamingResult.getType(), props, jobLogger);

      // job 的初始化
      job.init();

      // job 的前处理
      job.before();

      // job 的处理过程
      job.process();

      // job 的后处理过程
      job.after();

      success = (job.getExitCode() == 0);
    } catch (Exception e) {
      success = false;

      logger.error(String.format("job process exception, streaming job id: %s, exec id: %s",
          streamingResult.getStreamingId(), streamingResult.getExecId()), e);
    } finally {
      kill();

      // 执行完后, 清理目录, 避免文件过大
      try {
        FileUtils.deleteDirectory(new File(jobScriptPath));
      } catch (IOException e) {
        logger.error(String.format("delete dir exception: %s", jobScriptPath), e);
      }

      logger.info("job process done, streaming job id: {}, exec id: {}, success: {}",
          streamingResult.getStreamingId(), streamingResult.getExecId(), success);
    }

    // 运行完后发送报警
    EmailManager.sendMessageOfStreamingJob(streamingResult);

    return success;
  }

  /**
   * 得到资源文件
   */
  private List<String> genProjectResFiles() throws
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

    // 项目资源文件
    Set<String> projectFiles = new HashSet<>();

    // 得到结点参数信息
    BaseParam baseParam = BaseParamFactory
        .getBaseParam(streamingResult.getType(), streamingResult.getParameter());

    // 结点参数中获取资源文件
    if (baseParam != null) {
      List<String> projectResourceFiles = baseParam.getProjectResourceFiles();
      if (projectResourceFiles != null) {
        projectFiles.addAll(projectResourceFiles);
      }
    }

    return new ArrayList<>(projectFiles);
  }

  /**
   * 关闭任务
   */
  public void kill() {
    if (job != null && job.isStarted()) {
      try {
        job.cancel(false);
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
  }
}
