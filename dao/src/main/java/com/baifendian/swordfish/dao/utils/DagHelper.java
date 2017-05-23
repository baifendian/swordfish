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
package com.baifendian.swordfish.dao.utils;

import com.baifendian.swordfish.dao.exception.DagException;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.FlowNodeRelation;
import com.baifendian.swordfish.dao.model.flow.FlowDag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Dag 相关工具类
 * <p>
 */
public class DagHelper {

  /**
   * 获取节点依赖(或被依赖)的 DAG <p>
   *
   * @param flowDag       整个工作流 DAG
   * @param node          查询的节点
   * @param isDependentBy 为 true 表示查询 node 的后续, 为 false 表示前续
   * @return
   */
  public static FlowDag findNodeDepDag(FlowDag flowDag, FlowNode node, boolean isDependentBy) {
    FlowDag dagRequestDep = new FlowDag();
    List<FlowNode> nodesDep = new ArrayList<>();

    dagRequestDep.setNodes(nodesDep);

    List<FlowNodeRelation> edgesDep = new ArrayList<>();
    dagRequestDep.setEdges(edgesDep);

    // 待处理的节点列表
    List<FlowNode> nodesTemp = new ArrayList<>();
    // 临时存储节点信息
    List<FlowNode> nodesTempSwap = new ArrayList<>();
    // DAG的边列表
    List<FlowNodeRelation> edges = flowDag.getEdges();

    nodesTemp.add(node);

    while (!nodesTemp.isEmpty()) {
      Iterator<FlowNode> iterator = nodesTemp.iterator();

      while (iterator.hasNext()) {
        FlowNode flowNode = iterator.next();
        for (FlowNodeRelation edge : edges) {
          // 查询被依赖的 DAG
          if (isDependentBy) {
            // 当前 node 依赖的边
            if (edge.getStartNode().equals(flowNode.getName())) {
              edgesDep.add(edge);
              FlowNode endFlowNode = findNodeByName(flowDag.getNodes(), edge.getEndNode());
              if (endFlowNode != null) {
                // 没有处理过的节点
                if (!nodesDep.contains(endFlowNode)) {
                  nodesTempSwap.add(endFlowNode);
                }
              } else {
                // 节点可能被删除了，报错处理
                throw new DagException(String.format("DAG error ，end node %s not in flow dag", edge.getEndNode()));
              }
            }
          } else { // 查询依赖的 DAG
            // 当前node依赖的边
            if (edge.getEndNode().equals(flowNode.getName())) {
              edgesDep.add(edge);
              FlowNode startFlowNode = findNodeByName(flowDag.getNodes(), edge.getStartNode());

              if (startFlowNode != null) {
                // 没有处理过的节点
                if (!nodesDep.contains(startFlowNode)) {
                  nodesTempSwap.add(startFlowNode);
                }
              } else {
                // 节点可能被删除了，报错处理
                throw new DagException(String.format("DAG error ，end node %s not in flow dag", edge.getStartNode()));
              }
            }
          }
        }

        // 处理完成的节点加入到 “依赖的节点列表中”
        nodesDep.add(flowNode);
      }

      // 处理完一组节点后，更新待处理的节点
      nodesTemp.clear();
      nodesTemp.addAll(nodesTempSwap);
      nodesTempSwap.clear();
    }

    return dagRequestDep;
  }

  /**
   * 通过 name 获取节点 <p>
   *
   * @param nodeDetails
   * @param nodeName
   * @return
   * @see FlowNode
   */
  public static FlowNode findNodeByName(List<FlowNode> nodeDetails, String nodeName) {
    for (FlowNode flowNode : nodeDetails) {
      if (flowNode.getName().equals(nodeName)) {
        return flowNode;
      }
    }

    return null;
  }
}
