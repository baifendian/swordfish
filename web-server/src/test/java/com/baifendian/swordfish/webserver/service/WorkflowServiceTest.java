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
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.RestfulApiApplication;
import com.baifendian.swordfish.webserver.service.mock.MockDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * workflowService unit test
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

  @Autowired
  private FlowDao flowDao;

  @Test
  public void testCreateWorkflow() throws IOException {
    {
      //正常创建一个 workflow
      String name = mockDataService.getRandomString();
      String desc = mockDataService.getRandomString();
      String proxyUser = mockDataService.getRandomString();
      String queue = mockDataService.getRandomString();
      String data = mockDataService.mocProjectFlowDataJson(0);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow = workflowService.createWorkflow(user,project.getName(),name,desc,proxyUser,queue,data,null);
      ProjectFlow projectFlow1 = flowDao.projectFlowFindByPorjectNameAndName(project.getName(),projectFlow.getName());
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(projectFlow!=null);
      assertTrue(projectFlow.equals(projectFlow1));
      logger.info(JsonUtil.toJsonString(projectFlow));
    }
    {
      //重复创建一个 workflow
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      String desc = mockDataService.getRandomString();
      String proxyUser = mockDataService.getRandomString();
      String queue = mockDataService.getRandomString();
      String data = mockDataService.mocProjectFlowDataJson(0);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow2 = workflowService.createWorkflow(user,project.getName(),projectFlow.getName(),desc,proxyUser,queue,data,null);
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
      ProjectFlow projectFlow = workflowService.createWorkflow(user1,project.getName(),name,desc,proxyUser,queue,data,null);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_UNAUTHORIZED);
      assertEquals(projectFlow,null);

    }
  }

  @Test
  public void testPatchWorkflow() throws IOException {
    {
      //正常修改一个工作流的描述
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      String desc = mockDataService.getRandomString();
      projectFlow.setDesc(desc);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow1 = workflowService.patchWorkflow(user,project.getName(),projectFlow.getName(),desc,null,null,null,null);
      ProjectFlow projectFlow2 = flowDao.projectFlowFindByPorjectNameAndName(project.getName(),projectFlow.getName());
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(projectFlow.equals(projectFlow1));
      assertTrue(projectFlow.equals(projectFlow2));
    }
    {
      //正常修改一个工作流的data
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      ProjectFlow.ProjectFlowData data = mockDataService.mocProjectFlowData(projectFlow.getId());
      projectFlow.setData(data);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow1 = workflowService.patchWorkflow(user,project.getName(),projectFlow.getName(),null,null,null,JsonUtil.toJsonString(data),null);
      ProjectFlow projectFlow2 = flowDao.projectFlowFindByPorjectNameAndName(project.getName(),projectFlow.getName());
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(projectFlow.equals(projectFlow1));
      assertTrue(projectFlow.equals(projectFlow2));
    }
    {
      //修改一个不存在的工作流
      String name = mockDataService.getRandomString();
      String data = mockDataService.mocProjectFlowDataJson(0);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow = workflowService.patchWorkflow(user,project.getName(),name,null,null,null,data,null);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_NOT_MODIFIED);
      assertEquals(projectFlow,null);
    }
  }

  @Test
  public void testPutWorkflow() throws IOException {
    {
      //修改一个不存在工作流
      String name = mockDataService.getRandomString();
      String desc = mockDataService.getRandomString();
      String proxyUser = mockDataService.getRandomString();
      String queue = mockDataService.getRandomString();
      String data = mockDataService.mocProjectFlowDataJson(0);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow = workflowService.putWorkflow(user,project.getName(),name,desc,proxyUser,queue,data,null);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(projectFlow!=null);
    }
    {
      //修改一个存在工作流
      ProjectFlow projectFlow1 = mockDataService.mocProjectFlow(project,user);
      String desc = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow = workflowService.putWorkflow(user,project.getName(),projectFlow1.getName(),desc,null,null,null,null);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(projectFlow!=null);
    }
  }

  @Test
  public void testDeleteProjectFlow() throws JsonProcessingException {
    {
      //正常删除一个工作流
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      workflowService.deleteProjectFlow(user,project.getName(),projectFlow.getName());
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
    }
    {
      //删除一个不存在的工作流
      String name = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      workflowService.deleteProjectFlow(user,project.getName(),name);
      assertEquals(mockHttpServletResponse.getStatus(),HttpStatus.SC_NOT_MODIFIED);
    }
    {
      //无权限删除一个工作流
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(),user1.getId(), Constants.PROJECT_USER_PERM_READ);
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      workflowService.deleteProjectFlow(user1,project.getName(),projectFlow.getName());
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_UNAUTHORIZED);
    }
  }

  @Test
  public void testQueryProjectFlow() throws JsonProcessingException {
    {
      //查询一个工作流
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      ProjectFlow projectFlow1 = workflowService.queryProjectFlow(user,project.getName(),projectFlow.getName());
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(projectFlow1!=null);
    }
  }

  @Test
  public void testQueryAllProjectFlow() throws JsonProcessingException {
    {
      //查询一个项目下的工作流
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      List<ProjectFlow> projectFlowList = workflowService.queryAllProjectFlow(user,project.getName());
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(projectFlowList!=null);
      assertTrue(projectFlowList.size()>0);
    }
  }

  @Test
  public void testmModifyWorkflowConf() throws JsonProcessingException {
    {
      //修改一个项目下的工作流配置
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      String queue = mockDataService.getRandomString();
      String proxyUser = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      workflowService.modifyWorkflowConf(user,project.getName(),queue,proxyUser);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
    }
    {
      //无权限操作一个工作流
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(),user1.getId(), Constants.PROJECT_USER_PERM_READ);
      ProjectFlow projectFlow = mockDataService.mocProjectFlow(project,user);
      String queue = mockDataService.getRandomString();
      String proxyUser = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      workflowService.modifyWorkflowConf(user1,project.getName(),queue,proxyUser);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_UNAUTHORIZED);
    }
  }

  @Test
  public void testFlowNodeParamCheck(){
    {
      //检测是否能正常识别MR parameter
//      assertTrue(workflowService.flowNodeParamCheck(mockDataService.MR_PARAMETER.toString(), "MR"));
    }
  }

}
