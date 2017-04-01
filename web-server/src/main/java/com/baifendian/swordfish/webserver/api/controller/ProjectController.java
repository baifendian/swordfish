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
package com.baifendian.swordfish.webserver.api.controller;

import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectUser;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
  public Project createProject(@RequestAttribute(value = "session.user") User operator,
                               @PathVariable("name") String name,
                               @RequestParam(value = "desc", required = false) String desc,
                               HttpServletResponse response) {
    logger.info("Operator user id {}, create project, name: {}, desc: {}", operator.getId(), name, desc);
    return projectService.createProject(operator, name, desc, response);
  }

  /**
   * 修改一个项目, 如果不存在, 会返回错误
   *
   * @param name
   * @param desc
   * @param response
   * @return
   */
  @PutMapping(value = "/{name}")
  public Project modifyProject(@RequestAttribute(value = "session.user") User operator,
                               @PathVariable("name") String name,
                               @RequestParam(value = "desc", required = false) String desc,
                               HttpServletResponse response) {
    logger.info("Operator user id {}, modify project, name: {}, desc: {}", operator.getId(), name, desc);
    return projectService.modifyProject(operator, name, desc, response);
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
    logger.info("Operator user id {}, delete project, name: {}, desc: {}", operator.getId(), name);
    projectService.deleteProject(operator, name, response);
  }

  /**
   * 查看所有项目
   *
   * @param response
   * @return
   */
  @GetMapping(value = "")
  public List<Project> queryProjects(@RequestAttribute(value = "session.user") User operator,
                                     HttpServletResponse response) {
    logger.info("Operator user id {}, get project list", operator.getId());
    return projectService.queryProject(operator, response);
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
  @RequestMapping(value = "/{name}/users/{userName}", method = {RequestMethod.PUT})
  public ProjectUser addProjectUser(@RequestAttribute(value = "session.user") User operator,
                                    @PathVariable("name") String name,
                                    @PathVariable("userName") String userName,
                                    @RequestParam(value = "perm") int perm,
                                    HttpServletResponse response) {
    logger.info("Operator user id {}, add user to project, project name: {}, user name: {}, perm: {}", operator.getId(), name, userName, perm);
    return projectService.addProjectUser(operator, name, userName, perm, response);
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
  public ProjectUser modifyProjectUser(@RequestAttribute(value = "session.user") User operator,
                                    @PathVariable("name") String name,
                                    @PathVariable("userName") String userName,
                                    @RequestParam(value = "perm") int perm,
                                    HttpServletResponse response) {
    logger.info("Operator user id {}, modify user permission in the project, project name: {}, user name: {}, perm: {}", operator.getId(), name, userName, perm);
    return projectService.modifyProjectUser(operator, name, userName, perm, response);
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
    logger.info("Operator user id {}, delete user from project, project name: {}, user name: {}, perm: {}", operator.getId(), name, userName);
    projectService.deleteProjectUser(operator, name, userName, response);
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
  public List<ProjectUser> queryUser(@RequestAttribute(value = "session.user") User operator,
                                     @PathVariable("name") String name,
                                     HttpServletResponse response) {
    logger.info("Operator user id {}, query users of project, project name: {}", operator.getId(), name);
    return projectService.queryUser(operator, name, response);
  }
}
