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
package com.baifendian.swordfish.webserver.dto;

import com.baifendian.swordfish.dao.model.ExecutionState;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;

/**
 * 统计数据返回实体
 */
public class StatDto {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Date date;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Integer hour;

  private Info info = new Info();

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Info getInfo() {
    return info;
  }

  public void setInfo(Info info) {
    this.info = info;
  }

  public Integer getHour() {
    return hour;
  }

  public void setHour(Integer hour) {
    this.hour = hour;
  }

  public StatDto() {
  }

  public StatDto(ExecutionState executionState) {
    if (executionState != null) {
      this.hour = executionState.getHour();
      this.date = executionState.getDay();
      this.info = new Info(executionState);
    }
  }

  public static class Info {

    private int init;
    private int waitingDep;
    private int waitingRes;
    private int running;
    private int success;
    private int kill;
    private int failed;
    private int depFailed;
    private int inActive;
    private int total;

    public Info() {
    }

    public Info(ExecutionState executionState) {
      if (executionState != null) {
        this.init = executionState.getInit();
        this.waitingDep = executionState.getWaitingDep();
        this.waitingRes = executionState.getWaitingRes();
        this.running = executionState.getRunning();
        this.success = executionState.getSuccess();
        this.kill = executionState.getKill();
        this.failed = executionState.getFailed();
        this.depFailed = executionState.getDepFailed();
        this.inActive = executionState.getInActive();
      }
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

    public int getDepFailed() {
      return depFailed;
    }

    public void setDepFailed(int depFailed) {
      this.depFailed = depFailed;
    }

    public void setTotal(int total) {
      this.total = total;
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

    public int getTotal() {
      return this.init + this.waitingDep + this.waitingRes + this.running + this.success + this.kill
          + this.failed + this.depFailed + this.inActive;
    }

  }
}
