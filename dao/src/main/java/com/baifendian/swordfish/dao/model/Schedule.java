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

import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.enums.ScheduleStatus;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
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

  /**
   * 对应工作流id
   * 数据库映射字段
   */
  private int flowId;

  /**
   * 工作流名称
   * DTO需要字段
   */
  private String flowName;

  /**
   * 工作流所属项目名称
   * DTO需要字段
   */
  private String projectName;

  /**
   * 工作流的描述
   * DTO需要字段
   */
  private String desc;

  /**
   * 调度的起始时间
   * 数据库映射字段 start_date
   */
  private Date startDate;

  /**
   * 调度的结束时间
   * 数据库映射字段 end_date
   */
  private Date endDate;

  /**
   * 定时调度配置
   * 数据库映射字段 crontab
   */
  private String crontab;

  /**
   * 依赖工作流
   * 数据库映射字段 dep_workflows
   */
  private String depWorkflowsStr;

  /**
   * 依赖策略
   * 数据库映射字段/DTO需要字段 dep_policy
   */
  private DepPolicyType depPolicy;

  /**
   * 失败策略
   * 数据库映射字段/DTO需要字段 failure_policy
   */
  private FailurePolicyType failurePolicy;

  /**
   * 最大有效时间
   * 数据库映射字段/DTO需要字段 max_try_times
   */
  private Integer maxTryTimes;

  /**
   * 报警策略
   * 数据库映射字段/DTO需要字段 notify_type
   */
  private NotifyType notifyType;

  /**
   * 报警邮箱列表
   * 数据库映射字段/DTO需要字段 notify_mails
   */
  private String notifyMailsStr;

  /**
   * 工作流超时设置
   * 数据库映射字段/DTO需要字段 timeout
   */
  private Integer timeout;

  /**
   * 调度创建时间
   * 数据库映射字段/DTO需要字段 create_time
   */
  private Date createTime;

  /**
   * 修改时间
   * 数据库映射字段/DTO需要字段 modify_time
   */
  private Date modifyTime;

  /**
   * 调度责任人
   * 数据库映射字段 owner
   */
  private int ownerId;

  /**
   * 调度责任人名称
   * DTO需要字段
   */
  private String owner;

  /**
   * 调度状态（上线/下线）
   * 数据库映射字段/DTO需要字段 schedule_status
   */
  private ScheduleStatus scheduleStatus;

  /**
   * 报警通知邮件list
   * DTO需要字段
   */
  private List<String> notifyMails = new ArrayList<>();

  /**
   * 依赖工作流list
   * DTO需要字段
   */
  private List<DepWorkflow> depWorkflows = new ArrayList<>();

  public Schedule() {
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
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

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
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
