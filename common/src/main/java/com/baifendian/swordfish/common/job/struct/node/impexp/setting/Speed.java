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
  private int channel;
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
}
