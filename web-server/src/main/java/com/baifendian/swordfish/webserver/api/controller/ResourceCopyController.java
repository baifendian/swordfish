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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/projects/{projectName}/resources-copy")
public class ResourceCopyController {

  private static Logger logger = LoggerFactory.getLogger(ResourceCopyController.class.getName());

  @Autowired
  private ResourceService resourceService;

  /**
   * 拷贝某资源到另外一个资源, 需具备项目的 "w 权限"
   *
   * @param operator
   * @param projectName
   * @param srcResName
   * @param destResName
   * @param desc
   * @param response
   */
  @DeleteMapping(value = "")
  public Resource copyResource(@RequestAttribute(value = "session.user") User operator,
                               @PathVariable String projectName,
                               @RequestParam(value = "srcResName") String srcResName,
                               @RequestParam(value = "destResName") String destResName,
                               @RequestParam(value = "desc", required = false) String desc,
                               HttpServletResponse response) {
    logger.info("Operator user {}, copy resource, project name: {}, source resource name: {}, dest resource name: {}, desc: {}",
        operator.getName(), projectName, srcResName, destResName, desc);

    return resourceService.copyResource(operator, projectName, srcResName, destResName, desc, response);
  }
}
