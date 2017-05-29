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

public abstract class AbstractJob implements Job {
  /**
   * 配置参数
   **/
  protected JobProps props;

  /**
   * 退出状态
   */
  protected volatile int exitCode = -1;

  /**
   * 是否完成
   */
  protected volatile boolean complete = false;

  /**
   * 是否已经启动
   */
  protected volatile boolean started = false;

  /**
   * 是否长任务
   */
  protected volatile boolean isLongJob = false;

  /**
   * 日志记录
   */
  protected Logger logger;

  /**
   * @param props  作业配置信息, 各类作业根据此配置信息生成具体的作业
   * @param logger 日志
   */
  protected AbstractJob(JobProps props, boolean isLongJob, Logger logger) {
    this.props = props;
    this.isLongJob = isLongJob;
    this.logger = logger;

    initJob();
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
  public boolean isStarted() {
    return started;
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
  public boolean hasResults() {
    return false;
  }

  @Override
  public List<ExecResult> getResults() {
    return null;
  }

  @Override
  public boolean isLongJob() {
    return isLongJob;
  }

  /**
   * 初始化 job
   */
  public abstract void initJob();

  /**
   * 得到工作
   *
   * @return
   */
  public String getWorkingDirectory() {
    String workingDir = props.getWorkDir();

    if (workingDir == null) {
      return StringUtils.EMPTY;
    }

    return workingDir;
  }
}
