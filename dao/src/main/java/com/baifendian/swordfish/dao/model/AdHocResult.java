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
import java.util.Date;

public class AdHocResult {
  /**
   * exec id
   */
  private int execId;

  /**
   * 即席查询的名称
   */
  private String name;

  /**
   * 执行语句在查询语句数组中的索引
   */
  private int index;

  /**
   * 执行语句
   */
  private String stm;

  /**
   * 执行结果（JsonObject）
   */
  private String result;

  /**
   * 执行状态
   */
  private FlowStatus status;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 开始执行时间
   */
  private Date startTime;

  /**
   * 结束时间
   */
  private Date endTime;

  public int getExecId() {
    return execId;
  }

  public void setExecId(int execId) {
    this.execId = execId;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getStm() {
    return stm;
  }

  public void setStm(String stm) {
    this.stm = stm;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public FlowStatus getStatus() {
    return status;
  }

  public void setStatus(FlowStatus status) {
    this.status = status;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
