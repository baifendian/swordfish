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
package com.baifendian.swordfish.common.job.struct.node.storm;

import com.baifendian.swordfish.common.enums.StormType;
import com.baifendian.swordfish.common.job.struct.node.storm.param.IStormParam;
import com.baifendian.swordfish.common.job.struct.node.storm.param.StormParamFactory;
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * storm 参数构造器
 */
public class StormBuilder {
  private StormType type;
  private String topologyName;
  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String stormParam;
  private IStormParam iStormParam;

  public StormType getType() {
    return type;
  }

  public void setType(StormType type) {
    this.type = type;
  }

  public void setiStormParam(IStormParam iStormParam) {
    this.iStormParam = iStormParam;
  }

  public String getTopologyName() {
    return topologyName;
  }

  public void setTopologyName(String topologyName) {
    this.topologyName = topologyName;
  }

  public String getStormParam() {
    return stormParam;
  }

  public void setStormParam(String stormParam) {
    this.stormParam = stormParam;
  }

  public IStormParam getiStormParam() {
    return iStormParam;
  }

  public StormParam buildStormParam() {
    iStormParam = StormParamFactory.getStormParam(type, stormParam);
    StormParam stormParam = new StormParam();
    stormParam.setType(type);
    stormParam.setTopologyName(topologyName);
    stormParam.setStormParam(iStormParam);
    return stormParam;
  }
}
