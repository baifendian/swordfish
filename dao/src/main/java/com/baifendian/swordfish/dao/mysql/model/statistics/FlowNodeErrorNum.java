package com.baifendian.swordfish.dao.mysql.model.statistics;

import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.dao.mysql.enums.NodeType;

/**
 *  工作流和任务出错排行
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月29日
 */
public class FlowNodeErrorNum {

    private int flowId;

    private String flowName;

    private FlowType flowType;

    private int nodeId;

    private String nodeName;

    private NodeType nodeType;

    private int submitUser;

    private String submitUserName;

    private int num;

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public FlowType getFlowType() {
        return flowType;
    }

    public void setFlowType(FlowType flowType) {
        this.flowType = flowType;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public int getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(int submitUser) {
        this.submitUser = submitUser;
    }

    public String getSubmitUserName() {
        return submitUserName;
    }

    public void setSubmitUserName(String submitUserName) {
        this.submitUserName = submitUserName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
