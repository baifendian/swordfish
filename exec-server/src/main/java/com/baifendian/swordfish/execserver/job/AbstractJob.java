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
package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.execserver.common.ExecResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public abstract class AbstractJob implements Job {
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

  /**
   * 用户定义参数列表
   */
  protected Map<String, String> definedParamMap;

  /**
   * 项目 id
   */
  protected int projectId;

  /**
   * 退出状态
   */
  protected int exitCode = 0;

  /**
   * 是否完成
   */
  protected boolean complete = false;

  /**
   * 是否取消
   */
  protected boolean canceled = false;

  /**
   * 日志记录
   */
  protected Logger logger;

  /**
   * @param jobId  生成的作业 id
   * @param props  作业配置信息, 各类作业根据此配置信息生成具体的作业
   * @param logger 日志
   */
  protected AbstractJob(String jobId, JobProps props, Logger logger) {
    this.jobId = jobId;
    this.props = props;
    this.logger = logger;

    this.definedParamMap = props.getDefinedParams();
    this.projectId = props.getProjectId();

    initJobParams();
  }

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

  public abstract void initJobParams();

  public String getWorkingDirectory() {
    String workingDir = props.getWorkDir();

    if (workingDir == null) {
      return StringUtils.EMPTY;
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
