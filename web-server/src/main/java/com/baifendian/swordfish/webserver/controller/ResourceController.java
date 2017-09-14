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

import com.baifendian.swordfish.dao.model.Resource;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.ResourceDto;
import com.baifendian.swordfish.webserver.service.ResourceService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
   */
  @PostMapping(value = "/{name:.+}")
  @ResponseStatus(HttpStatus.CREATED)
  public ResourceDto createResource(@RequestAttribute(value = "session.user") User operator,
                                    @PathVariable String projectName,
                                    @PathVariable String name,
                                    @RequestParam(value = "desc", required = false) String desc,
                                    @RequestParam("file") MultipartFile file) {
    logger.info("Operator user {}, create resource, project name: {}, resource name: {}, desc: {}, file: [{},{}]",
        operator.getName(), projectName, name, desc, file.getName(), file.getOriginalFilename());

    return new ResourceDto(resourceService.createResource(operator, projectName, name, desc, file));
  }

  /**
   * 修改资源, 如果不存在则会创建, 需要具备项目的 "w 权限"
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param file
   * @return
   */
  @PutMapping(value = "/{name:.+}")
  public ResourceDto putResource(@RequestAttribute(value = "session.user") User operator,
                                 @PathVariable String projectName,
                                 @PathVariable String name,
                                 @RequestParam(value = "desc", required = false) String desc,
                                 @RequestParam("file") MultipartFile file) {
    logger.info("Operator user {}, put resource, project name: {}, resource name: {}, desc: {}, file: [{},{}]",
        operator.getName(), projectName, name, desc, file.getName(), file.getOriginalFilename());

    return new ResourceDto(resourceService.putResource(operator, projectName, name, desc, file));
  }

  /**
   * 部分修改资源, 需要具备项目的 "w 权限"
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param file
   */
  @PatchMapping(value = "/{name:.+}")
  public ResourceDto modifyResource(@RequestAttribute(value = "session.user") User operator,
                                    @PathVariable String projectName,
                                    @PathVariable String name,
                                    @RequestParam(value = "desc", required = false) String desc,
                                    @RequestParam(value = "file", required = false) MultipartFile file) {
    logger.info("Operator user {}, modify resource, project name: {}, resource name: {}, desc: {}, file: [{},{}]",
        operator.getName(), projectName, name, desc, (file == null) ? null : file.getName(), (file == null) ? null : file.getOriginalFilename());

    return new ResourceDto(resourceService.modifyResource(operator, projectName, name, desc, file));
  }

  /**
   * 需要具备项目的 "w 权限"
   *
   * @param operator
   * @param projectName
   * @param name
   */
  @DeleteMapping(value = "/{name:.+}")
  public void deleteResource(@RequestAttribute(value = "session.user") User operator,
                             @PathVariable String projectName,
                             @PathVariable String name) {
    logger.info("Operator user {}, delete resource, project name: {}, resource name: {}",
        operator.getName(), projectName, name);

    resourceService.deleteResource(operator, projectName, name);
  }

  /**
   * 需要具备项目的 "r 权限"
   *
   * @param operator
   * @param projectName
   */
  @GetMapping(value = "")
  public List<ResourceDto> getResources(@RequestAttribute(value = "session.user") User operator,
                                        @PathVariable String projectName) {
    logger.info("Operator user {}, get resource list of project, project name: {}",
        operator.getName(), projectName);

    List<Resource> resourceList = resourceService.getResources(operator, projectName);
    List<ResourceDto> resourceDtoList = new ArrayList<>();
    for (Resource resource : resourceList) {
      resourceDtoList.add(new ResourceDto(resource));
    }
    return resourceDtoList;
  }

  /**
   * 需要具备项目的 "r 权限"
   *
   * @param operator
   * @param projectName
   * @param name
   */
  @GetMapping(value = "/{name:.+}")
  public ResourceDto getResource(@RequestAttribute(value = "session.user") User operator,
                                 @PathVariable String projectName,
                                 @PathVariable String name) {
    logger.info("Operator user {}, get resource detail, project name: {}, resource name: {}",
        operator.getName(), projectName, name);

    return new ResourceDto(resourceService.getResource(operator, projectName, name));
  }

  /**
   * 下载资源, 须有资源的 'r 权限'
   *
   * @param operator
   * @param projectName
   * @param name
   */
  @GetMapping(value = "/{name:.+}/file")
  @ResponseBody
  public ResponseEntity<org.springframework.core.io.Resource> downloadResource(@RequestAttribute(value = "session.user") User operator,
                                                                               @PathVariable String projectName,
                                                                               @PathVariable String name) {
    logger.info("Operator user {}, download resource, project name: {}, resource name: {}",
        operator.getName(), projectName, name);

    org.springframework.core.io.Resource file = resourceService.downloadResource(operator, projectName, name);

    if (file == null) {
      return ResponseEntity
          .noContent().build();
    }

    return ResponseEntity
        .ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }
}
