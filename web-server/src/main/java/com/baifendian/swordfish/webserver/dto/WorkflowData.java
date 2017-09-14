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
import com.baifendian.swordfish.dao.model.flow.Property;
import java.util.ArrayList;
import java.util.List;

/**
 * workflowData 节点
 */
public class WorkflowData {
  /**
   * 结点信息
   */
  private List<WorkflowNodeDto> nodes;

  /**
   * 用户自定义参数
   */
  private List<Property> userDefParams;

  public WorkflowData() {
  }

  /**
   * 支持通过数据库flowNode实体 或 DTO workflowNodeDTO 的方式构建
   *
   * @param nodes
   * @param userDefParams
   * @param clazz
   */
  public WorkflowData(List<?> nodes, List<Property> userDefParams, Class<?> clazz) {
    if (clazz == WorkflowNodeDto.class) {
      this.nodes = (List<WorkflowNodeDto>) nodes;
    } else if (clazz == FlowNode.class) {
      List<WorkflowNodeDto> workflowNodeDTOList = new ArrayList<>();

      for (Object node : nodes) {
        workflowNodeDTOList.add(new WorkflowNodeDto((FlowNode) node));
      }

      this.nodes = workflowNodeDTOList;
    }

    this.userDefParams = userDefParams;
  }

  /**
   * @param nodes
   * @param userDefParams
   */
  public WorkflowData(List<WorkflowNodeDto> nodes, List<Property> userDefParams) {
    this(nodes, userDefParams, WorkflowNodeDto.class);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    WorkflowData that = (WorkflowData) o;

    return EqualUtils.equalLists(nodes, that.nodes) &&
        EqualUtils.equalLists(userDefParams, that.userDefParams);
  }

  public List<WorkflowNodeDto> getNodes() {
    return nodes;
  }

  public void setNodes(List<WorkflowNodeDto> nodes) {
    this.nodes = nodes;
  }

  public List<Property> getUserDefParams() {
    return userDefParams;
  }

  public void setUserDefParams(List<Property> userDefParams) {
    this.userDefParams = userDefParams;
  }
}
