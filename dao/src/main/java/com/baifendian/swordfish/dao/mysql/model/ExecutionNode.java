
package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.dao.mysql.enums.FlowStatus;

import java.util.List;

/**
 * Node 执行的信息
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月30日
 */
public class ExecutionNode {

    /** Node 执行id **/
    private Long id;

    /** 具体workflow执行的 id **/
    private Long execId;

    /** workflow的id **/
    private int flowId;

    /** node 的 id **/
    private Integer nodeId;

    /** node 的名称 **/
    private String NodeName;

    /** 对应yarn任务 id **/
    private String appsId;

    /** 运行状态 **/
    private FlowStatus status;

    /** 起始时间 **/
    private int startTime;

    /** 结束时间 **/
    private int endTime;

    /** 尝试次数 **/
    private int attempt;

    /** 执行的job id **/
    private String jobId;

    private List<ExecNodeLog> execNodeLogs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return NodeName;
    }

    public void setNodeName(String nodeName) {
        NodeName = nodeName;
    }

    public String getAppsId() {
        return appsId;
    }

    public void setAppsId(String appsId) {
        this.appsId = appsId;
    }

    public FlowStatus getStatus() {
        return status;
    }

    public void setStatus(FlowStatus status) {
        this.status = status;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public List<ExecNodeLog> getExecNodeLogs() {
        return execNodeLogs;
    }

    public void setExecNodeLogs(List<ExecNodeLog> execNodeLogs) {
        this.execNodeLogs = execNodeLogs;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
