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

import com.baifendian.swordfish.dao.model.flow.params.Property;
import com.baifendian.swordfish.webserver.dto.response.ExecutionNodeResponse;

import java.util.List;

/**
 * 执行flow node节点信息DTO
 */
public class ExecutionFlowData {
  private List<ExecutionNodeResponse> nodes;

  private List<Property> userDefParams;

  public ExecutionFlowData() {
  }

  public ExecutionFlowData(List<ExecutionNodeResponse> nodes, List<Property> userDefParams) {
    this.nodes = nodes;
    this.userDefParams = userDefParams;
  }

  public List<ExecutionNodeResponse> getNodes() {
    return nodes;
  }

  public void setNodes(List<ExecutionNodeResponse> nodes) {
    this.nodes = nodes;
  }

  public List<Property> getUserDefParams() {
    return userDefParams;
  }

  public void setUserDefParams(List<Property> userDefParams) {
    this.userDefParams = userDefParams;
  }
}
