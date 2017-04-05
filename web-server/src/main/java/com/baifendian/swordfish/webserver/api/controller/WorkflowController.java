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

/**
 * 工作流管理的服务入口
 */

import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/projects/{projectName}")
public class WorkflowController {

  private static Logger logger = LoggerFactory.getLogger(WorkflowController.class.getName());

  @Autowired
  private WorkflowService workflowService;

  /**
   * 创建工作流
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param proxyUser
   * @param queue
   * @param data
   * @param file
   * @param response
   */
  @RequestMapping(value = "/workflows/{name}", method = {RequestMethod.POST})
  public ProjectFlow createWorkflow(@RequestAttribute(value = "session.user") User operator,
                                    @PathVariable String projectName,
                                    @PathVariable String name,
                                    @RequestParam(value = "desc", required = false) String desc,
                                    @RequestParam(value = "proxyUser") String proxyUser,
                                    @RequestParam(value = "queue") String queue,
                                    @RequestParam(value = "data") String data,
                                    @RequestParam("file") MultipartFile file,
                                    HttpServletResponse response) {
    return workflowService.createWorkflow(operator,projectName,name,desc,proxyUser,queue,data,file,response);
  }

  /**
   * 修改或创建一个工作流
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param proxyUser
   * @param queue
   * @param data
   * @param file
   * @param response
   */
  @RequestMapping(value = "/workflows/{name}", method = {RequestMethod.PUT})
  public ProjectFlow putWorkflow(@RequestAttribute(value = "session.user") User operator,
                             @PathVariable String projectName,
                             @PathVariable String name,
                             @RequestParam(value = "desc", required = false) String desc,
                             @RequestParam(value = "proxyUser") String proxyUser,
                             @RequestParam(value = "queue") String queue,
                             @RequestParam(value = "data") String data,
                             @RequestParam("file") MultipartFile file,
                             HttpServletResponse response) {

    return workflowService.putWorkflow(operator,projectName,name,desc,proxyUser,queue,data,file,response);
  }

  /**
   * 修改或创建一个工作流
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param proxyUser
   * @param queue
   * @param data
   * @param file
   * @param response
   */
  @RequestMapping(value = "/workflows/{name}", method = {RequestMethod.PATCH})
  public ProjectFlow patchWorkflow(@RequestAttribute(value = "session.user") User operator,
                                 @PathVariable String projectName,
                                 @PathVariable String name,
                                 @RequestParam(value = "desc", required = false) String desc,
                                 @RequestParam(value = "proxyUser") String proxyUser,
                                 @RequestParam(value = "queue") String queue,
                                 @RequestParam(value = "data") String data,
                                 @RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) {

    return workflowService.patchWorkflow(operator,projectName,name,desc,proxyUser,queue,data,file,response);
  }

  /**
   * 删除一个工作流
   * @param operator
   * @param projectName
   * @param name
   * @param response
   */
  @RequestMapping(value = "/workflows/{name}", method = {RequestMethod.DELETE})
  public void modifyWorkflow(@RequestAttribute(value = "session.user") User operator,
                             @PathVariable String projectName,
                             @PathVariable String name,
                             HttpServletResponse response) {
    workflowService.deleteProjectFlow(operator,projectName,name,response);

  }

  @RequestMapping(value = "/workflows-conf", method = {RequestMethod.DELETE})
  public void modifyWorkflowConf(@RequestAttribute(value = "session.user") User operator,
                                 @PathVariable String projectName,
                                 @PathVariable String name,
                                 @RequestParam(value = "queue", required = false) String queue,
                                 @RequestParam(value = "proxyUser", required = false) String proxyUser,
                                 HttpServletResponse response){
    workflowService.modifyWorkflowConf(operator,projectName,queue,proxyUser,response);
  }

  /**
   * 查询一个项目下所有的工作流
   * @param operator
   * @param projectName
   * @param response
   * @return
   */
  @RequestMapping(value = "", method = {RequestMethod.GET})
  public List<ProjectFlow> queryWorkflow(@RequestAttribute(value = "session.user") User operator,
                                         @PathVariable String projectName,
                                         HttpServletResponse response) {
    return workflowService.queryAllProjectFlow(operator,projectName,response);

  }

  /**
   * 查询一个具体的工作流
   * @param operator
   * @param projectName
   * @param name
   * @param response
   * @return
   */
  @RequestMapping(value = "/workflows/{name}", method = {RequestMethod.GET})
  public ProjectFlow queryWorkflowDetail(@RequestAttribute(value = "session.user") User operator,
                                  @PathVariable String projectName,
                                  @PathVariable String name,
                                  HttpServletResponse response) {
    return workflowService.queryProjectFlow(operator,projectName,name,response);
  }

  /**
   * 下载指定文件
   * @param operator
   * @param projectName
   * @param name
   * @param response
   * @return
   */
  @RequestMapping(value = "/workflows/{name}/file", method = {RequestMethod.GET})
  public ResponseEntity<Resource> downloadWorkflowDetail(@RequestAttribute(value = "session.user") User operator,
                                                         @PathVariable String projectName,
                                                         @PathVariable String name,
                                                         HttpServletResponse response) {
    org.springframework.core.io.Resource file = workflowService.queryProjectFlowFile(operator,projectName,name,response);
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
