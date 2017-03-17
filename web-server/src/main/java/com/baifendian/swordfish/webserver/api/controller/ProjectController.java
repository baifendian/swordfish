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

import com.baifendian.swordfish.webserver.api.service.ProjectService;
import org.apache.tools.ant.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 项目管理入口
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {
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
  @RequestMapping(value = "/{name}", method = {RequestMethod.POST})
  public Project createProject(@PathVariable("name") String name,
                               @RequestParam(value = "desc", required = false) String desc,
                               HttpServletResponse response) {
    return null;
  }

  /**
   * 修改一个项目, 如果不存在, 会返回错误
   *
   * @param name
   * @param desc
   * @param response
   * @return
   */
  @RequestMapping(value = "/{name}", method = {RequestMethod.PUT})
  public Project modifyProject(@PathVariable("name") String name,
                               @RequestParam(value = "desc", required = false) String desc,
                               HttpServletResponse response) {
    return null;
  }

  /**
   * 删除项目
   *
   * @param name
   * @param response
   * @return
   */
  @RequestMapping(value = "/{name}", method = {RequestMethod.DELETE})
  public Project deleteProject(@PathVariable("name") String name,
                               HttpServletResponse response) {
    return null;
  }

  @RequestMapping(value = "/", method = {RequestMethod.GET})
  public Project queryProjects(HttpServletResponse response) {
    return null;
  }
}
