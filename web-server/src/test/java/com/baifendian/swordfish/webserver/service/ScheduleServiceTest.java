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
package com.baifendian.swordfish.webserver.service;

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.enums.ScheduleStatus;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.RestfulApiApplication;
import com.baifendian.swordfish.webserver.dto.ScheduleParam;
import com.baifendian.swordfish.webserver.dto.ScheduleDto;
import com.baifendian.swordfish.mock.MockDataService;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * schedule service 单元测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RestfulApiApplication.class)
@WebAppConfiguration
@Transactional
public class ScheduleServiceTest {
  private static Logger logger = LoggerFactory.getLogger(ScheduleServiceTest.class.getName());

  @Autowired
  private ScheduleService scheduleService;

  @Autowired
  private MockDataService mockDataService;

  private User user;

  private Project project;

  @Before
  public void setUp() throws Exception {
    user = mockDataService.createGeneralUser();
    project = mockDataService.createProject(user);
  }

  @Test
  public void testCreateSchedule() throws IOException {
    {
      //正常创建一个调度
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      Date now = new Date();
      ScheduleParam scheduleParam = new ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = JsonUtil.toJsonString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = JsonUtil.toJsonString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = JsonUtil.toJsonString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      ScheduleDto scheduleObj = scheduleService.createSchedule(user,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_OK);
      assertTrue(scheduleObj!=null);
      logger.info(JsonUtil.toJsonString(scheduleObj));
    }
    {
      //创建一个不存在的projectFlow的调度
      Date now = new Date();
      ScheduleParam scheduleParam = new ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = JsonUtil.toJsonString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = JsonUtil.toJsonString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = JsonUtil.toJsonString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      ScheduleDto scheduleObj = scheduleService.createSchedule(user,project.getName(),mockDataService.getRandomString(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_NOT_MODIFIED);
      assertEquals(scheduleObj,null);
    }
    {
      //创建一个已经存在的调度
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      mockDataService.mockSchedule(project.getName(),projectFlow.getId(),user.getId());
      Date now = new Date();
      ScheduleParam scheduleParam = new ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = JsonUtil.toJsonString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = JsonUtil.toJsonString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = JsonUtil.toJsonString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      ScheduleDto scheduleObj = scheduleService.createSchedule(user,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_CONFLICT);
      assertEquals(scheduleObj,null);
    }
    {
      //无权限创建一个调度
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(),user1.getId(), Constants.PROJECT_USER_PERM_READ);
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      Date now = new Date();
      ScheduleParam scheduleParam = new ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = JsonUtil.toJsonString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = JsonUtil.toJsonString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = JsonUtil.toJsonString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      ScheduleDto scheduleObj = scheduleService.createSchedule(user1,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_UNAUTHORIZED);
      assertEquals(scheduleObj,null);
    }
  }

  @Test
  public void testPatchSchedule() throws IOException {
    {
      //正常修改一个调度
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      mockDataService.mockSchedule(project.getName(),projectFlow.getId(),user.getId());
      Date now = new Date();
      ScheduleParam scheduleParam = new ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = JsonUtil.toJsonString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = JsonUtil.toJsonString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = JsonUtil.toJsonString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      ScheduleStatus scheduleStatus = ScheduleStatus.ONLINE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      ScheduleDto scheduleObj = scheduleService.patchSchedule(user,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,scheduleStatus);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_OK);
      assertTrue(scheduleObj!=null);
    }
    {
      //修改一个不存在的调度
      Date now = new Date();
      ScheduleParam scheduleParam = new ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = JsonUtil.toJsonString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = JsonUtil.toJsonString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = JsonUtil.toJsonString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      ScheduleStatus scheduleStatus = ScheduleStatus.ONLINE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      ScheduleDto scheduleObj = scheduleService.patchSchedule(user,project.getName(),mockDataService.getRandomString(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,scheduleStatus);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_NOT_MODIFIED);
      assertEquals(scheduleObj,null);
    }
    {
      //无权限修改
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      mockDataService.mockSchedule(project.getName(),projectFlow.getId(),user.getId());
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(),user1.getId(), Constants.PROJECT_USER_PERM_READ);
      Date now = new Date();
      ScheduleParam scheduleParam = new ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = JsonUtil.toJsonString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = JsonUtil.toJsonString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = JsonUtil.toJsonString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      ScheduleStatus scheduleStatus = ScheduleStatus.ONLINE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      ScheduleDto scheduleObj = scheduleService.patchSchedule(user1,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,scheduleStatus);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_UNAUTHORIZED);
      assertEquals(scheduleObj,null);
    }
  }

  @Test
  public void testPutSchedule() throws IOException {
    {
      //修改一个已经存在的调度
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      mockDataService.mockSchedule(project.getName(),projectFlow.getId(),user.getId());
      Date now = new Date();
      ScheduleParam scheduleParam = new ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = JsonUtil.toJsonString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = JsonUtil.toJsonString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = JsonUtil.toJsonString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      ScheduleDto scheduleObj = scheduleService.putSchedule(user,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_OK);
      assertTrue(scheduleObj!=null);
    }
    {
      //修改一个不存在的调度
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      Date now = new Date();
      ScheduleParam scheduleParam = new ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = JsonUtil.toJsonString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = JsonUtil.toJsonString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = JsonUtil.toJsonString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      ScheduleDto scheduleObj = scheduleService.putSchedule(user,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_OK);
      assertTrue(scheduleObj!=null);
    }
  }

  @Test
  public void testQuerySchedule() throws IOException {
    {
      //查询一个调度信息
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      mockDataService.mockSchedule(project.getName(),projectFlow.getId(),user.getId());
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      ScheduleDto schedule = scheduleService.querySchedule(user,project.getName(),projectFlow.getName());
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_OK);
      assertTrue(schedule!=null);
    }
  }
}
