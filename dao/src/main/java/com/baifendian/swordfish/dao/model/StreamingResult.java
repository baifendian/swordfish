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
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.baifendian.swordfish.dao.utils.json.JsonObjectSerializer;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述一个流任务的结果
 */
public class StreamingResult {

  /**
   * 流任务执行 id
   **/
  private int execId;

  /**
   * 流任务的 id
   */
  private int streamingId;

  /**
   * 结点参数, 注意这个是当次执行的情况
   * 数据库映射字段/DTO需要字段
   */
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  private String parameter;

  /**
   * 用户定义参数, 注意这个是当次执行的情况
   * 数据库映射字段
   */
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  @JsonSerialize(using = JsonObjectSerializer.class)
  private String userDefinedParams;

  /**
   * 提交人 id
   */
  private int submitUserId;

  /**
   * 提交人
   */
  private String submitUser;

  /**
   * 提交时间
   **/
  private Date submitTime;

  /**
   * 队列信息
   * 数据库映射字段/DTO需要字段
   */
  private String queue;

  /**
   * 代理用户
   * 数据库映射字段/DTO需要字段
   */
  private String proxyUser;

  /**
   * 调度时间
   **/
  private Date scheduleTime;

  /**
   * 起始时间
   **/
  private Date startTime;

  /**
   * 结束时间
   */
  private Date endTime;

  /**
   * 执行的状态
   **/
  private FlowStatus status;

  /**
   * 执行的 job id
   **/
  private String jobId;

  /**
   * 日志 link, json array
   */
  private String appLinks;

  /**
   * 日志 link, json array
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

  /**
   * 流任务
   * 数据库映射字段/DTO也需要使用
   */
  private String name;

  /**
   * 项目 id
   * 数据库映射字段
   */
  private int projectId;

  /**
   * 项目名称
   * DTO 需要字段
   */
  private String projectName;

  /**
   * 工作流描述
   * 数据库映射字段/DTO需要字段
   */
  private String desc;

  /**
   * 创建时间
   * 数据库映射字段/DTO需要字段
   */
  private Date createTime;

  /**
   * 修改时间
   * 数据库映射字段/DTO需要字段
   */
  private Date modifyTime;

  /**
   * owner id
   * 数据库映射字段
   */
  private int ownerId;

  /**
   * owner 名称
   * DTO需要字段
   */
  private String owner;

  /**
   * 结点类型
   * 数据库映射字段/DTO需要字段
   */
  private String type;

  public int getExecId() {
    return execId;
  }

  public void setExecId(int execId) {
    this.execId = execId;
  }

  public int getStreamingId() {
    return streamingId;
  }

  public void setStreamingId(int streamingId) {
    this.streamingId = streamingId;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public String getUserDefinedParams() {
    return userDefinedParams;
  }

  public void setUserDefinedParams(String userDefinedParams) {
    this.userDefinedParams = userDefinedParams;
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

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public Date getScheduleTime() {
    return scheduleTime;
  }

  public void setScheduleTime(Date scheduleTime) {
    this.scheduleTime = scheduleTime;
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

  public FlowStatus getStatus() {
    return status;
  }

  public void setStatus(FlowStatus status) {
    this.status = status;
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

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<String> getAppLinkList() {
    return appLinkList;
  }

  public void setAppLinkList(List<String> appLinkList) {
    this.appLinkList = appLinkList;
    if (appLinkList != null) {
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
    if (jobLinkList != null) {
      this.jobLinks = JsonUtil.toJsonString(jobLinkList);
    }
  }
}
