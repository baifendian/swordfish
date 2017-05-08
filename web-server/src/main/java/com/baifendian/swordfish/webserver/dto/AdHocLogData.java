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

import com.baifendian.swordfish.common.json.JsonOrdinalSerializer;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class AdHocLogData {
  /**
   * 工作量的状态
   */
  @JsonSerialize(using = JsonOrdinalSerializer.class)
  private FlowStatus status;

  /**
   * 是否最后一条 sql
   */
  private boolean lastSql = false;

  /**
   * 是否具有结果
   */
  private boolean hasResult = false;

  /**
   * 日志信息
   */
  private LogResult logContent = null;

  public AdHocLogData() {
    status = FlowStatus.INIT;
  }

  public FlowStatus getStatus() {
    return status;
  }

  public void setStatus(FlowStatus status) {
    this.status = status;
  }

  public boolean isLastSql() {
    return lastSql;
  }

  public void setLastSql(boolean lastSql) {
    this.lastSql = lastSql;
  }

  public boolean isHasResult() {
    return hasResult;
  }

  public void setHasResult(boolean hasResult) {
    this.hasResult = hasResult;
  }

  public LogResult getLogContent() {
    return logContent;
  }

  public void setLogContent(LogResult logContent) {
    this.logContent = logContent;
  }
}
