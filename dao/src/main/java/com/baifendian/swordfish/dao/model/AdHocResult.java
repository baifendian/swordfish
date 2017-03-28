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

import java.sql.Timestamp;

public class AdHocResult {
  /**
   * ad hoc id
   */
  private long adHocId;

  /**
   * 执行语句在查询语句数组中的索引
   */
  private int index;

  private FlowStatus status;

  /**
   * 执行语句
   */
  private String stm;

  /**
   * 执行结果（JsonObject）
   */
  private String result;

  /**
   * 创建时间
   */
  private Timestamp createTime;

  public long getAdHocId() {
    return adHocId;
  }

  public void setAdHocId(long adHocId) {
    this.adHocId = adHocId;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public FlowStatus getStatus() {
    return status;
  }

  public void setStatus(FlowStatus status) {
    this.status = status;
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

  public Timestamp getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime;
  }
}
