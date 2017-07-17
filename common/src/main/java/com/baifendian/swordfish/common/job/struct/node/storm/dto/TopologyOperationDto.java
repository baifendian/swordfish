package com.baifendian.swordfish.common.job.struct.node.storm.dto;

/**
 * Created by caojingwei on 2017/7/15.
 */
public class TopologyOperationDto {
  private String topologyOperation;
  private String topologyId;
  private String status;

  public String getTopologyOperation() {
    return topologyOperation;
  }

  public void setTopologyOperation(String topologyOperation) {
    this.topologyOperation = topologyOperation;
  }

  public String getTopologyId() {
    return topologyId;
  }

  public void setTopologyId(String topologyId) {
    this.topologyId = topologyId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
