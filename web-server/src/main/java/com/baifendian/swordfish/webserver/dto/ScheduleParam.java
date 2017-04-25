package com.baifendian.swordfish.webserver.dto;

import java.util.Date;

/**
 * Created by caojingwei on 2017/4/25.
 */
public class ScheduleParam {
  private Date startDate;
  private Date endDate;
  private String crontab;

  public ScheduleParam() {
  }

  public ScheduleParam(Date startDate, Date endDate, String crontab) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.crontab = crontab;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getCrontab() {
    return crontab;
  }

  public void setCrontab(String crontab) {
    this.crontab = crontab;
  }
}
