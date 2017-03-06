package com.baifendian.swordfish.dao.mysql.model.flow;

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.dao.mysql.enums.ScheduleType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * schedule的基本
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月26日
 */
public class ScheduleMeta {

    private int startDate;

    private int endDate;

    private ScheduleType type;

    private String crontab;

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public ScheduleType getType() {
        return type;
    }

    public void setType(ScheduleType type) {
        this.type = type;
    }

    public String getCrontab() {
        return crontab;
    }

    public void setCrontab(String crontab) {
        this.crontab = crontab;
    }
}
