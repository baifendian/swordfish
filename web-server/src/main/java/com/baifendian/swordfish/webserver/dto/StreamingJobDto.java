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

import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.model.StreamingJob;
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

public class StreamingJobDto {

  private String name;

  private String desc;

  private String projectName;

  private String type;

  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String parameter;

  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String userDefParams;

  private Date createTime;

  private Date modifyTime;

  private String owner;

  private NotifyType notifyType;

  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String notifyMails;

  public StreamingJobDto() {
  }

  public StreamingJobDto(StreamingJob streamingJob) {
    if (streamingJob != null) {
      this.name = streamingJob.getName();
      this.desc = streamingJob.getDesc();
      this.projectName = streamingJob.getProjectName();
      this.type = streamingJob.getType();
      this.parameter = streamingJob.getParameter();
      this.userDefParams = streamingJob.getUserDefinedParams();
      this.createTime = streamingJob.getCreateTime();
      this.modifyTime = streamingJob.getModifyTime();
      this.owner = streamingJob.getOwner();
      this.notifyType = streamingJob.getNotifyType();
      this.notifyMails = streamingJob.getNotifyMails();
    }
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