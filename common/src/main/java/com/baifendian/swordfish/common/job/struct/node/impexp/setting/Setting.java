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

/**
 * 其他配置设定
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Setting {

  /**
   * 速度控制
   */
  private Speed speed;
  /**
   * 脏数据控制
   */
  private ErrorLimit errorLimit;

  public Setting(Speed speed, ErrorLimit errorLimit) {
    this.speed = speed;
    this.errorLimit = errorLimit;
  }

  public Setting(Speed speed) {
    this.speed = speed;
  }

  public Setting() {
  }

  public Speed getSpeed() {
    return speed;
  }

  public void setSpeed(Speed speed) {
    this.speed = speed;
  }

  public ErrorLimit getErrorLimit() {
    return errorLimit;
  }

  public void setErrorLimit(ErrorLimit errorLimit) {
    this.errorLimit = errorLimit;
  }

  public boolean checkValid() {
    return speed != null && speed.checkValid();
  }
}
