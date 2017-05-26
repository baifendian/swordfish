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

import java.util.List;

public class StreamingResultsDto {
  private int total;
  private int length;
  private List<StreamingResultDto> executions;

  public StreamingResultsDto() {
  }

  public StreamingResultsDto(int total, List<StreamingResultDto> executions) {
    this.total = total;
    this.length = executions.size();
    this.executions = executions;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public List<StreamingResultDto> getExecutions() {
    return executions;
  }

  public void setExecutions(List<StreamingResultDto> executions) {
    this.executions = executions;
  }
}
