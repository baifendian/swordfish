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
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class ExecutionNode {

  /**
   * 具体 workflow 执行的 id
   **/
  private Integer execId;

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

  /**
   * application link
   */
  private String appLinks;

  /**
   * job link
   */
  private String jobLinks;

  /**
   * 得到日志链接
   */
  private List<String> appLinkList = new ArrayList<>();

  /**
   * 得到 job 的链接
   */
  private List<String> jobLinkList = new ArrayList<>();

  public Integer getExecId() {
    return execId;
  }

  public void setExecId(Integer execId) {
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

  public void incAttempt() {
    if (attempt == null) {
      attempt = 0;
    }

    attempt += 1;
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

  public List<String> getAppLinkList() {
    return appLinkList;
  }

  public void setAppLinkList(List<String> appLinkList) {
    this.appLinkList = appLinkList;
    if (CollectionUtils.isNotEmpty(appLinkList)) {
      this.appLinks = JsonUtil.toJsonString(appLinkList);
    }
  }

  public String getAppLinks() {
    return appLinks;
  }

  public void setAppLinks(String appLinks) {
    this.appLinkList = JsonUtil.parseObjectList(appLinks, String.class);
    this.appLinks = appLinks;
  }

  public String getJobLinks() {
    return jobLinks;
  }

  public void setJobLinks(String jobLinks) {
    this.jobLinkList = JsonUtil.parseObjectList(jobLinks, String.class);
    this.jobLinks = jobLinks;
  }

  public List<String> getJobLinkList() {
    return jobLinkList;
  }

  public void setJobLinkList(List<String> jobLinkList) {
    this.jobLinkList = jobLinkList;
    if (CollectionUtils.isNotEmpty(jobLinkList)) {
      this.jobLinks = JsonUtil.toJsonString(jobLinkList);
    }
  }
}
