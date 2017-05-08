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
    if (executionState != null){
      this.hour = executionState.getHour();
      this.date = executionState.getDay();
      this.info = new Info(executionState);
    }
  }

  public static class Info {
    private int init;
    private int waiting_dep;
    private int waiting_res;
    private int running;
    private int success;
    private int kill;
    private int failed;
    private int total;

    public Info(){}

    public Info(ExecutionState executionState){
      if (executionState!=null){
        this.init = executionState.getInit();
        this.waiting_dep = executionState.getWaitingDep();
        this.waiting_res = executionState.getWaitingRes();
        this.running = executionState.getRunning();
        this.success = executionState.getSuccess();
        this.kill = executionState.getKill();
        this.failed = executionState.getFailed();
      }
    }

    public int getInit() {
      return init;
    }

    public void setInit(int init) {
      this.init = init;
    }

    public int getWaiting_dep() {
      return waiting_dep;
    }

    public void setWaiting_dep(int waiting_dep) {
      this.waiting_dep = waiting_dep;
    }

    public int getWaiting_res() {
      return waiting_res;
    }

    public void setWaiting_res(int waiting_res) {
      this.waiting_res = waiting_res;
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
      return this.init+this.waiting_dep+this.waiting_res+this.running+this.success+this.kill+this.failed;
    }

  }
}
