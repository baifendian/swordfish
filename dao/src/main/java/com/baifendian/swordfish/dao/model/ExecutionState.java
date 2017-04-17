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
package com.baifendian.swordfish.dao.model;

import java.util.Date;

/**
 * 运行统计结果实体
 */
public class ExecutionState {
  private Date day;

  /**
   * 初始化个数
   */
  private int init;
  /**
   * 依赖任务中个数
   */
  private int waitingDep;

  /**
   * 依赖资源中个数
   */
  private int waitingRes;

  /**
   * 运行中个数
   */
  private int running;

  /**
   * 运行成功个数
   */
  private int success;

  /**
   * 被kill的个数
   */
  private int kill;

  /**
   * 运行失败的个数
   */
  private int failed;

  /**
   * 依赖失败的个数
   */
  private int depFailed;

  public Date getDay() {
    return day;
  }

  public void setDay(Date day) {
    this.day = day;
  }

  public int getInit() {
    return init;
  }

  public void setInit(int init) {
    this.init = init;
  }

  public int getWaitingDep() {
    return waitingDep;
  }

  public void setWaitingDep(int waitingDep) {
    this.waitingDep = waitingDep;
  }

  public int getWaitingRes() {
    return waitingRes;
  }

  public void setWaitingRes(int waitingRes) {
    this.waitingRes = waitingRes;
  }

  public int getRunning() {
    return running;
  }

  public void setRunning(int running) {
    this.running = running;
  }

  public int getSuccess() {
    return success;
  }

  public void setSuccess(int success) {
    this.success = success;
  }

  public int getKill() {
    return kill;
  }

  public void setKill(int kill) {
    this.kill = kill;
  }

  public int getFailed() {
    return failed;
  }

  public void setFailed(int failed) {
    this.failed = failed;
  }

  public int getDepFailed() {
    return depFailed;
  }

  public void setDepFailed(int depFailed) {
    this.depFailed = depFailed;
  }
}
