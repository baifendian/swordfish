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

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.utils.PermUtil;
import com.baifendian.swordfish.dao.enums.UserRoleType;
import com.baifendian.swordfish.dao.mapper.*;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectUser;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.exception.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.List;

import static com.baifendian.swordfish.webserver.utils.ParamVerify.verifyDesc;
import static com.baifendian.swordfish.webserver.utils.ParamVerify.verifyProjectName;

@Service
public class ProjectService {

  private static Logger logger = LoggerFactory.getLogger(ProjectService.class.getName());

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private UserService userService;

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectUserMapper projectUserMapper;

  @Autowired
  private ProjectFlowMapper projectFlowMapper;

  @Autowired
  private ResourceMapper resourceMapper;

  @Autowired
  private DataSourceMapper dataSourceMapper;

  /**
   * 创建一个项目
   *
   * @param operator
   * @param name
   * @param desc
   * @return
   */
  public Project createProject(User operator, String name, String desc) {

    verifyProjectName(name);
    verifyDesc(desc);

    //只有普通用户才可以创建项目
    if (!userService.isGeneral(operator)) {
      throw new PermissionException("User \"{0}\" is not general user", operator.getName());
    }

    Project project = new Project();
    Date now = new Date();

    project.setName(name);
    project.setDesc(desc);
    project.setOwnerId(operator.getId());
    project.setOwner(operator.getName());
    project.setCreateTime(now);
    project.setModifyTime(now);

    try {
      projectMapper.insert(project);
    } catch (DuplicateKeyException e) {
      logger.error("Project has exist, can't create again.", e);
      throw new ServerErrorException("Project has exist, can't create again.");
    }

    return project;
  }

  /**
   * 修改项目
   *
   * @param operator
   * @param name
   * @param desc
   * @return
   */
  public Project modifyProject(User operator, String name, String desc) {

    verifyDesc(desc);

    Project project = projectMapper.queryByName(name);
    if (project == null) {
      logger.error("Project does not exist: {}", name);
      throw new NotFoundException("Not found project \"{0}\"", name);
    }

    // 只有项目 owner 才能操作
    if (!isProjectOwner(operator.getId(), project)) {
      logger.error("User {} owner for the project {}", operator.getName(), name);
      throw new PermissionException("User \"{0}\" not owner for the project \"{1}\"", operator.getName(), project.getName());
    }

    Date now = new Date();

    if (desc != null) {
      project.setDesc(desc);
    }

    project.setModifyTime(now);

    int count = projectMapper.updateById(project);

    if (count <= 0) {
      throw new ServerErrorException("Not update count");
    }

    return project;
  }

  /**
   * 删除一个项目
   *
   * @param operator
   * @param name
   */
  @Transactional(value = "TransactionManager")
  public void deleteProject(User operator, String name) {

    Project project = projectMapper.queryByName(name);
    if (project == null) {
      logger.error("Project does not exist: {}", name);
      throw new NotFoundException("Not found project \"{0}\"", name);
    }

    // 只有管理员或者 owner 可以删除
    if (operator.getId() != project.getOwnerId() && operator.getRole() != UserRoleType.ADMIN_USER) {
      logger.error("User \"{}\" owner for the project \"{}\"", operator.getName(), name);
      throw new PermissionException("User \"{0}\" not owner for the project \"{1}\"", operator.getName(), name);
    }

    // 需要判断项目下, 是否有 "工作流/资源/数据源" 存在
    int count;

    count = projectFlowMapper.countProjectFlows(project.getId());
    if (count > 0) {
      throw new PreFailedException("Project's workflow is not empty");
    }

    count = resourceMapper.countProjectRes(project.getId());
    if (count > 0) {
      throw new PreFailedException("Project's resource is not empty");
    }

    count = dataSourceMapper.countProjectDatasource(project.getId());
    if (count > 0) {
      throw new PreFailedException("Project's data source is not empty");
    }

    count = projectMapper.deleteById(project.getId());

    if (count <= 0) {
      throw new ServerErrorException("Not delete count");
    }

    // 应该清理 Local/HDFS 上的相关目录
    HdfsClient.getInstance().delete(BaseConfig.getHdfsProjectDir(project.getId()), true);
    FileUtils.deleteQuietly(new File(BaseConfig.getLocalProjectDir(project.getId())));

    return;
  }

  /**
   * 查询项目列表
   *
   * @param operator
   * @return
   */
  public List<Project> queryProject(User operator) {
    switch (operator.getRole()) {
      case ADMIN_USER:
        return projectMapper.queryAllProject();
      case GENERAL_USER:
        return projectMapper.queryProjectByUser(operator.getId());
      default:
        return null;
    }
  }

