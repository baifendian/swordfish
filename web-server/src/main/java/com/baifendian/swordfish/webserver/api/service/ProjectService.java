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

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.ProjectUserMapper;
import com.baifendian.swordfish.dao.mapper.UserMapper;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectUser;
import com.baifendian.swordfish.dao.model.User;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;


@Service
public class ProjectService {

  private static Logger logger = LoggerFactory.getLogger(ProjectService.class.getName());

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectUserMapper projectUserMapper;

  /**
   * 创建一个项目
   *
   * @param operator
   * @param name
   * @param desc
   * @param response
   * @return
   */
  public Project createProject(User operator, String name, String desc, HttpServletResponse response) {

    Project project = new Project();
    Date now = new Date();

    project.setName(name);
    project.setDesc(desc);
    project.setOwnerId(operator.getId());
    project.setCreateTime(now);
    project.setModifyTime(now);

    try {
      projectMapper.insert(project);
    } catch (DuplicateKeyException e) {
      logger.error("Project has exist, can't create again.", e);
      response.setStatus(HttpStatus.SC_CONFLICT);
      return null;
    }
    projectMapper.insert(project);
    return project;
  }

  /**
   * 修改项目
   *
   * @param operator
   * @param name
   * @param desc
   * @param response
   * @return
   */
  public Project modifyProject(User operator, String name, String desc, HttpServletResponse response) {
    Project project = projectMapper.queryByName(name);
    Date now = new Date();

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (ObjectUtils.notEqual(operator.getId(), project.getOwnerId())) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    project.setDesc(desc);
    project.setModifyTime(now);

    int count = projectMapper.updateById(project);

    if (count <= 0) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    return project;
  }

  /**
   * 删除一个项目
   *
   * @param operator
   * @param name
   * @param response
   */
  public void deleteProject(User operator, String name, HttpServletResponse response) {
    Project project = projectMapper.queryByName(name);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    if (ObjectUtils.notEqual(operator.getId(), project.getOwnerId())) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return;
    }

    int count = projectMapper.deleteById(project.getId());

    if (count <= 0) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }
    return;
  }

  /**
   * 查询项目列表
   * @param operator
   * @param response
   * @return
   */
  public List<Project> queryProject(User operator, HttpServletResponse response) {
    switch (operator.getRole()){
      case ADMIN_USER: return projectMapper.queryAllProject();
      case GENERAL_USER: return projectMapper.queryUserProject(operator.getId());
      default: return null;
    }
  }

  /**
   * 把一个用户添加到项目中
   * @param operator
   * @param name
   * @param userName
   * @param perm
   * @param response
   * @return
   */
  public ProjectUser addProjectUser(User operator,String name,String userName ,int perm , HttpServletResponse response){
    Project project = projectMapper.queryByName(name);

    //不存在的项目名
    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    //操作用户不是项目的owner
    if (ObjectUtils.notEqual(operator.getId(), project.getOwnerId())) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    User user = userMapper.queryByName(userName);

    //增加的用户不存在
    if (user == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    ProjectUser projectUser = projectUserMapper.query(user.getId(),project.getId());

    //增加用户已经存在
    if (projectUser != null){
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    projectUser = new ProjectUser();
    Date now = new Date();

    projectUser.setProjectId(project.getId());
    projectUser.setUserId(user.getId());
    projectUser.setPerm(perm);
    projectUser.setCreateTime(now);

    projectUserMapper.insert(projectUser);

    return projectUser;
  }

  /**
   * 删除一个项目中的用户
   * @param operator
   * @param name
   * @param userName
   * @param response
   */
  public void deleteProjectUser(User operator,String name,String userName,HttpServletResponse response){
    Project project = projectMapper.queryByName(name);

    //不存在的项目名
    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    //操作用户不是项目的owner
    if (ObjectUtils.notEqual(operator.getId(), project.getOwnerId())) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return;
    }

    User user = userMapper.queryByName(userName);

    //增加的用户不存在
    if (user == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    int count = projectUserMapper.delete(project.getId(),user.getId());

    if (count <= 0) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }
    return;
  }

  /**
   * 查询一个项目下所有用户
   * @param operator
   * @param name
   * @param response
   * @return
   */
  public List<User> queryUser(User operator,String name,HttpServletResponse response){
    Project project = projectMapper.queryByName(name);

    //不存在的项目名
    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    //操作用户不是项目的owner
    if (ObjectUtils.notEqual(operator.getId(), project.getOwnerId())) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    return projectUserMapper.queryForUser(project.getId());
  }

  /**
   * 查询一个用户在项目中是否有指定权限
   * @param userId
   * @param projectId
   * @param perm
   * @return
   */
  public boolean queryPerm(int userId,int projectId,int perm){

    ProjectUser projectUser = projectUserMapper.query(userId,projectId);
    if (projectUser == null){
      return false;
    }

    return  (projectUser.getPerm() & perm) == perm;
  }

  /**
   * 查询一个用户在指定项目中是否有写权限
   * @param userId
   * @param projectId
   * @return
   */
  public boolean queryWritePerm(int userId,int projectId){
    return queryPerm(userId,projectId, Constants.PROJECT_USER_PERM_WRITE);
  }

  /**
   * 查询一个用户在指定项目中是否有读权限
   * @param userId
   * @param projectId
   * @return
   */
  public boolean queryReadPerm(int userId,int projectId){
    return queryPerm(userId,projectId, Constants.PROJECT_USER_PERM_READ);
  }

  /**
   * 查询一个用户在指定项目中是否有执行权限
   * @param userId
   * @param projectId
   * @return
   */
  public boolean queryExecPerm(int userId,int projectId){
    return queryPerm(userId,projectId, Constants.PROJECT_USER_PERM_EXEC);
  }

}
