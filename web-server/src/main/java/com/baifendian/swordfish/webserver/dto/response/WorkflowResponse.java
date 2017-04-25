package com.baifendian.swordfish.webserver.dto.response;

import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.webserver.dto.WorkflowData;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;

/**
 * Created by caojingwei on 2017/4/24.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowResponse {
  private String name;
  private String desc;
  private WorkflowData data;
  private String proxyUser;
  private String queue;
  private Date createTime;
  private Date modifyTime;
  private String owner;
  private String extras;
  private String projectName;

  public WorkflowResponse() {
  }

  public WorkflowResponse(ProjectFlow projectFlow) {
    this.name = projectFlow.getName();
    this.desc = projectFlow.getDesc();
    if (CollectionUtils.isNotEmpty(projectFlow.getFlowsNodes())){
      this.data = new WorkflowData(projectFlow.getFlowsNodes(),projectFlow.getUserDefinedParamList());
    }
    this.proxyUser = projectFlow.getProxyUser();
    this.queue = projectFlow.getQueue();
    this.createTime = projectFlow.getCreateTime();
    this.modifyTime = projectFlow.getModifyTime();
    this.owner = projectFlow.getOwner();
    this.extras = projectFlow.getExtras();
    this.projectName = projectFlow.getProjectName();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public WorkflowData getData() {
    return data;
  }

  public void setData(WorkflowData data) {
    this.data = data;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public void setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(Date modifyTime) {
    this.modifyTime = modifyTime;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getExtras() {
    return extras;
  }

  public void setExtras(String extras) {
    this.extras = extras;
  }
}
