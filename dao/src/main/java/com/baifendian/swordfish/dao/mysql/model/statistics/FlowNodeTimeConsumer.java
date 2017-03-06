package com.baifendian.swordfish.dao.mysql.model.statistics;

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.dao.mysql.enums.NodeType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 *  工作流和任务耗时排行
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月29日
 */
public class FlowNodeTimeConsumer {

    private Long execId;

    private int flowId;

    private String flowName;

    private FlowType flowType;

    private int nodeId;

    private String nodeName;

    private NodeType nodeType;

    private int submitUser;

    private String submitUserName;

    @JsonFormat(pattern = Constants.BASE_DATETIME_FORMAT)
    private Date startTime;

    @JsonFormat(pattern = Constants.BASE_DATETIME_FORMAT)
    private Date endTime;

    private int duration;

    private FlowStatus status;

    public Long getExecId() {
        return execId;
    }

    public void setExecId(Long execId) {
        this.execId = execId;
    }

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public FlowStatus getStatus() {
        return status;
    }

    public void setStatus(FlowStatus status) {
        this.status = status;
    }
}
