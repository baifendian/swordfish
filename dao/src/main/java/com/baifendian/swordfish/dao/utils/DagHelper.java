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
import com.baifendian.swordfish.dao.utils.json.JsonUtil;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Dag 相关工具类
 * <p>
 * 
 */
public class DagHelper {

    /**
     * 获取节点依赖(或被依赖)的DAG
     * <p>
     *
     * @param flowDag
     *            DAG
     * @param isDependentBy
     *            是否被依赖
     * @return {@link FlowDag}
     */
    public static FlowDag findNodeDepDag(FlowDag flowDag, FlowNode node, boolean isDependentBy) {
        FlowDag dagRequestDep = new FlowDag(); // 待返回的新DAG
        List<FlowNode> nodesDep = new ArrayList<>();
        dagRequestDep.setNodes(nodesDep);
        List<FlowNodeRelation> edgesDep = new ArrayList<>();
        dagRequestDep.setEdges(edgesDep);

        List<FlowNode> nodesTemp = new ArrayList<>(); // 待处理的节点列表
        List<FlowNode> nodesTempSwap = new ArrayList<>(); // 临时存储节点信息
        List<FlowNodeRelation> edges = flowDag.getEdges(); // DAG的边列表
        nodesTemp.add(node);
        while (!nodesTemp.isEmpty()) {
            Iterator<FlowNode> iterator = nodesTemp.iterator();
            while (iterator.hasNext()) {
                FlowNode flowNode = iterator.next();
                for (FlowNodeRelation edge : edges) {
                    if (isDependentBy) { // 查询被依赖的 DAG
                        if (edge.getStartNode().equals(flowNode.getName())) { // 当前node依赖的边
                            edgesDep.add(edge);
                            FlowNode endFlowNode = findNodeByName(flowDag.getNodes(), edge.getEndNode());
                            if (endFlowNode != null) {
                                if (!nodesDep.contains(endFlowNode)) { // 没有处理过的节点
                                    nodesTempSwap.add(endFlowNode);
                                }
                            } else {
                                throw new DagException(String.format("DAG error ，end node %s not in flow dag", edge.getEndNode()));// 节点可能被删除了，报错处理
                            }
                        }
                    } else { // 查询依赖的 DAG
                        if (edge.getEndNode().equals(flowNode.getName())) { // 当前node依赖的边
                            edgesDep.add(edge);
                            FlowNode startFlowNode = findNodeByName(flowDag.getNodes(), edge.getStartNode());
                            if (startFlowNode != null) {
                                if (!nodesDep.contains(startFlowNode)) { // 没有处理过的节点
                                    nodesTempSwap.add(startFlowNode);
                                }
                            } else {
                                throw new DagException(String.format("DAG error ，end node %s not in flow dag", edge.getStartNode()));// 节点可能被删除了，报错处理
                            }
                        }
                    }
                }
                nodesDep.add(flowNode); // 处理完成的节点加入到“依赖的节点列表中”
            }
            // 处理完一组节点后，更新待处理的节点
            nodesTemp.clear();
            nodesTemp.addAll(nodesTempSwap);
            nodesTempSwap.clear();
        }

        return dagRequestDep;
    }

    /**
     * 通过 name 获取节点
     * <p>
     *
     * @param nodeDetails
     * @param nodeName
     * @return {@link FlowNode}
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
