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
package com.baifendian.swordfish.webserver.api.service.mock;

import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.enums.UserRoleType;
import com.baifendian.swordfish.dao.mapper.DataSourceMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.ProjectUserMapper;
import com.baifendian.swordfish.dao.mapper.UserMapper;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectUser;
import com.baifendian.swordfish.dao.model.User;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 单元测试模拟数据工具
 */
@Service
public class MockDataService {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectUserMapper projectUserMapper;

  @Autowired
  private DataSourceMapper dataSourceMapper;

  /**
   * 获取一个随机字符串
   * @return
   */
  public String getRandomString(){
    return RandomStringUtils.random(5, new char[]{'a', 'b', 'c', 'd', 'e', 'f'});
  }

  /**
   * 创建一个的用户
   * @return
   */
  public User createUser(UserRoleType userRoleType){
    User user = new User();
    Date now = new Date();

    user.setName(getRandomString());
    user.setPassword(getRandomString());
    user.setDesc(getRandomString());
    user.setEmail(getRandomString());
    user.setPhone(getRandomString());
    user.setRole(userRoleType);
    user.setProxyUsers("*");
    user.setCreateTime(now);
    user.setModifyTime(now);

    userMapper.insert(user);
    return user;
  }

  /**
   * 创建一个普通用户
   * @return
   */
  public User createGeneralUser(){
    return createUser(UserRoleType.GENERAL_USER);
  }

  /**
   * 创建一个管理员用户
   * @return
   */
  public User createAdminUser(){
    return createUser(UserRoleType.ADMIN_USER);
  }

  /**
   * 创建一个项目
   * @param user
   * @return
   */
  public Project createProject(User user){
    Project project = new Project();
    Date now = new Date();

    project.setName(getRandomString());
    project.setDesc(getRandomString());
    project.setCreateTime(now);
    project.setModifyTime(now);
    project.setOwnerId(user.getId());

    projectMapper.insert(project);
    return project;
  }

  /**
   * 创建一个用户项目关系
   * @param projectId
   * @param userId
   * @param perm
   * @return
   */
  public ProjectUser createProjectUser(int projectId, int userId, int perm){
    ProjectUser projectUser = new ProjectUser();
    Date now = new Date();

    projectUser.setProjectId(projectId);
    projectUser.setUserId(userId);
    projectUser.setPerm(perm);
    projectUser.setCreateTime(now);
    projectUser.setModifyTime(now);

    projectUserMapper.insert(projectUser);

    return projectUser;
  }

  /**
   * 创建一个数据源
   * @param projectId
   * @param userId
   * @return
   */
  public DataSource createDataSource(int projectId,int userId){
    DataSource dataSource = new DataSource();
    Date now = new Date();

    dataSource.setName(getRandomString());
    dataSource.setDesc(getRandomString());
    dataSource.setType(DbType.MYSQL);
    dataSource.setOwnerId(userId);
    dataSource.setProjectId(projectId);
    dataSource.setParams(getRandomString());
    dataSource.setCreateTime(now);
    dataSource.setModifyTime(now);

    dataSourceMapper.insert(dataSource);

    return dataSource;
  }
}
