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
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.RestfulApiApplication;
import com.baifendian.swordfish.webserver.service.mock.MockDataService;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * 数据源单元测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RestfulApiApplication.class)
@WebAppConfiguration
@Transactional
public class DataSourceServiceTest {

  @Autowired
  private MockDataService mockDataService;

  @Autowired
  private DatasourceService datasourceService;

  //测试用数据源
  private DataSource dataSource;

  //测试用项目
  private Project project;

  //测试用户
  private User user;

  @Before
  public void setUp() throws Exception {
    user = mockDataService.createGeneralUser();
    project = mockDataService.createProject(user);
    dataSource = mockDataService.createDataSource(project.getId(), user.getId());
  }

  @Test
  public void testCreateDataSource() {
    {
      //正常创建一个数据源
      String name = mockDataService.getRandomString();
      String desc = mockDataService.getRandomString();
      String paramter = mockDataService.getRandomString();

      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      DataSource res = datasourceService.createDataSource(user, project.getName(), name, desc, DbType.MYSQL, paramter, mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(res != null);
    }
    {
      //无权限创建一个数据源
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(), user1.getId(), Constants.PROJECT_USER_PERM_READ);
      String name = mockDataService.getRandomString();
      String desc = mockDataService.getRandomString();
      String paramter = mockDataService.getRandomString();

      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      DataSource res = datasourceService.createDataSource(user1, project.getName(), name, desc, DbType.MYSQL, paramter, mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_UNAUTHORIZED);
      assertTrue(res == null);
    }
    {
      //重复创建一个数据源
      String name = dataSource.getName();
      String desc = mockDataService.getRandomString();
      String paramter = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      DataSource res = datasourceService.createDataSource(user, project.getName(), name, desc, DbType.MYSQL, paramter, mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_CONFLICT);
      assertTrue(res == null);
    }
  }

  @Test
  public void testModifyDataSource() {
    {
      //正常修改一个数据源
      String desc = mockDataService.getRandomString();
      String paramter = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      DataSource res = datasourceService.modifyDataSource(user, project.getName(), dataSource.getName(), desc, paramter, mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(res != null);
    }
    {
      //无权限修改一个数据源
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(), user1.getId(), Constants.PROJECT_USER_PERM_READ);
      String desc = mockDataService.getRandomString();
      String paramter = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      DataSource res = datasourceService.modifyDataSource(user1, project.getName(), dataSource.getName(), desc, paramter, mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_UNAUTHORIZED);
      assertTrue(res == null);
    }
    {
      //修改一个不存在的数据源
      String name = mockDataService.getRandomString();
      String desc = mockDataService.getRandomString();
      String paramter = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      DataSource res = datasourceService.modifyDataSource(user, project.getName(), name, desc, paramter, mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_NOT_MODIFIED);
      assertTrue(res == null);
    }
  }

  @Test
  public void testDeleteDataSource() {
    {
      //正常删除一个数据源
      DataSource dataSource1 = mockDataService.createDataSource(project.getId(), user.getId());
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      datasourceService.deleteDataSource(user, project.getName(), dataSource1.getName(), mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
    }
    {
      //无权限用户删除一个数据源
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(), user1.getId(), Constants.PROJECT_USER_PERM_READ);
      DataSource dataSource1 = mockDataService.createDataSource(project.getId(), user.getId());
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      datasourceService.deleteDataSource(user1, project.getName(), dataSource1.getName(), mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_UNAUTHORIZED);
    }
    {
      //删除一个不存在的数据源
      String name = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      datasourceService.deleteDataSource(user, project.getName(), name, mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_NOT_MODIFIED);
    }
  }

  @Test
  public void testQuery() {
    {
      //正常查看所有数据源
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      List<DataSource> dataSourceList = datasourceService.query(user, project.getName(), mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(dataSourceList != null);
      assertTrue(dataSourceList.size() > 0);
    }
    {
      //无权限查看数据源
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(), user1.getId(), Constants.PROJECT_USER_PERM_WRITE);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      List<DataSource> dataSourceList = datasourceService.query(user1, project.getName(), mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_UNAUTHORIZED);
      assertTrue(dataSourceList == null);
    }
  }

  @Test
  public void testQueryByName() {
    {
      //正常查看指定数据源
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      DataSource res = datasourceService.queryByName(user, project.getName(), dataSource.getName(), mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(res != null);
    }
    {
      //无权限查看指定数据源
      User user1 = mockDataService.createGeneralUser();
      mockDataService.createProjectUser(project.getId(), user1.getId(), Constants.PROJECT_USER_PERM_WRITE);
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      DataSource res = datasourceService.queryByName(user1, project.getName(), dataSource.getName(), mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_UNAUTHORIZED);
      assertTrue(res == null);
    }
    {
      //查看一个不存在的数据源
      String name = mockDataService.getRandomString();
      MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
      DataSource res = datasourceService.queryByName(user, project.getName(), name, mockHttpServletResponse);
      assertEquals(mockHttpServletResponse.getStatus(), HttpStatus.SC_OK);
      assertTrue(res == null);
    }
  }
}
