package com.baifendian.swordfish.common.job.struct.node.storm.dto;

/**
 * Created by caojingwei on 2017/7/15.
 */
public class TopologyDto {
  private String id;
  private String name;
  private String status;
  private String uptime;
  private Integer tasksTotal;
  private Integer workersTotal;
  private Integer executorsTotal;
  private Integer replicationCount;
  private Double requestedMemOnHeap;
  private Double requestedMemOffHeap;
  private Double requestedTotalMem;
  private Double requestedCpu;
  private Double assignedMemOnHeap;
  private Double assignedMemOffHeap;
  private Double assignedTotalMem;
  private Double assignedCpu;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUptime() {
    return uptime;
  }

  public void setUptime(String uptime) {
    this.uptime = uptime;
  }

  public Integer getTasksTotal() {
    return tasksTotal;
  }

  public void setTasksTotal(Integer tasksTotal) {
    this.tasksTotal = tasksTotal;
  }

  public Integer getWorkersTotal() {
    return workersTotal;
  }

  public void setWorkersTotal(Integer workersTotal) {
    this.workersTotal = workersTotal;
  }

  public Integer getExecutorsTotal() {
    return executorsTotal;
  }

  public void setExecutorsTotal(Integer executorsTotal) {
    this.executorsTotal = executorsTotal;
  }

  public Integer getReplicationCount() {
    return replicationCount;
  }

  public void setReplicationCount(Integer replicationCount) {
    this.replicationCount = replicationCount;
  }

  public Double getRequestedMemOnHeap() {
    return requestedMemOnHeap;
  }

  public void setRequestedMemOnHeap(Double requestedMemOnHeap) {
    this.requestedMemOnHeap = requestedMemOnHeap;
  }

  public Double getRequestedMemOffHeap() {
    return requestedMemOffHeap;
  }

  public void setRequestedMemOffHeap(Double requestedMemOffHeap) {
    this.requestedMemOffHeap = requestedMemOffHeap;
  }

  public Double getRequestedTotalMem() {
    return requestedTotalMem;
  }

  public void setRequestedTotalMem(Double requestedTotalMem) {
    this.requestedTotalMem = requestedTotalMem;
  }

  public Double getRequestedCpu() {
    return requestedCpu;
  }

  public void setRequestedCpu(Double requestedCpu) {
    this.requestedCpu = requestedCpu;
  }

  public Double getAssignedMemOnHeap() {
    return assignedMemOnHeap;
  }

  public void setAssignedMemOnHeap(Double assignedMemOnHeap) {
    this.assignedMemOnHeap = assignedMemOnHeap;
  }

  public Double getAssignedMemOffHeap() {
    return assignedMemOffHeap;
  }

  public void setAssignedMemOffHeap(Double assignedMemOffHeap) {
    this.assignedMemOffHeap = assignedMemOffHeap;
  }

  public Double getAssignedTotalMem() {
    return assignedTotalMem;
  }

  public void setAssignedTotalMem(Double assignedTotalMem) {
    this.assignedTotalMem = assignedTotalMem;
  }

  public Double getAssignedCpu() {
    return assignedCpu;
  }

  public void setAssignedCpu(Double assignedCpu) {
    this.assignedCpu = assignedCpu;
  }
}
