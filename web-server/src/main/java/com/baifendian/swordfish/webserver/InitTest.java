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

package com.baifendian.swordfish.webserver;

import com.baifendian.swordfish.dao.enums.FlowType;
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
