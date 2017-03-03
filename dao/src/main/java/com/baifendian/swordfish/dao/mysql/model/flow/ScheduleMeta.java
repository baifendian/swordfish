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

    // comment by qifeng.dai(用: BASE_DATE_FORMAT 更加合适?)
    private int startDate;

    private int endDate;

    private ScheduleType type;

    private int startTime;

    private int endTime;

    private int interval;

    private List<Integer> weekDays;

    private List<Integer> monthDays;


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

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public List<Integer> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<Integer> weekDays) {
        this.weekDays = weekDays;
    }

    public List<Integer> getMonthDays() {
        return monthDays;
    }

    public void setMonthDays(List<Integer> monthDays) {
        this.monthDays = monthDays;
    }
}
