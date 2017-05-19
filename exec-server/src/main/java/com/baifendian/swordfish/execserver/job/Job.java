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
import com.baifendian.swordfish.execserver.common.ExecResult;

import java.util.List;

/**
 * 执行的 Job (用于执行某个具体任务，如 MR/Spark 等) <p>
 */
public interface Job {
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
   * 取消执行
   */
  void cancel() throws Exception;

  /**
   * 作业是否执行完成
   *
   * @return 是否已完成
   */
  boolean isCompleted();

  /**
   * 作业是否被取消了
   */
  boolean isCanceled();

  /**
   * 获取返回码
   *
   * @return 0 表示成功，其他值表示失败
   */
  int getExitCode();

  /**
   * job 执行是否有返回结果
   */
  boolean hasResults();

  /**
   * 获取 job 执行返回的结果
   *
   * @return
   */
  List<ExecResult> getResults();

  /**
   * 获取 job 参数
   *
   * @return
   */
  BaseParam getParam();
}
