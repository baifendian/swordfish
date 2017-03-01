package com.baifendian.swordfish.dao.mysql.model.statistics;

import java.util.Date;

/**
 *  查询一个工作流平均耗时时间
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年10月8日
 */
public class FlowAvgTime {

    private int hour;

    private Date day;

    private int duration;

    private int num;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
