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

import com.baifendian.swordfish.dao.mapper.utils.EqualUtils;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.flow.params.Property;
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.baifendian.swordfish.dao.utils.json.JsonObjectSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Objects;

/**
 * Created by caojingwei on 2017/4/24.
 */
public class WorkflowData {
  /**
   * 结点信息
   */
  private List<FlowNode> nodes;

  /**
   * 用户自定义参数
   */
  private List<Property> userDefParams;

  public WorkflowData() {
  }

  public WorkflowData(List<FlowNode> nodes, List<Property> userDefParams) {
    this.nodes = nodes;
    this.userDefParams = userDefParams;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WorkflowData that = (WorkflowData) o;
    return EqualUtils.equalLists(nodes, that.nodes) &&
            EqualUtils.equalLists(userDefParams, that.userDefParams);
  }

  public List<FlowNode> getNodes() {
    return nodes;
  }

  public void setNodes(List<FlowNode> nodes) {
    this.nodes = nodes;
  }

  public List<Property> getUserDefParams() {
    return userDefParams;
  }

  public void setUserDefParams(List<Property> userDefParams) {
    this.userDefParams = userDefParams;
  }
}