  /**
   * 把一个用户添加到项目中
   *
   * @param operator
   * @param name
   * @param userName
   * @param perm
   * @return
   */
  public ProjectUser addProjectUser(User operator, String name, String userName, int perm) {

    // 不能添加自己
    if (operator.getName().equals(name)) {
      throw new BadRequestException("Can't add myself");
    }

    Project project = projectMapper.queryByName(name);
    if (project == null) {
      logger.error("Project does not exist: {}", name);
      throw new NotFoundException("Not found project \"{0}\"", name);
    }

    // 只有 owner 可以操作
    if (!isProjectOwner(operator.getId(), project)) {
      logger.error("User \"{}\" owner for the project \"{}\"", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" not owner for the project \"{1}\"", operator.getName(), project.getName());
    }

    User user = userMapper.queryByName(userName);
    if (user == null) {
      logger.error("User {} not found", userName);
      throw new NotFoundException("User \"{0}\" not found", userName);
    }

    ProjectUser projectUser = projectUserMapper.query(project.getId(), user.getId());

    // 增加用户已经存在
    if (projectUser != null) {
      throw new ServerErrorException("User has exist, can't add again.");
    }

    // 构建信息, 插入
    projectUser = new ProjectUser();
    Date now = new Date();

    projectUser.setProjectId(project.getId());
    projectUser.setProjectName(project.getName());
    projectUser.setUserId(user.getId());
    projectUser.setUserName(userName);
    projectUser.setPerm(perm);
    projectUser.setCreateTime(now);
    projectUser.setModifyTime(now);

    projectUserMapper.insert(projectUser);

    return projectUser;
  }

  /**
   * 修改项目中用户的权限
   *
   * @param operator
   * @param name
   * @param userName
   * @param perm
   */
  public ProjectUser modifyProjectUser(User operator, String name, String userName, int perm) {
    Project project = projectMapper.queryByName(name);
    if (project == null) {
      logger.error("Project does not exist: {}", name);
      throw new NotFoundException("Not found project \"{0}\"", name);
    }

    // 必须是项目owner
    if (!isProjectOwner(operator.getId(), project)) {
      logger.error("User \"{}\" owner for the project \"{}\"", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" not owner for the project \"{1}\"", operator.getName(), project.getName());
    }

    // 查询用户信息，如果查询不到抛出异常
    User user = userMapper.queryByName(userName);
    if (user == null) {
      logger.error("User {} not found", userName);
      throw new NotFoundException("User \"{0}\" not found", userName);
    }

    ProjectUser projectUser = projectUserMapper.query(project.getId(), user.getId());
    if (projectUser == null) {
      throw new NotFoundException("Project \"{0}\" don't has user \"{1}\"", user.getName(), project.getName());
    }

    // 构建信息, 插入
    Date now = new Date();

    projectUser.setProjectName(project.getName());
    projectUser.setUserName(userName);
    projectUser.setPerm(perm);
    projectUser.setModifyTime(now);

    projectUserMapper.modify(projectUser);

    return projectUser;
  }

  /**
   * 删除一个项目中的用户
   *
   * @param operator
   * @param name
   * @param userName
   */
  public void deleteProjectUser(User operator, String name, String userName) {

    // 不能删除自己
    if (operator.getName().equals(name)) {
      throw new BadRequestException("Can't delete myself");
    }

    Project project = projectMapper.queryByName(name);
    if (project == null) {
      logger.error("Project does not exist: {}", name);
      throw new NotFoundException("Not found project \"{0}\"", name);
    }

    // 必须是项目owner
    if (!isProjectOwner(operator.getId(), project)) {
      logger.error("User \"{}\" owner for the project \"{}\"", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" not owner for the project \"{1}\"", operator.getName(), project.getName());
    }

    User user = userMapper.queryByName(userName);
    if (user == null) {
      logger.error("User {} not found", userName);
      throw new NotFoundException("User \"{0}\" not found", userName);
    }

    int count = projectUserMapper.delete(project.getId(), user.getId());

    if (count <= 0) {
      throw new ServerErrorException("Not delete count");
    }

    return;
  }

  /**
   * 查询一个项目下所有用户
   *
   * @param operator
   * @param name
   * @return
   */
  public List<ProjectUser> queryUser(User operator, String name) {
    Project project = projectMapper.queryByName(name);

    if (project == null) {
      logger.error("Project does not exist: {}", name);
      throw new NotFoundException("Not found project \"{0}\"", name);
    }

    // 只有 owner 可以操作
    if (!isProjectOwner(operator.getId(), project)) {
      logger.error("User \"{}\" owner for the project \"{}\"", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" not owner for the project \"{1}\"", operator.getName(), project.getName());
    }

    return projectUserMapper.queryByProject(project.getId());
  }

  /**
   * 查询一个用户在项目中具备的权限
   *
   * @param userId
   * @param project
   * @return
   */
  public int queryPerm(int userId, Project project) {
    if (project.getOwnerId() == userId) {
      return Constants.PROJECT_USER_PERM_ALL;
    }

    ProjectUser projectUser = projectUserMapper.query(project.getId(), userId);

    if (projectUser == null) {
      return 0;
    }

    return projectUser.getPerm();
  }

  /**
   * 判断一个用户师傅是项目owner
   *
   * @param userId
   * @param project
   * @return
   */
  public boolean isProjectOwner(int userId, Project project) {
    return userId == project.getOwnerId();
  }

  /**
   * 判断一个用户是否有project 写权限
   *
   * @param userId
   * @param project
   * @return
   */
  public boolean hasWritePerm(int userId, Project project) {
    return PermUtil.hasWritePerm(queryPerm(userId, project));
  }

  /**
   * 判断一个用户是否有project 读权限
   *
   * @param userId
   * @param project
   * @return
   */
  public boolean hasReadPerm(int userId, Project project) {
    return PermUtil.hasReadPerm(queryPerm(userId, project));
  }

  /**
   * 是否有项目执行权限
   *
   * @param userId
   * @param project
   * @return
   */
  public boolean hasExecPerm(int userId, Project project) {
    return PermUtil.hasExecPerm(queryPerm(userId, project));
  }


}
