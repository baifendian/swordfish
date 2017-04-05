package com.baifendian.swordfish.webserver.api.service;

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.enums.ScheduleStatus;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.RestfulApiApplication;
import com.baifendian.swordfish.webserver.api.service.mock.MockDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Created by caojingwei on 2017/4/5.
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

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testCreateSchedule() throws IOException {
    {
      //正常创建一个调度
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      Date now = new Date();
      Schedule.ScheduleParam scheduleParam = new Schedule.ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = objectMapper.writeValueAsString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = objectMapper.writeValueAsString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = objectMapper.writeValueAsString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      Schedule scheduleObj = scheduleService.createSchedule(user,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,mockHttpServletRespon);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_OK);
      assertTrue(scheduleObj!=null);
      logger.info(objectMapper.writeValueAsString(scheduleObj));
    }
    {
      //创建一个不存在的projectFlow的调度
      Date now = new Date();
      Schedule.ScheduleParam scheduleParam = new Schedule.ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = objectMapper.writeValueAsString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = objectMapper.writeValueAsString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = objectMapper.writeValueAsString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      Schedule scheduleObj = scheduleService.createSchedule(user,project.getName(),mockDataService.getRandomString(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,mockHttpServletRespon);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_NOT_MODIFIED);
      assertEquals(scheduleObj,null);
    }
    {
      //创建一个已经存在的调度
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      mockDataService.mockSchedule(project.getName(),projectFlow.getId(),user.getId());
      Date now = new Date();
      Schedule.ScheduleParam scheduleParam = new Schedule.ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = objectMapper.writeValueAsString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = objectMapper.writeValueAsString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = objectMapper.writeValueAsString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      Schedule scheduleObj = scheduleService.createSchedule(user,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,mockHttpServletRespon);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_CONFLICT);
      assertEquals(scheduleObj,null);
    }
    {
      //无权限创建一个调度
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(),user1.getId(), Constants.PROJECT_USER_PERM_READ);
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      Date now = new Date();
      Schedule.ScheduleParam scheduleParam = new Schedule.ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = objectMapper.writeValueAsString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = objectMapper.writeValueAsString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = objectMapper.writeValueAsString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      Schedule scheduleObj = scheduleService.createSchedule(user1,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,mockHttpServletRespon);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_UNAUTHORIZED);
      assertEquals(scheduleObj,null);
    }
  }

  @Test
  public void testPatchSchedule() throws IOException {
    {
      //正常修改一个调度
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      mockDataService.mockSchedule(project.getName(),projectFlow.getId(),user.getId());
      Date now = new Date();
      Schedule.ScheduleParam scheduleParam = new Schedule.ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = objectMapper.writeValueAsString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = objectMapper.writeValueAsString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = objectMapper.writeValueAsString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      ScheduleStatus scheduleStatus = ScheduleStatus.ONLINE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      Schedule scheduleObj = scheduleService.patchSchedule(user,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,scheduleStatus,mockHttpServletRespon);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_OK);
      assertTrue(scheduleObj!=null);
    }
    {
      //修改一个不存在的调度
      Date now = new Date();
      Schedule.ScheduleParam scheduleParam = new Schedule.ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = objectMapper.writeValueAsString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = objectMapper.writeValueAsString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = objectMapper.writeValueAsString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      ScheduleStatus scheduleStatus = ScheduleStatus.ONLINE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      Schedule scheduleObj = scheduleService.patchSchedule(user,project.getName(),mockDataService.getRandomString(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,scheduleStatus,mockHttpServletRespon);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_NOT_MODIFIED);
      assertEquals(scheduleObj,null);
    }
    {
      //无权限修改
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      mockDataService.mockSchedule(project.getName(),projectFlow.getId(),user.getId());
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(),user1.getId(), Constants.PROJECT_USER_PERM_READ);
      Date now = new Date();
      Schedule.ScheduleParam scheduleParam = new Schedule.ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = objectMapper.writeValueAsString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = objectMapper.writeValueAsString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = objectMapper.writeValueAsString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      ScheduleStatus scheduleStatus = ScheduleStatus.ONLINE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      Schedule scheduleObj = scheduleService.patchSchedule(user1,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,scheduleStatus,mockHttpServletRespon);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_UNAUTHORIZED);
      assertEquals(scheduleObj,null);
    }
  }

  @Test
  public void testPutSchedule() throws IOException {
    {
      //修改一个已经存在的调度
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      mockDataService.mockSchedule(project.getName(),projectFlow.getId(),user.getId());
      Date now = new Date();
      Schedule.ScheduleParam scheduleParam = new Schedule.ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = objectMapper.writeValueAsString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = objectMapper.writeValueAsString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = objectMapper.writeValueAsString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      Schedule scheduleObj = scheduleService.putSchedule(user,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,mockHttpServletRespon);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_OK);
      assertTrue(scheduleObj!=null);
    }
    {
      //修改一个不存在的调度
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      Date now = new Date();
      Schedule.ScheduleParam scheduleParam = new Schedule.ScheduleParam();
      scheduleParam.setStartDate(now);
      scheduleParam.setEndDate(now);
      scheduleParam.setCrontab("0 8 * * * * ?");
      String schedule = objectMapper.writeValueAsString(scheduleParam);
      NotifyType notifyType = NotifyType.FAILURE;
      String notifyMails = objectMapper.writeValueAsString(Arrays.asList(new String[]{"ABC@baifendian.com"}));
      Integer maxTryTimes = 2;
      FailurePolicyType failurePolicyType = FailurePolicyType.END;
      String depWorkflows = objectMapper.writeValueAsString(Arrays.asList(new Schedule.DepWorkflow[]{new Schedule.DepWorkflow(project.getName(),mockDataService.getRandomString())}));
      DepPolicyType depPolicyType = DepPolicyType.NO_DEP_PRE;
      Integer timeout = 3600;
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      Schedule scheduleObj = scheduleService.putSchedule(user,project.getName(),projectFlow.getName(),schedule,notifyType,notifyMails,maxTryTimes,failurePolicyType,depWorkflows,depPolicyType,timeout,mockHttpServletRespon);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_OK);
      assertTrue(scheduleObj!=null);
    }
  }

  @Test
  public void testQuerySchedule() throws IOException {
    {
      //查询一个调度信息
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      mockDataService.mockSchedule(project.getName(),projectFlow.getId(),user.getId());
      MockHttpServletResponse mockHttpServletRespon = new MockHttpServletResponse();
      Schedule schedule = scheduleService.querySchedule(user,project.getName(),projectFlow.getName(),mockHttpServletRespon);
      assertEquals(mockHttpServletRespon.getStatus(), HttpStatus.SC_OK);
      assertTrue(schedule!=null);
    }
  }
}
