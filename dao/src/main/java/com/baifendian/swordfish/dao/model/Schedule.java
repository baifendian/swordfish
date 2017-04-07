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


import com.baifendian.swordfish.dao.enums.*;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 调度的设置基础数据 <p>
 *
 */
public class Schedule {

  private ScheduleParam schedule = new ScheduleParam();

  @JsonIgnore
  private String scheduleStr;

  @JsonIgnore
  private int flowId;

  private String flowName;

  private String projectName;

  @JsonIgnore
  private Date startDate;

  @JsonIgnore
  private Date endDate;

  @JsonIgnore
  private String crontab;

  @JsonIgnore
  private String depWorkflowsStr;

  private DepPolicyType depPolicy;

  private FailurePolicyType failurePolicy;

  private Integer maxTryTimes;

  private NotifyType notifyType;

  @JsonIgnore
  private String notifyMailsStr;

  private Integer timeout;

  private Date createTime;

  private Date modifyTime;

  @JsonIgnore
  private int ownerId;

  private String owner;

  @JsonIgnore
  private ScheduleStatus scheduleStatus;

  private List<String> notifyMails = new ArrayList<>();

  private List<DepWorkflow> depWorkflows = new ArrayList<>();

  public Schedule() {
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public ScheduleParam getSchedule() {
    return schedule;
  }

  public void setSchedule(ScheduleParam schedule) throws JsonProcessingException {
    this.scheduleStr = JsonUtil.toJsonString(schedule);
    this.schedule = schedule;
  }

  public String getScheduleStr() {
    return scheduleStr;
  }

  public void setScheduleStr(String scheduleStr) throws IOException {
    this.schedule = JsonUtil.parseObject(scheduleStr,ScheduleParam.class);
    this.scheduleStr = scheduleStr;
  }

  public int getFlowId() {
    return flowId;
  }

  public void setFlowId(int flowId) {
    this.flowId = flowId;
  }

  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  public Date getStartDate() {

    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.schedule.setStartDate(startDate);
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.schedule.setEndDate(endDate);
    this.endDate = endDate;
  }

  public String getCrontab() {
    return crontab;
  }

  public void setCrontab(String crontab) {
    this.schedule.setCrontab(crontab);
    this.crontab = crontab;
  }

  public String getDepWorkflowsStr() {
    return depWorkflowsStr;
  }

  public void setDepWorkflowsStr(String depWorkflowsStr) throws IOException {
    this.depWorkflows = JsonUtil.parseObjectList(depWorkflowsStr,DepWorkflow.class);
    this.depWorkflowsStr = depWorkflowsStr;
  }

  public List<DepWorkflow> getDepWorkflows() {
    return depWorkflows;
  }

  public void setDepWorkflows(List<DepWorkflow> depWorkflows) throws JsonProcessingException {
    this.depWorkflowsStr = JsonUtil.toJsonString(depWorkflows);
    this.depWorkflows = depWorkflows;
  }

  public DepPolicyType getDepPolicy() {
    return depPolicy;
  }

  public void setDepPolicy(DepPolicyType depPolicy) {
    this.depPolicy = depPolicy;
  }

  public FailurePolicyType getFailurePolicy() {
    return failurePolicy;
  }

  public void setFailurePolicy(FailurePolicyType failurePolicy) {
    this.failurePolicy = failurePolicy;
  }

  public Integer getMaxTryTimes() {
    return maxTryTimes;
  }

  public void setMaxTryTimes(Integer maxTryTimes) {
    this.maxTryTimes = maxTryTimes;
  }

  public NotifyType getNotifyType() {
    return notifyType;
  }

  public void setNotifyType(NotifyType notifyType) {
    this.notifyType = notifyType;
  }

  public String getNotifyMailsStr() {
    return notifyMailsStr;
  }

  public void setNotifyMailsStr(String notifyMailsStr) throws IOException {
    this.notifyMails = JsonUtil.parseObjectList(notifyMailsStr,String.class);
    this.notifyMailsStr = notifyMailsStr;
  }

  public List<String> getNotifyMails() {
    return notifyMails;
  }

  public void setNotifyMails(List<String> notifyMails) throws JsonProcessingException {
    this.notifyMailsStr = JsonUtil.toJsonString(notifyMails);
    this.notifyMails = notifyMails;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
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

  public int getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public ScheduleStatus getScheduleStatus() {
    return scheduleStatus;
  }

  public void setScheduleStatus(ScheduleStatus scheduleStatus) {
    this.scheduleStatus = scheduleStatus;
  }

  public static class ScheduleParam{
    private Date startDate;
    private Date endDate;
    private String crontab;

    public ScheduleParam() {
    }

    public Date getStartDate() {
      return startDate;
    }

    public void setStartDate(Date startDate) {
      this.startDate = startDate;
    }

    public Date getEndDate() {
      return endDate;
    }

    public void setEndDate(Date endDate) {
      this.endDate = endDate;
    }

    public String getCrontab() {
      return crontab;
    }

    public void setCrontab(String crontab) {
      this.crontab = crontab;
    }
  }

  public static class DepWorkflow{
    private String projectName;
    private String workflowName;

    public DepWorkflow() {
    }

    public DepWorkflow(String projectName, String workflowName) {
      this.projectName = projectName;
      this.workflowName = workflowName;
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
  }
}
