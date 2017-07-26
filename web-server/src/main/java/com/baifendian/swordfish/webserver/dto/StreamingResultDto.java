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

import com.baifendian.swordfish.common.hadoop.ConfigurationUtil;
import com.baifendian.swordfish.common.json.JsonOrdinalSerializer;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.model.StreamingResult;
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.baifendian.swordfish.common.job.struct.node.JobType.SPARK_STREAMING;
import static com.baifendian.swordfish.common.job.struct.node.JobType.STORM;

public class StreamingResultDto {

  private int execId;

  private String name;

  private String desc;

  private String projectName;

  private Date createTime;

  private Date modifyTime;

  private String owner;

  private String type;

  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String parameter;

  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String userDefParams;

  private Date submitTime;

  private Date startTime;

  private Date endTime;

  private String submitUser;

  private String proxyUser;

  private String queue;

  @JsonSerialize(using = JsonOrdinalSerializer.class)
  private FlowStatus status;

  private List<String> appLinks;

  private NotifyType notifyType;

  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String notifyMails;

  public StreamingResultDto() {
  }

  public StreamingResultDto(StreamingResult streamingResult) {
    if (streamingResult != null) {
      this.execId = streamingResult.getExecId();
      this.name = streamingResult.getName();
      this.desc = streamingResult.getDesc();
      this.projectName = streamingResult.getProjectName();
      this.createTime = streamingResult.getCreateTime();
      this.modifyTime = streamingResult.getModifyTime();
      this.owner = streamingResult.getOwner();
      this.type = streamingResult.getType();
      this.parameter = streamingResult.getParameter();
      this.userDefParams = streamingResult.getUserDefinedParams();
      this.submitTime = streamingResult.getSubmitTime();
      this.startTime = streamingResult.getStartTime();
      this.endTime = streamingResult.getEndTime();
      this.submitUser = streamingResult.getSubmitUser();
      this.proxyUser = streamingResult.getProxyUser();
      this.queue = streamingResult.getQueue();
      this.status = streamingResult.getStatus();

      // link 需要添加前缀
      List<String> appIds = streamingResult.getAppLinkList();

      switch (type) {
        case STORM: {
          if (CollectionUtils.isNotEmpty(appIds)) {
            this.appLinks = new ArrayList<>();

            for (String appId : appIds) {
              this.appLinks.add(ConfigurationUtil.getStormAppAddress(appId));
            }
          }
          break;
        }
        case SPARK_STREAMING: {
          if (CollectionUtils.isNotEmpty(appIds)) {
            this.appLinks = new ArrayList<>();

            for (String appId : appIds) {
              this.appLinks.add(ConfigurationUtil.getWebappAddress(appId));
            }
          }
          break;
        }
      }


      this.notifyType = streamingResult.getNotifyType();
      this.notifyMails = streamingResult.getNotifyMails();
    }
  }

  public int getExecId() {
    return execId;
  }

  public void setExecId(int execId) {
    this.execId = execId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  public String getUserDefParams() {
    return userDefParams;
  }

  public void setUserDefParams(String userDefParams) {
    this.userDefParams = userDefParams;
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

  public String getSubmitUser() {
    return submitUser;
  }

  public void setSubmitUser(String submitUser) {
    this.submitUser = submitUser;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  public FlowStatus getStatus() {
    return status;
  }

  public void setStatus(FlowStatus status) {
    this.status = status;
  }

  public List<String> getAppLinks() {
    return appLinks;
  }

  public void setAppLinks(List<String> appLinks) {
    this.appLinks = appLinks;
  }

  public NotifyType getNotifyType() {
    return notifyType;
  }

  public void setNotifyType(NotifyType notifyType) {
    this.notifyType = notifyType;
  }

  public String getNotifyMails() {
    return notifyMails;
  }

  public void setNotifyMails(String notifyMails) {
    this.notifyMails = notifyMails;
  }
}
