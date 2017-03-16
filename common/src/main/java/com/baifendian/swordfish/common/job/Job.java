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

import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.List;

/**
 * 执行的 Job (用于执行某个具体任务，如 MR/Spark 等) <p>
 *
 * @author : liujin
 * @date : 2017年3月2日
 */
public interface Job {
  /**
   * 获取 生成的作业ID: 节点类型_yyyyMMddHHmmss_NodeId_execId <p>
   *
   * @return 执行 id
   */
  String getJobId();

  /**
   * 作业前处理
   */
  void before() throws Exception;

  /**
   * 作业处理
   */
  void process() throws Exception;

  /**
   * 作业后处理
   */
  void after() throws Exception;

  /**
   * 取消执行(执行 cancel 之前，必须要保证已经调用 run) <p>
   */
  void cancel() throws Exception;

  /**
   * 作业是否执行完成 <p>
   *
   * @return 是否已完成
   */
  boolean isCompleted();

  /**
   * 作业是否被取消了
   */
  boolean isCanceled();

  /**
   * 获取作业的全局参数信息
   */
  JobProps getJobProps();

  /**
   * 获取返回码
   *
   * @return 0-成功，其他值-失败
   */
  int getExitCode();

  /**
   * job执行是否有返回结果
   */
  boolean hasResults();

  List<ExecResult> getResults();

  /**
   * 获取作业的配置参数信息
   */
  BaseParam getParam();

}
