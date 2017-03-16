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
package com.baifendian.swordfish.dao.model.flow;

import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.FlowNodeRelation;

import java.util.List;

/**
 * workflow dag 信息 <p>
 *
 * @author : dsfan
 * @date : 2016年8月29日
 */
public class FlowDag {

  /**
   * DAG 边信息
   **/
  private List<FlowNodeRelation> edges;

  /**
   * 表示 node 的内容详情
   */
  private List<FlowNode> nodes;

  /**
   * getter method
   *
   * @return the edges
   * @see FlowDag#edges
   */
  public List<FlowNodeRelation> getEdges() {
    return edges;
  }

  /**
   * setter method
   *
   * @param edges the edges to set
   * @see FlowDag#edges
   */
  public void setEdges(List<FlowNodeRelation> edges) {
    this.edges = edges;
  }

  /**
   * getter method
   *
   * @return the nodes
   * @see FlowDag#nodes
   */
  public List<FlowNode> getNodes() {
    return nodes;
  }

  /**
   * setter method
   *
   * @param nodes the nodes to set
   * @see FlowDag#nodes
   */
  public void setNodes(List<FlowNode> nodes) {
    this.nodes = nodes;
  }

}
