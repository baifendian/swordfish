package com.baifendian.swordfish.common.job.struct.node.storm.dto;

import java.util.List;

/**
 * Created by caojingwei on 2017/7/15.
 */
public class TopologySummaryDto {
  private List<TopologyDto> topologies;
  private boolean schedulerDisplayResource;

  public List<TopologyDto> getTopologies() {
    return topologies;
  }

  public void setTopologies(List<TopologyDto> topologies) {
    this.topologies = topologies;
  }

  public boolean isSchedulerDisplayResource() {
    return schedulerDisplayResource;
  }

  public void setSchedulerDisplayResource(boolean schedulerDisplayResource) {
    this.schedulerDisplayResource = schedulerDisplayResource;
  }
}
