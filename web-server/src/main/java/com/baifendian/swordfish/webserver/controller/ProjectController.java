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
package com.baifendian.swordfish.webserver.controller;

import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectUser;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.ProjectDto;
import com.baifendian.swordfish.webserver.dto.ProjectUserDto;
import com.baifendian.swordfish.webserver.service.ProjectService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目管理入口
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {

  private static Logger logger = LoggerFactory.getLogger(ProjectController.class.getName());

  @Autowired
  private ProjectService projectService;

  /**
   * 创建一个项目, 如果存在, 会返回错误
   *
   * @param operator
   * @param name
   * @param desc
   * @return
   */
  @PostMapping(value = "/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  public ProjectDto createProject(@RequestAttribute(value = "session.user") User operator,
                                  @PathVariable("name") String name,
                                  @RequestParam(value = "desc", required = false) String desc) {
    logger.info("Operator user {}, create project, name: {}, desc: {}", operator.getName(), name, desc);

    return new ProjectDto(projectService.createProject(operator, name, desc));
  }

  /**
   * 修改一个项目, 如果不存在, 会返回错误
   *
   * @param operator
   * @param name
   * @param desc
   * @return
   */
  @PatchMapping(value = "/{name}")
  public ProjectDto modifyProject(@RequestAttribute(value = "session.user") User operator,
                                  @PathVariable("name") String name,
                                  @RequestParam(value = "desc", required = false) String desc) {
    logger.info("Operator user {}, modify project, name: {}, desc: {}", operator.getName(), name, desc);

    return new ProjectDto(projectService.modifyProject(operator, name, desc));
  }

  /**
   * 删除项目
   *
   * @param operator
   * @param name
   * @return
   */
  @DeleteMapping(value = "/{name}")
  public void deleteProject(@RequestAttribute(value = "session.user") User operator,
                            @PathVariable("name") String name) {
    logger.info("Operator user {}, delete project, name: {}, desc: {}", operator.getName(), name);

    projectService.deleteProject(operator, name);
  }

  /**
   * 查看所有项目
   *
   * @param operator
   * @return
   */
  @GetMapping(value = "")
  public List<ProjectDto> queryProjects(@RequestAttribute(value = "session.user") User operator) {
    logger.info("Operator user {}, get project list", operator.getName());

    List<Project> projectList = projectService.queryProject(operator);
    List<ProjectDto> projectDtoList = new ArrayList<>();

    for (Project project : projectList) {
      projectDtoList.add(new ProjectDto(project));
    }

    return projectDtoList;
  }

  /**
   * 项目增加一个用户
   *
   * @param operator
   * @param name
   * @param userName
   * @param perm
   * @return
   */
  @PostMapping(value = "/{name}/users/{userName}")
  public ProjectUserDto addProjectUser(@RequestAttribute(value = "session.user") User operator,
                                       @PathVariable("name") String name,
                                       @PathVariable("userName") String userName,
                                       @RequestParam(value = "perm") int perm) {
    logger.info("Operator user {}, add user to project, project name: {}, user name: {}, perm: {}", operator.getName(), name, userName, perm);

    return new ProjectUserDto(projectService.addProjectUser(operator, name, userName, perm));
  }

  /**
   * 修改项目的用户权限信息
   *
   * @param operator
   * @param name
   * @param userName
   * @param perm
   * @return
   */
  @PutMapping(value = "/{name}/users/{userName}")
  public ProjectUserDto modifyProjectUser(@RequestAttribute(value = "session.user") User operator,
                                          @PathVariable("name") String name,
                                          @PathVariable("userName") String userName,
                                          @RequestParam(value = "perm") int perm) {
    logger.info("Operator user {}, modify user permission in the project, project name: {}, user name: {}, perm: {}", operator.getName(), name, userName, perm);

    return new ProjectUserDto(projectService.modifyProjectUser(operator, name, userName, perm));
  }

  /**
   * 项目删除一个用户
   *
   * @param operator
   * @param name
   * @param userName
   */
  @DeleteMapping(value = "/{name}/users/{userName}")
  public void deleteProjectUser(@RequestAttribute(value = "session.user") User operator,
                                @PathVariable("name") String name,
                                @PathVariable("userName") String userName) {
    logger.info("Operator user {}, delete user from project, project name: {}, user name: {}, perm: {}", operator.getName(), name, userName);

    projectService.deleteProjectUser(operator, name, userName);
  }

  /**
   * 查询一个项目下所有的用户
   *
   * @param operator
   * @param name
   * @return
   */
  @GetMapping(value = "/{name}/users")
  public List<ProjectUserDto> queryUser(@RequestAttribute(value = "session.user") User operator,
                                        @PathVariable("name") String name) {
    logger.info("Operator user {}, query users of project, project name: {}", operator.getName(), name);

    List<ProjectUser> projectUserList = projectService.queryUser(operator, name);
    List<ProjectUserDto> projectUserDtoList = new ArrayList<>();

    for (ProjectUser projectUser : projectUserList) {
      projectUserDtoList.add(new ProjectUserDto(projectUser));
    }

    return projectUserDtoList;
  }
}
