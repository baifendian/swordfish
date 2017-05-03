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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

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
   * @param name
   * @param desc
   * @param response
   * @return
   */
  @PostMapping(value = "/{name}")
  public ProjectDto createProject(@RequestAttribute(value = "session.user") User operator,
                                  @PathVariable("name") String name,
                                  @RequestParam(value = "desc", required = false) String desc,
                                  HttpServletResponse response) {
    logger.info("Operator user {}, create project, name: {}, desc: {}", operator.getName(), name, desc);

    return new ProjectDto(projectService.createProject(operator, name, desc));
  }

  /**
   * 修改一个项目, 如果不存在, 会返回错误
   *
   * @param name
   * @param desc
   * @param response
   * @return
   */
  @PatchMapping(value = "/{name}")
  public ProjectDto modifyProject(@RequestAttribute(value = "session.user") User operator,
                                  @PathVariable("name") String name,
                                  @RequestParam(value = "desc", required = false) String desc,
                                  HttpServletResponse response) {
    logger.info("Operator user {}, modify project, name: {}, desc: {}", operator.getName(), name, desc);

    return new ProjectDto(projectService.modifyProject(operator, name, desc));
  }

  /**
   * 删除项目
   *
   * @param name
   * @param response
   * @return
   */
  @DeleteMapping(value = "/{name}")
  public void deleteProject(@RequestAttribute(value = "session.user") User operator,
                            @PathVariable("name") String name,
                            HttpServletResponse response) {
    logger.info("Operator user {}, delete project, name: {}, desc: {}", operator.getName(), name);

    projectService.deleteProject(operator, name);
  }

  /**
   * 查看所有项目
   *
   * @param response
   * @return
   */
  @GetMapping(value = "")
  public List<ProjectDto> queryProjects(@RequestAttribute(value = "session.user") User operator,
                                        HttpServletResponse response) {
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
   * @param response
   * @return
   */
  @PostMapping(value = "/{name}/users/{userName}")
  public ProjectUserDto addProjectUser(@RequestAttribute(value = "session.user") User operator,
                                       @PathVariable("name") String name,
                                       @PathVariable("userName") String userName,
                                       @RequestParam(value = "perm") int perm,
                                       HttpServletResponse response) {
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
   * @param response
   * @return
   */
  @PutMapping(value = "/{name}/users/{userName}")
  public ProjectUserDto modifyProjectUser(@RequestAttribute(value = "session.user") User operator,
                                          @PathVariable("name") String name,
                                          @PathVariable("userName") String userName,
                                          @RequestParam(value = "perm") int perm,
                                          HttpServletResponse response) {
    logger.info("Operator user {}, modify user permission in the project, project name: {}, user name: {}, perm: {}", operator.getName(), name, userName, perm);

    return new ProjectUserDto(projectService.modifyProjectUser(operator, name, userName, perm));
  }

  /**
   * 项目删除一个用户
   *
   * @param operator
   * @param name
   * @param userName
   * @param response
   */
  @DeleteMapping(value = "/{name}/users/{userName}")
  public void deleteProjectUser(@RequestAttribute(value = "session.user") User operator,
                                @PathVariable("name") String name,
                                @PathVariable("userName") String userName,
                                HttpServletResponse response) {
    logger.info("Operator user {}, delete user from project, project name: {}, user name: {}, perm: {}", operator.getName(), name, userName);

    projectService.deleteProjectUser(operator, name, userName);
  }

  /**
   * 查询一个项目下所有的用户
   *
   * @param operator
   * @param name
   * @param response
   * @return
   */
  @GetMapping(value = "/{name}/users")
  public List<ProjectUserDto> queryUser(@RequestAttribute(value = "session.user") User operator,
                                        @PathVariable("name") String name,
                                        HttpServletResponse response) {
    logger.info("Operator user {}, query users of project, project name: {}", operator.getName(), name);

    List<ProjectUser> projectUserList = projectService.queryUser(operator, name);
    List<ProjectUserDto> projectUserDtoList = new ArrayList<>();
    for (ProjectUser projectUser : projectUserList) {
      projectUserDtoList.add(new ProjectUserDto(projectUser));
    }
    return projectUserDtoList;
  }
}
