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

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import java.util.List;
import org.slf4j.Logger;

/**
 * 执行的 Job (用于执行某个具体任务，如 MR/Spark 等) <p>
 */
public abstract class Job {

  /**
   * 配置参数
   **/
  protected JobProps props;

  /**
   * 日志记录
   */
  protected Logger logger;

  /**
   * 是否长任务
   */
  protected volatile boolean isLongJob;

  /**
   * 是否已经启动
   */
  protected volatile boolean started = false;

  /**
   * 是否完成
   */
  protected volatile boolean complete = false;

  /**
   * 是否发出了取消请求
   */
  protected volatile boolean cancel = false;

  /**
   * 退出状态
   */
  protected volatile int exitCode = -1;

  /**
   * @param props 作业配置信息, 各类作业根据此配置信息生成具体的作业
   * @param logger 日志
   */
  protected Job(JobProps props, boolean isLongJob, Logger logger) {
    this.props = props;
    this.isLongJob = isLongJob;
    this.logger = logger;
  }

  /**
   * 初始化 job
   */
  public void init() throws Exception {
  }

  /**
   * 前置处理
   */
  public void before() throws Exception {
    started = true;
  }

  /**
   * job 的处理
   */
  public abstract void process() throws Exception;

  /**
   * 后置处理
   */
  public void after() throws Exception {
    complete = true;
  }

  public void cancel(boolean cancelApplication) throws Exception {
    cancel = true;
  }

  /**
   * 处理日志
   */
  public void logProcess(List<String> logs) {
    // 注意, 这里换行还要加 " " 是为了便于日志解析
    logger.info("(stdout, stderr) -> {}", String.join("\n ", logs));
  }

  /**
   * 是否启动了
   */
  public boolean isStarted() {
    return started;
  }

  /**
   * 是否发出取消请求
   */
  public boolean isCancel() {
    return cancel;
  }

  /**
   * 是否完成
   */
  public boolean isCompleted() {
    return complete;
  }

  /**
   * 退出状态
   */
  public int getExitCode() {
    return exitCode;
  }

  /**
   * 是否长任务
   */
  public boolean isLongJob() {
    return isLongJob;
  }

  /**
   * 获取 job 参数
   */
  public abstract BaseParam getParam();
}
