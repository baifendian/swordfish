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

import com.baifendian.swordfish.dao.model.Resource;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.service.ResourceService;
import com.sun.tools.javac.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 资源管理入口
 */
@RestController
@RequestMapping("/projects/{projectName}/resources")
public class ResourceController {

  private static Logger logger = LoggerFactory.getLogger(ResourceController.class.getName());

  @Autowired
  private ResourceService resourceService;

  /**
   * 创建资源, 需要具备项目的 "w 权限"
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param file
   * @param response
   */
  @PostMapping(value = "/{name}")
  public Resource createResource(@RequestAttribute(value = "session.user") User operator,
                                 @PathVariable String projectName,
                                 @PathVariable String name,
                                 @RequestParam(value = "desc", required = false) String desc,
                                 @RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) {
    logger.info("Operator user id {}, create resource, project name: {}, resource name: {}, desc: {}, file: [{},{}]",
        operator.getId(), projectName, name, desc, file.getName(), file.getOriginalFilename());

    return resourceService.createResource(operator, projectName, name, desc, file, response);
  }

  /**
   * 修改资源, 需要具备项目的 "w 权限"
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param file
   * @param response
   */
  @PatchMapping(value = "/{name}")
  public Resource modifyResource(@RequestAttribute(value = "session.user") User operator,
                                 @PathVariable String projectName,
                                 @PathVariable String name,
                                 @RequestParam(value = "desc", required = false) String desc,
                                 @RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) {
    logger.info("Operator user id {}, modify resource, project name: {}, resource name: {}, desc: {}",
        operator.getId(), projectName, name, desc);

    return resourceService.modifyResource(operator, projectName, name, desc, file, response);
  }

  /**
   * 需要具备项目的 "w 权限"
   *
   * @param operator
   * @param projectName
   * @param name
   * @param response
   */
  @DeleteMapping(value = "/{name}")
  public void deleteResource(@RequestAttribute(value = "session.user") User operator,
                             @PathVariable String projectName,
                             @PathVariable String name,
                             HttpServletResponse response) {
    logger.info("Operator user id {}, delete resource, project name: {}, resource name: {}",
        operator.getId(), projectName, name);

    resourceService.deleteResource(operator, projectName, name, response);
  }

  /**
   * 需要具备项目的 "r 权限"
   *
   * @param operator
   * @param projectName
   * @param response
   */
  @GetMapping(value = "")
  public List<Resource> getResources(@RequestAttribute(value = "session.user") User operator,
                                     @PathVariable String projectName,
                                     HttpServletResponse response) {
    logger.info("Operator user id {}, retrieve resource of the project, project name: {}",
        operator.getId(), projectName);

    return null;
  }

  /**
   * 需要具备项目的 "r 权限"
   *
   * @param operator
   * @param projectName
   * @param name
   * @param response
   */
  @GetMapping(value = "/{name}")
  public List<Resource> getResource(@RequestAttribute(value = "session.user") User operator,
                                    @PathVariable String projectName,
                                    @PathVariable String name,
                                    HttpServletResponse response) {
    logger.info("Operator user id {}, retrieve resource, project name: {}, resource name: {}",
        operator.getId(), projectName, name);

    return null;
  }

  /**
   * 下载资源, 须有资源的 'r 权限'
   *
   * @param operator
   * @param projectName
   * @param name
   * @param response
   */
  @GetMapping(value = "/{name}/file")
  @ResponseBody
  public ResponseEntity<org.springframework.core.io.Resource> downloadResource(@RequestAttribute(value = "session.user") User operator,
                                                                               @PathVariable String projectName,
                                                                               @PathVariable String name,
                                                                               HttpServletResponse response) {
    logger.info("Operator user id {}, download resource, project name: {}, resource name: {}",
        operator.getId(), projectName, name);

    org.springframework.core.io.Resource file = resourceService.downloadResource(operator, projectName, name, response);

    if(file == null) {
      return ResponseEntity
          .noContent().build();
    }

    return ResponseEntity
        .ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }
}
