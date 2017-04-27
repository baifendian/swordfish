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

import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.webserver.dto.ScheduleParam;

import java.util.Date;
import java.util.List;

/**
 * 调度返回DTO
 */
public class ScheduleDto {
  private String projectName;
  private String workflowName;
  private String desc;
  private NotifyType notifyType;
  private List<String> notifyMails;
  private int maxTryTimes;
  private FailurePolicyType failurePolicyType;
  private List<Schedule.DepWorkflow> depWorkflows;
  private DepPolicyType depPolicyType;
  private int timeout;
  private Date createTime;
  private Date modifyTime;
  private String owner;
  private ScheduleParam scheduleParam;

  public ScheduleDto() {
  }

  public ScheduleDto(Schedule schedule) {
    this.projectName = schedule.getProjectName();
    this.workflowName = schedule.getFlowName();
    this.desc = schedule.getDesc();
    this.notifyType = schedule.getNotifyType();
    this.notifyMails = schedule.getNotifyMails();
    this.maxTryTimes = schedule.getMaxTryTimes();
    this.failurePolicyType = schedule.getFailurePolicy();
    this.depWorkflows = schedule.getDepWorkflows();
    this.depPolicyType = schedule.getDepPolicy();
    this.timeout = schedule.getTimeout();
    this.createTime = schedule.getCreateTime();
    this.modifyTime = schedule.getModifyTime();
    this.owner = schedule.getOwner();
    this.scheduleParam = new ScheduleParam(schedule.getStartDate(), schedule.getEndDate(), schedule.getCrontab());
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getWorkflowName() {
    return workflowName;
  }

  public void setWorkflowName(String workflowName) {
    this.workflowName = workflowName;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public NotifyType getNotifyType() {
    return notifyType;
  }

  public void setNotifyType(NotifyType notifyType) {
    this.notifyType = notifyType;
  }

  public List<String> getNotifyMails() {
    return notifyMails;
  }

  public void setNotifyMails(List<String> notifyMails) {
    this.notifyMails = notifyMails;
  }

  public int getMaxTryTimes() {
    return maxTryTimes;
  }

  public void setMaxTryTimes(int maxTryTimes) {
    this.maxTryTimes = maxTryTimes;
  }

  public FailurePolicyType getFailurePolicyType() {
    return failurePolicyType;
  }

  public void setFailurePolicyType(FailurePolicyType failurePolicyType) {
    this.failurePolicyType = failurePolicyType;
  }

  public List<Schedule.DepWorkflow> getDepWorkflows() {
    return depWorkflows;
  }

  public void setDepWorkflows(List<Schedule.DepWorkflow> depWorkflows) {
    this.depWorkflows = depWorkflows;
  }

  public DepPolicyType getDepPolicyType() {
    return depPolicyType;
  }

  public void setDepPolicyType(DepPolicyType depPolicyType) {
    this.depPolicyType = depPolicyType;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Date modifyTime) {
    this.modifyTime = modifyTime;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public ScheduleParam getScheduleParam() {
    return scheduleParam;
  }

  public void setScheduleParam(ScheduleParam scheduleParam) {
    this.scheduleParam = scheduleParam;
  }
}
