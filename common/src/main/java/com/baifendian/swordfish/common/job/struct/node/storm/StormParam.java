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
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.storm.param.IStormParam;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * storm 任务参数
 */
public class StormParam extends BaseParam {
  private StormType type;
  private String topologyName;
  private IStormParam stormParam;

  public StormParam() {
  }

  public StormType getType() {
    return type;
  }

  public void setType(StormType type) {
    this.type = type;
  }

  public String getTopologyName() {
    return topologyName;
  }

  public void setTopologyName(String topologyName) {
    this.topologyName = topologyName;
  }

  public IStormParam getStormParam() {
    return stormParam;
  }

  public void setStormParam(IStormParam stormParam) {
    this.stormParam = stormParam;
  }

  @Override
  public boolean checkValid() {
    return type != null &&
            StringUtils.isNotEmpty(topologyName) &&
            stormParam != null &&
            stormParam.checkValid();
  }

  @Override
  public List<String> getProjectResourceFiles() {
    return stormParam.getProjectResourceFiles();
  }
}
