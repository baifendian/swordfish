package com.baifendian.swordfish.dao.mysql.model.statistics;

import com.baifendian.swordfish.common.job.FlowStatus;

import java.util.Date;

/**
 *  flow 执行状态统计
 * <p>
 *
 * @author : wenting.wang
 * @date : 2016年9月27日
 */
public class FlowStatusNum {

    private FlowStatus status;

    private int num;

    private int hour;

    private Date day;

    public FlowStatus getStatus() {
        return status;
    }

    public void setStatus(FlowStatus status) {
        this.status = status;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

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
}
