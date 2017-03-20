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
package com.baifendian.swordfish.dao.model;

import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.dao.utils.json.StringNodeJsonDeserializer;
import com.baifendian.swordfish.dao.utils.json.StringNodeJsonSerializer;
import com.baifendian.swordfish.dao.model.flow.EdgeAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 已发布workflow节点关系信息 <p>
 *
 * @author : wenting.wang
 * @author : dsfan
 * @date : 2016年8月24日
 */
public class FlowNodeRelation {

  private int flowId;

  private int startId;

  private int endId;

  @JsonDeserialize(using = StringNodeJsonDeserializer.class)
  @JsonSerialize(using = StringNodeJsonSerializer.class)
  private String attribute;

  @JsonIgnore
  private EdgeAttribute attributeObject;

  public int getFlowId() {
    return flowId;
  }

  public void setFlowId(int flowId) {
    this.flowId = flowId;
  }

  public int getStartId() {
    return startId;
  }

  public void setStartId(int startId) {
    this.startId = startId;
  }

  public int getEndId() {
    return endId;
  }

  public void setEndId(int endId) {
    this.endId = endId;
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  /**
   * 获取 EdgeAttribute 对象 <p>
   *
   * @return EdgeAttribute 对象
   */
  public EdgeAttribute getAttributeObject() {
    if (attributeObject == null) {
      attributeObject = JsonUtil.parseObject(attribute, EdgeAttribute.class);
    }
    return attributeObject;
  }

}
