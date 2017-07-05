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

import com.baifendian.swordfish.common.job.struct.node.BaseParamFactory;
import com.baifendian.swordfish.common.job.struct.node.adhoc.AdHocParam;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;

import java.util.Date;

/**
 * 即席查询记录DTO
 */
public class AdHocDto {
  private int execId;
  private Date startTime;
  private Date endTime;
  private String stms;
  private FlowStatus status;
  private String proxyUser;
  private String queue;

  public AdHocDto() {
  }

  public AdHocDto(AdHoc adHoc) {
    execId = adHoc.getId();
    startTime = adHoc.getStartTime();
    endTime = adHoc.getEndTime();
    AdHocParam adHocParam = JsonUtil.parseObject(adHoc.getParameter(), AdHocParam.class);
    stms = adHocParam.getStms();
    status = adHoc.getStatus();
    proxyUser = adHoc.getProxyUser();
    queue = adHoc.getQueue();
  }

  public int getExecId() {
    return execId;
  }

  public void setExecId(int execId) {
    this.execId = execId;
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

  public String getStms() {
    return stms;
  }

  public void setStms(String stms) {
    this.stms = stms;
  }

  public FlowStatus getStatus() {
    return status;
  }

  public void setStatus(FlowStatus status) {
    this.status = status;
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
}
