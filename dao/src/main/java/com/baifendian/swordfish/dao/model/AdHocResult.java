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
   * 执行 id
   */
  private long execId;

  /**
   * 节点 id
   */
  private int nodeId;

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

  /**
   * getter method
   *
   * @return the execId
   * @see AdHocResult#execId
   */
  public long getExecId() {
    return execId;
  }

  /**
   * setter method
   *
   * @param execId the execId to set
   * @see AdHocResult#execId
   */
  public void setExecId(long execId) {
    this.execId = execId;
  }

  /**
   * getter method
   *
   * @return the nodeId
   * @see AdHocResult#nodeId
   */
  public int getNodeId() {
    return nodeId;
  }

  /**
   * setter method
   *
   * @param nodeId the nodeId to set
   * @see AdHocResult#nodeId
   */
  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * getter method
   *
   * @return the index
   * @see AdHocResult#index
   */
  public int getIndex() {
    return index;
  }

  /**
   * setter method
   *
   * @param index the index to set
   * @see AdHocResult#index
   */
  public void setIndex(int index) {
    this.index = index;
  }

  public FlowStatus getStatus() {
    return status;
  }

  public void setStatus(FlowStatus status) {
    this.status = status;
  }

  /**
   * getter method
   *
   * @return the stm
   * @see AdHocResult#stm
   */
  public String getStm() {
    return stm;
  }

  /**
   * setter method
   *
   * @param stm the stm to set
   * @see AdHocResult#stm
   */
  public void setStm(String stm) {
    this.stm = stm;
  }

  /**
   * getter method
   *
   * @return the result
   * @see AdHocResult#result
   */
  public String getResult() {
    return result;
  }

  /**
   * setter method
   *
   * @param result the result to set
   * @see AdHocResult#result
   */
  public void setResult(String result) {
    this.result = result;
  }

  /**
   * getter method
   *
   * @return the createTime
   * @see AdHocResult#createTime
   */
  public Timestamp getCreateTime() {
    return createTime;
  }

  /**
   * setter method
   *
   * @param createTime the createTime to set
   * @see AdHocResult#createTime
   */
  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime;
  }

}
