/*
 * Create Author  : dsfan
 * Create Date    : 2016年8月29日
 * File Name      : FlowDagRequest.java
 */

package com.baifendian.swordfish.dao.mysql.model.flow;

import com.baifendian.swordfish.dao.mysql.model.FlowNode;
import com.baifendian.swordfish.dao.mysql.model.FlowNodeRelation;

import java.util.List;

/**
 * workflow dag 信息
 * <p>
 * 
 * @author : dsfan
 * @date : 2016年8月29日
 */
public class FlowDag {

    /** DAG 边信息 **/
    private List<FlowNodeRelation> edges;

    /** 表示 node 的内容详情 */
    private List<FlowNode> nodes;

    /**
     * getter method
     * 
     * @see FlowDag#edges
     * @return the edges
     */
    public List<FlowNodeRelation> getEdges() {
        return edges;
    }

    /**
     * setter method
     * 
     * @see FlowDag#edges
     * @param edges
     *            the edges to set
     */
    public void setEdges(List<FlowNodeRelation> edges) {
        this.edges = edges;
    }

    /**
     * getter method
     * 
     * @see FlowDag#nodes
     * @return the nodes
     */
    public List<FlowNode> getNodes() {
        return nodes;
    }

    /**
     * setter method
     * 
     * @see FlowDag#nodes
     * @param nodes
     *            the nodes to set
     */
    public void setNodes(List<FlowNode> nodes) {
        this.nodes = nodes;
    }

}
