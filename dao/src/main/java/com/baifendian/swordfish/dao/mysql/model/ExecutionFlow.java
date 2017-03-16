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

package com.baifendian.swordfish.dao.mysql.model;

import com.baifendian.swordfish.dao.mysql.enums.FlowErrorCode;
import com.baifendian.swordfish.dao.mysql.enums.FlowRunType;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.mysql.enums.FlowType;

import java.util.Date;

/**
 * workflow 执行的信息
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月29日
 */
public class ExecutionFlow {
    /** 具体执行的id **/
    private Long id;

    /** workflow的id **/
    private int flowId;

    /** workflow名称 **/
    private String flowName;

    /** worker的host **/
    private String worker;

    /** workflow执行的状态 **/
    private FlowStatus status;

    /** 提交用户id **/
    private int submitUser;

    /** 提交用户 **/
    private String submitUserName;

    /** 代理用户 **/
    private String proxyUser;

    /** 提交时间 **/
    private Date submitTime;

    /** 起始时间 **/
    private Date startTime;

    /** 结束时间 **/
    private Date endTime;

    /** workflow的数据 **/
    private String workflowData;

    /** workflow 等运行的类型 **/
    private FlowRunType type;

    /** worklow 的类型 */
    private FlowType flowType;

    /** workflow 所在项目的id */
    private Integer projectId;

    /** workflow 所在项目的名称 */
    private String projectName;

    /** workflow 所在组织的id */
    private Integer orgId;

    /** workflow 所在组织的名称 */
    private String orgName;

    /** 调度时间 **/
    private Date scheduleTime;

    /** 执行的错误码 */
    private FlowErrorCode errorCode;

    /** 作业提交队列 **/
    private String queue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public FlowStatus getStatus() {
        return status;
    }

    public void setStatus(FlowStatus status) {
        this.status = status;
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

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
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

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getWorkflowData() {
        return workflowData;
    }

    public void setWorkflowData(String workflowData) {
        this.workflowData = workflowData;
    }

    public FlowRunType getType() {
        return type;
    }

    public void setType(FlowRunType type) {
        this.type = type;
    }

    public FlowType getFlowType() {
        return flowType;
    }

    public void setFlowType(FlowType flowType) {
        this.flowType = flowType;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * getter method
     * 
     * @see ExecutionFlow#orgName
     * @return the orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * setter method
     * 
     * @see ExecutionFlow#orgName
     * @param orgName
     *            the orgName to set
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public FlowErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(FlowErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }
}
