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
package com.baifendian.swordfish.webserver.api.service;

import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.RestfulApiApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * projectService 单元测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Transactional
public class ProjectServiceTest {

  @Autowired
  private MockDataService mockDataService;

  @Autowired
  private ProjectService projectService;

  //模拟普通用户
  private User user;
  //模拟管理员用户
  private User userAdmin;
  //模拟项目1
  private Project project;

  @Before
  public void setUp() throws Exception {
    user = mockDataService.createGeneralUser();
    userAdmin = mockDataService.createAdminUser();
    project = mockDataService.createProject(user);
  }

  /**
   * 创建项目测试
   */
  @Test
  public void testCreateProject(){
    {
      //正常创建一个项目
      String name = mockDataService.getRandomString();
      String desc = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      Project project = projectService.createProject(user,name,desc,mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(),200);
      assertTrue(project!=null);
    }
  }
}
