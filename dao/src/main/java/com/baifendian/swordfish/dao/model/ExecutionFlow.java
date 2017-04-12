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

import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.flow.params.Property;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExecutionFlow {
  /**
   * 具体执行的id
   **/
  private Integer id;

  /**
   * workflow的id
   **/
  @JsonIgnore
  private int flowId;

  /**
   * workflow名称
   **/
  private String flowName;

  /**
   * worker的host
   **/
  private String worker;

  /**
   * workflow执行的状态
   **/
  private FlowStatus status;

  /**
   * 提交用户id
   **/
  private int submitUserId;

  /**
   * 提交用户
   **/
  private String submitUser;

  /**
   * 代理用户
   **/
  private String proxyUser;

  /**
   * 提交时间
   **/
  private Date submitTime;

  /**
   * 起始时间
   **/
  private Date startTime;

  /**
   * 结束时间
   **/
  private Date endTime;

  /**
   * workflow的数据
   **/
  private String workflowData;

  /**
   * workflow 等运行的类型
   **/
  private ExecType type;

  /**
   * workflow 所在项目的id
   */
  private Integer projectId;

  /**
   * workflow 所在项目的名称
   */
  private String projectName;

  /**
   * 调度时间
   **/
  private Date scheduleTime;

  private Integer maxTryTimes;

  private Integer timeout;

  private String userDefinedParams;

  private String extras;

  @JsonIgnore
  private Map<String, String> userDefinedParamMap;

  /**
   * 作业提交队列
   **/
  private String queue;

  private String owner;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public String getWorker() {
    return worker;
  }

  public void setWorker(String worker) {
    this.worker = worker;
  }

  public FlowStatus getStatus() {
    return status;
  }

  public void setStatus(FlowStatus status) {
    this.status = status;
  }

  public int getSubmitUserId() {
    return submitUserId;
  }

  public void setSubmitUserId(int submitUserId) {
    this.submitUserId = submitUserId;
  }

  public String getSubmitUser() {
    return submitUser;
  }

  public void setSubmitUser(String submitUser) {
    this.submitUser = submitUser;
  }

  public Date getSubmitTime() {
    return submitTime;
  }

  public void setSubmitTime(Date submitTime) {
    this.submitTime = submitTime;
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

  public void setScheduleTime(Date scheduleTime) {
    this.scheduleTime = scheduleTime;
  }

  public String getWorkflowData() {
    return workflowData;
  }

  public void setWorkflowData(String workflowData) {
    this.workflowData = workflowData;
  }

  public ExecType getType() {
    return type;
  }

  public void setType(ExecType type) {
    this.type = type;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public String getProjectName() {
    return projectName;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  public Date getScheduleTime() {
    return scheduleTime;
  }

  public Integer getMaxTryTimes() {
    return maxTryTimes;
  }

  public void setMaxTryTimes(Integer maxTryTimes) {
    this.maxTryTimes = maxTryTimes;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public String getUserDefinedParams() {
    return userDefinedParams;
  }

  public void setUserDefinedParams(String userDefinedParams) {
    this.userDefinedParams = userDefinedParams;
  }

  public String getExtras() {
    return extras;
  }

  public void setExtras(String extras) {
    this.extras = extras;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public Map<String, String> getUserDefinedParamMap() {
    List<Property> propList;
    if (userDefinedParamMap == null && StringUtils.isNotEmpty(userDefinedParams)) {
      propList = JsonUtil.parseObjectList(userDefinedParams, Property.class);
      userDefinedParamMap = propList.stream().collect(Collectors.toMap(Property::getProp, Property::getValue));
    }
    return userDefinedParamMap;
  }
}
