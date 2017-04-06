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
package com.baifendian.swordfish.webserver.api.dto;

import com.baifendian.swordfish.dao.model.AdHocJsonObject;

import java.util.Date;

public class AdHocResultData {

  /**
   * start time of exec
   */
  private Date startTime;

  /**
   * end time of exec
   */
  private Date endTime;

  /**
   * 具体的语句
   */
  private String stm;

  /**
   * 结果
   */
  private AdHocJsonObject results;

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

  public String getStm() {
    return stm;
  }

  public void setStm(String stm) {
    this.stm = stm;
  }

  public AdHocJsonObject getResults() {
    return results;
  }

  public void setResults(AdHocJsonObject results) {
    this.results = results;
  }
}
