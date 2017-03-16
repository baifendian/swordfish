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

import java.util.Date;

/**
 * 调度的设置基础数据 <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月23日
 */
public class Schedule {

  private int flowId;

  private String flowName;

  private FlowType flowType;

  private Date createTime;

  private Date modifyTime;

  private int lastModifyBy;

  private PubStatus pubStatus;

  private ScheduleStatus scheduleStatus;

  private Date startDate;

  private Date endDate;

  private ScheduleType scheduleType;

  private String crontabStr;

  private Date nextSubmitTime;

  private String depWorkflows;

  private DepPolicyType depPolicy;

  private FailurePolicyType failurePolicy;

  private Integer maxTryTimes;

  private Boolean failureEmails;

  private Boolean successEmails;

  private Integer timeout;

  private int ownerId;

  private String ownerName;

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

  public FlowType getFlowType() {
    return flowType;
  }

  public void setFlowType(FlowType flowType) {
    this.flowType = flowType;
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

  public int getLastModifyBy() {
    return lastModifyBy;
  }

  public void setLastModifyBy(int lastModifyBy) {
    this.lastModifyBy = lastModifyBy;
  }

  public PubStatus getPubStatus() {
    return pubStatus;
  }

  public void setPubStatus(PubStatus pubStatus) {
    this.pubStatus = pubStatus;
  }

  public ScheduleStatus getScheduleStatus() {
    return scheduleStatus;
  }

  public void setScheduleStatus(ScheduleStatus scheduleStatus) {
    this.scheduleStatus = scheduleStatus;
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

  public ScheduleType getScheduleType() {
    return scheduleType;
  }

  public void setScheduleType(ScheduleType scheduleType) {
    this.scheduleType = scheduleType;
  }

  public String getCrontabStr() {
    return crontabStr;
  }

  public void setCrontabStr(String crontabStr) {
    this.crontabStr = crontabStr;
  }

  public Date getNextSubmitTime() {
    return nextSubmitTime;
  }

  public void setNextSubmitTime(Date nextSubmitTime) {
    this.nextSubmitTime = nextSubmitTime;
  }

  public String getDepWorkflows() {
    return depWorkflows;
  }

  public void setDepWorkflows(String depWorkflows) {
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

  public Boolean getFailureEmails() {
    return failureEmails;
  }

  public void setFailureEmails(Boolean failureEmails) {
    this.failureEmails = failureEmails;
  }

  public Boolean getSuccessEmails() {
    return successEmails;
  }

  public void setSuccessEmails(Boolean successEmails) {
    this.successEmails = successEmails;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public int getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }
}
