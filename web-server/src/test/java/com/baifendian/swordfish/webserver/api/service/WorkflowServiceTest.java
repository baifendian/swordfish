package com.baifendian.swordfish.webserver.api.service;

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.dao.enums.NodeType;
import com.baifendian.swordfish.dao.enums.UserRoleType;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.RestfulApiApplication;
import com.baifendian.swordfish.webserver.api.service.mock.MockDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by caojingwei on 2017/3/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RestfulApiApplication.class)
@WebAppConfiguration
@Transactional
public class WorkflowServiceTest {

  private static Logger logger = LoggerFactory.getLogger(ProjectServiceTest.class.getName());

  @Autowired
  private WorkflowService workflowService;

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
  public void testCreateWorkflow() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    {
      //正常创建一个 workflow
      String name = mockDataService.getRandomString();
      String desc = mockDataService.getRandomString();
      String proxyUser = mockDataService.getRandomString();
      String queue = mockDataService.getRandomString();
      String data = mockDataService.mocProjectFlowDataJson(0);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow = workflowService.createWorkflow(user,project.getName(),name,desc,proxyUser,queue,data,null,mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(projectFlow!=null);
      logger.info(objectMapper.writeValueAsString(projectFlow));
    }
    {
      //重复创建一个 workflow
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      String desc = mockDataService.getRandomString();
      String proxyUser = mockDataService.getRandomString();
      String queue = mockDataService.getRandomString();
      String data = mockDataService.mocProjectFlowDataJson(0);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow2 = workflowService.createWorkflow(user,project.getName(),projectFlow.getName(),desc,proxyUser,queue,data,null,mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_CONFLICT);
      assertEquals(projectFlow2,null);
    }
    {
      //无权限创建一个 workflow
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(),user1.getId(), Constants.PROJECT_USER_PERM_READ);
      String name = mockDataService.getRandomString();
      String desc = mockDataService.getRandomString();
      String proxyUser = mockDataService.getRandomString();
      String queue = mockDataService.getRandomString();
      String data = mockDataService.mocProjectFlowDataJson(0);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow = workflowService.createWorkflow(user1,project.getName(),name,desc,proxyUser,queue,data,null,mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_UNAUTHORIZED);
      assertEquals(projectFlow,null);

    }
  }

  @Test
  public void testPatchWorkflow() throws IOException {
    {
      //正常修改一个工作流的描述
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      String desc = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      projectFlow = workflowService.patchWorkflow(user,project.getName(),projectFlow.getName(),desc,null,null,null,null,mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(projectFlow!=null);
    }
    {
      //正常修改一个工作流的data
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project.getId(),user.getId());
      String data = mockDataService.mocProjectFlowDataJson(projectFlow.getId());
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      projectFlow = workflowService.patchWorkflow(user,project.getName(),projectFlow.getName(),null,null,null,data,null,mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(projectFlow!=null);
    }
    {
      //修改一个不存在的工作流
      String name = mockDataService.getRandomString();
      String data = mockDataService.mocProjectFlowDataJson(0);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow = workflowService.patchWorkflow(user,project.getName(),name,null,null,null,data,null,mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_NOT_MODIFIED);
      assertEquals(projectFlow,null);
    }
  }

  @Test
  public void testFlowNodeParamCheck(){
    {
      //检测是否能正常识别MR parameter
      assertTrue(workflowService.flowNodeParamCheck(mockDataService.MR_PARAMETER.toString(), "MR"));
    }
  }
}
