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
      this.info.setWait(executionState.getInit()+executionState.getWaitingDep()+executionState.getWaitingRes());
      this.info.setFailed(executionState.getFailed()+executionState.getDepFailed());
      this.info.setSuccess(executionState.getSuccess());
      this.info.setRunning(executionState.getRunning());
    }
  }

  public static class Info {
    private int running;
    private int total;
    private int wait;
    private int success;
    private int failed;

    public int getRunning() {
      return running;
    }

    public void setRunning(int running) {
      this.running = running;
    }

    public int getTotal() {
      return this.running+this.wait+this.success+this.failed;
    }

    public void setTotal(int total) {
      this.total = total;
    }

    public int getWait() {
      return wait;
    }

    public void setWait(int wait) {
      this.wait = wait;
    }

    public int getSuccess() {
      return success;
    }

    public void setSuccess(int success) {
      this.success = success;
    }

    public int getFailed() {
      return failed;
    }

    public void setFailed(int failed) {
      this.failed = failed;
    }
  }
}
