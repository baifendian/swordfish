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
package com.baifendian.swordfish.dao.mapper;

import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.enums.ScheduleStatus;
import com.baifendian.swordfish.dao.model.Schedule;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

public class ScheduleMapperTest {

  @Test
  public void testInsert() throws JsonProcessingException {
    ScheduleMapper scheduleMapper = ConnectionFactory.getSqlSession().getMapper(ScheduleMapper.class);
    Schedule schedule = new Schedule();
    schedule.setFlowId(2);
    schedule.setStartDate(new Date());
    schedule.setEndDate(new Date(System.currentTimeMillis() + 24 * 3600 * 1000));
    schedule.setCrontab("10 * * * * ?");
    schedule.setCreateTime(new Date());
    schedule.setNotifyMails(Arrays.asList("aaron.liu@baifendian.com"));
    schedule.setDepPolicy(DepPolicyType.DEP_PRE);
    schedule.setFailurePolicy(FailurePolicyType.CONTINUE);
    schedule.setMaxTryTimes(3);
    schedule.setTimeout(5000);
    schedule.setNotifyType(NotifyType.ALL);
    schedule.setModifyTime(new Date());
    schedule.setOwner("1");
    schedule.setScheduleStatus(ScheduleStatus.OFFLINE);
    scheduleMapper.insert(schedule);
  }
}
