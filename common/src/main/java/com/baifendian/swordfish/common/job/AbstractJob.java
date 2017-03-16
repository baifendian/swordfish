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

package com.baifendian.swordfish.common.job;

import com.baifendian.swordfish.common.job.config.BaseConfig;
import com.baifendian.swordfish.common.job.logger.JobLogger;
import com.fasterxml.jackson.databind.deser.Deserializers;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : liujin
 * @date : 2017-03-06 10:56
 */
public abstract class AbstractJob implements Job {
  /**
   * LOGGER
   */
  protected final JobLogger logger;

  private final Logger _logger;

  /**
   * jobId
   **/
  protected final String jobId;

  /**
   * {@link Process}
   */
  protected Process process;

  /**
   * 配置参数
   **/
  protected JobProps props;

  protected String jobPath;

  protected int exitCode;

  protected boolean complete = false;

  protected boolean canceled = false;

  protected Map<String, Object> jobParams;

  protected Map<String, String> definedParamMap;

  protected int projectId;

  /**
   * @param jobId  生成的作业id
   * @param props  作业配置信息,各类作业根据此配置信息生成具体的作业
   * @param logger 日志
   */
  protected AbstractJob(String jobId, JobProps props, Logger logger) throws IOException {
    this.jobId = jobId;
    this.props = props;
    this._logger = logger;
    this.logger = new JobLogger(jobId, logger);
    this.definedParamMap = props.getDefinedParams();
    this.projectId = props.getProjectId();
    initJobParams();
  }

  @Override
  public String getJobId() {
    return jobId;
  }

  @Override
  public void before() throws Exception {
  }

  @Override
  public void process() throws Exception {

  }

  @Override
  public void after() throws Exception {
  }

  @Override
  public void cancel() throws Exception {
    // 暂不支持
  }

  @Override
  public boolean isCanceled() {
    return canceled;
  }

  @Override
  public boolean isCompleted() {
    return complete;
  }

  @Override
  public int getExitCode() {
    return exitCode;
  }

  @Override
  public JobProps getJobProps() {
    return props;
  }

  public abstract void initJobParams() throws IOException;

  public String getWorkingDirectory() {
    String workingDir = props.getWorkDir();
    if (workingDir == null) {
      return "";
    }

    return workingDir;
  }

  public String getProxyUser() {
    return props.getProxyUser();
  }

  @Override
  public boolean hasResults() {
    return false;
  }

  @Override
  public List<ExecResult> getResults() {
    return null;
  }

}
