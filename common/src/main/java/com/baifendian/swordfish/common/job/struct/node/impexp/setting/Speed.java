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
package com.baifendian.swordfish.common.job.struct.node.impexp.setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DataX速度配置
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Speed {
  /**
   * 最大并发数
   */
  private Integer channel;
  /**
   * 最大流量
   */
  @JsonProperty("byte")
  private Long byteInt;

  public int getChannel() {
    return channel;
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  public Long getByteInt() {
    return byteInt;
  }

  public void setByteInt(Long byteInt) {
    this.byteInt = byteInt;
  }

  public boolean checkValid() {
    return channel != null && byteInt != null;
  }
}
