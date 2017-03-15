/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.baifendian.swordfish.webserver;

import com.baifendian.swordfish.dao.mysql.enums.FlowType;
import com.baifendian.swordfish.execserver.MasterClient;
import com.baifendian.swordfish.rpc.ScheduleInfo;

/**
 * @author : liujin
 * @date : 2017-03-13 13:54
 */
public class InitTest {
    public static void main(String[] args) {
        MasterClient masterClient = new MasterClient("172.18.1.22", 9999, 3);
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setStartDate(System.currentTimeMillis()-3600*24*1000);
        scheduleInfo.setEndDate(4101494400000l);
        scheduleInfo.setCronExpression("1 10 * * * ?");
        masterClient.setSchedule(1, 1, FlowType.SHORT.name(), scheduleInfo);
    }
}
