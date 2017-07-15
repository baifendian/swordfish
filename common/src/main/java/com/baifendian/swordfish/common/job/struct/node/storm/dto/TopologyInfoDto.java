package com.baifendian.swordfish.common.job.struct.node.storm.dto;

import java.util.List;

/**
 * Created by caojingwei on 2017/7/15.
 */
public class TopologyInfoDto {
  private String name;
  private String id;
  private String status;
  List<TopologyWorkerDto> workers;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<TopologyWorkerDto> getWorkers() {
    return workers;
  }

  public void setWorkers(List<TopologyWorkerDto> workers) {
    this.workers = workers;
  }
}
