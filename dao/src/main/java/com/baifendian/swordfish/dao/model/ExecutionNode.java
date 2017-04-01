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

import com.baifendian.swordfish.dao.enums.FlowStatus;

import java.util.Date;
import java.util.List;

public class ExecutionNode {

  /**
   * 具体workflow执行的 id
   **/
  private Long execId;

  /**
   * node 的名称
   **/
  private String name;

  /**
   * 运行状态
   **/
  private FlowStatus status;

  /**
   * 起始时间
   **/
  private Date startTime;

  /**
   * 结束时间
   **/
  private Date endTime;

  /**
   * 尝试次数
   **/
  private Integer attempt;

  /**
   * 执行的job id
   **/
  private String jobId;

  private String logLinks;

  public Long getExecId() {
    return execId;
  }

  public void setExecId(Long execId) {
    this.execId = execId;
  }

  public FlowStatus getStatus() {
    return status;
  }

  public void setStatus(FlowStatus status) {
    this.status = status;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public Integer getAttempt() {
    return attempt;
  }

  public void setAttempt(Integer attempt) {
    this.attempt = attempt;
  }

  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLogLinks() {
    return logLinks;
  }

  public void setLogLinks(String logLinks) {
    this.logLinks = logLinks;
  }
}
