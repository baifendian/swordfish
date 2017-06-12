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
}
