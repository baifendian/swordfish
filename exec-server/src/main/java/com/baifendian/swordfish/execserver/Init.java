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
package com.baifendian.swordfish.execserver;

import com.baifendian.swordfish.common.job.Job;
import com.baifendian.swordfish.common.job.JobProps;
import com.baifendian.swordfish.common.utils.DateUtils;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.FlowRunType;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.flow.ScheduleMeta;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.JobTypeManager;
import com.baifendian.swordfish.execserver.service.ExecServiceImpl;
import com.baifendian.swordfish.rpc.ScheduleInfo;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.thrift.TException;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;

/**
 * 测试，数据初始化工具类
 */
public class Init {
  public static void initFlow() {
    FlowDao flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    ExecutionFlow executionFlow = flowDao.scheduleFlowToExecution(1, 3, 1, new Date(), FlowRunType.DISPATCH, 3, 3*3600);
    System.out.println(executionFlow.getId());
  }

  public static void initSchedule() {
    MasterClient masterClient = new MasterClient("172.18.1.22", 9999, 3);
    ScheduleInfo scheduleInfo = new ScheduleInfo();
    scheduleInfo.setStartDate(System.currentTimeMillis() - 3600 * 24 * 1000);
    scheduleInfo.setEndDate(4101494400000l);
    scheduleInfo.setCronExpression("1 40 * * * ?");
    masterClient.setSchedule(1, 1, scheduleInfo);
  }

  public static void testJob() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    JobProps props = new JobProps();
    props.setJobParams("{\"script\":\"ls -l\"}");
    Logger logger = LoggerFactory.getLogger(Init.class);
    Job job = JobTypeManager.newJob("NODE_1", "VIRTUAL", props, logger);
  }

  public static void runFlow() throws TException {
    Configuration conf = new PropertiesConfiguration();
    ExecServiceImpl impl = new ExecServiceImpl("127.0.0.1", 7777, conf);
    impl.execFlow(779);
  }
  public static void execFlow(int id) throws TException {
    MasterClient masterClient = new MasterClient("172.18.1.22",9999, 3);
    masterClient.execFlow(id);
  }

  public static void cancelExecFlow(int id) throws TException {
    MasterClient masterClient = new MasterClient("172.18.1.22",9999, 3);
    masterClient.cancelExecFlow(id);
  }

  public static void appendWorkFlow() throws TException {
    MasterClient masterClient = new MasterClient("172.18.1.22",9999, 3);
    ScheduleMeta scheduleMeta = new ScheduleMeta();
    scheduleMeta.setStartDate(new Date());
    scheduleMeta.setEndDate(DateUtils.parse("2017-04-05 16:00:00"));
    scheduleMeta.setCrontab("10 */10 * * * ?");
    masterClient.appendWorkFlow(1, 2, JsonUtil.toJsonString(scheduleMeta));
  }

  public static void runAdHoc() throws TException {
    Configuration conf = new PropertiesConfiguration();
    /*
    ExecServiceImpl impl = new ExecServiceImpl("127.0.0.1", 7777, conf);
    impl.execAdHoc(1);
    */
    MasterClient masterClient = new MasterClient("172.18.1.22",9999, 3);
    masterClient.execAdHoc(1);
  }

  public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException, TException, ParseException {
    //Init.initFlow();
    //Init.testJob();
    //Init.initSchedule();
    //Init.runFlow();
    //Init.runAdHoc();
    //Init.appendWorkFlow();
    //Init.execFlow(817);
    Init.cancelExecFlow(817);
    CronExpression cron = new CronExpression("10 * * * * ?");
    Date now = new Date();
    Date date = cron.getTimeAfter(now);
    Date nextInvalidTime = cron.getNextInvalidTimeAfter(now);
    System.out.printf("now %s \nafter %s \ninvalid %s\n", now, date, nextInvalidTime);

  }
}
