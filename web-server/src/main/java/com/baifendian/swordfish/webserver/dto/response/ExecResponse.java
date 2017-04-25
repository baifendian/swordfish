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
package com.baifendian.swordfish.webserver.dto.response;

import java.util.List;

/**
 * 调度系统基本返回
 */
public class ExecResponse extends BaseResponse {
  private List<Integer> execIds;
  private Integer execId;

  public List<Integer> getExecIds() {
    return execIds;
  }

  public void setExecIds(List<Integer> execIds) {
    this.execIds = execIds;
  }

  public Integer getExecId() {
    return execId;
  }

  public void setExecId(Integer execId) {
    this.execId = execId;
  }
}
