package com.baifendian.swordfish.common.job.struct.node.impexp.setting;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 异常阈值
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorLimit {
  /**
   * 数据错误数阈值
   */
  private Long recod;
  /**
   * 数据错误百分比阈值
   */
  private Double percentage;

  public Long getRecod() {
    return recod;
  }

  public void setRecod(Long recod) {
    this.recod = recod;
  }

  public Double getPercentage() {
    return percentage;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }
}
