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

import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.dto.UserSessionData;
import com.baifendian.swordfish.webserver.api.service.ExecService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 执行任务的服务入口
 */
@RestController
@RequestMapping("/executors")
public class ExecController {

  private static Logger logger = LoggerFactory.getLogger(ExecController.class.getName());

  @Autowired
  private ExecService execService;

  /**
   * 执行一个已经创建的工作流
   *
   * @param operator
   * @param projectName
   * @param workflowName
   * @param schedule
   * @param nodeName
   * @param nodeDep
   * @param notifyType
   * @param notifyMails
   * @param timeout
   * @param response
   * @return
   */
  @PostMapping(value = "")
  public UserSessionData execExistWorkflow(@RequestAttribute(value = "session.user") User operator,
                                           @RequestParam(value = "projectName") String projectName,
                                           @RequestParam(value = "workflowName") String workflowName,
                                           @RequestParam(value = "schedule", required = false) String schedule,
                                           @RequestParam(value = "nodeName", required = false) String nodeName,
                                           @RequestParam(value = "nodeDep", required = false) String nodeDep,
                                           @RequestParam(value = "notifyType", required = false) String notifyType,
                                           @RequestParam(value = "notifyMails", required = false) String notifyMails,
                                           @RequestParam(value = "timeout", required = false, defaultValue = "1800") int timeout,
                                           HttpServletResponse response) {
    logger.info("Operator user {}, exec workflow, project name: {}, workflow name: {}, schedule: {}, node name: {}, node dep: {}, notify type: {}," +
            "notify mails: {}, timeout: {}",
        operator.getName(), projectName, workflowName, schedule, nodeName, nodeDep, notifyType, notifyMails, timeout);

    return null;
  }

  /**
   * 直接执行一个新的工作流, 这个工作流可能是没有创建的, 有可能是创建了
   *
   * @param operator
   * @param projectName
   * @param workflowName
   * @param proxyUser
   * @param queue
   * @param nodeDep
   * @param file
   * @param notifyType
   * @param notifyMails
   * @param timeout
   * @param response
   * @return
   */
  @PostMapping(value = "/direct")
  public UserSessionData execNewWorkflow(@RequestAttribute(value = "session.user") User operator,
                                         @RequestParam(value = "projectName") String projectName,
                                         @RequestParam(value = "workflowName") String workflowName,
                                         @RequestParam(value = "proxyUser") String proxyUser,
                                         @RequestParam(value = "queue") String queue,
                                         @RequestParam(value = "data", required = false) String nodeDep,
                                         @RequestParam(value = "file", required = false) MultipartFile file,
                                         @RequestParam(value = "notifyType", required = false) String notifyType,
                                         @RequestParam(value = "notifyMails", required = false) String notifyMails,
                                         @RequestParam(value = "timeout", required = false, defaultValue = "1800") int timeout,
                                         HttpServletResponse response) {
    logger.info("Operator user {}, direct exec workflow, project name: {}, workflow name: {}, proxy user: {}, queue: {}, node dep: {}, file name: [{},{}], notify type: {}," +
            "notify mails: {}, timeout: {}",
        operator.getName(), projectName, workflowName, proxyUser, queue, nodeDep, file.getName(), file.getOriginalFilename(), notifyType, notifyMails, timeout);

    return null;
  }

  /**
   * 查询工作流列表信息
   *
   * @param operator
   * @param startDate
   * @param endDate
   * @param projectName
   * @param workflowName
   * @param status
   * @param from
   * @param size
   * @param response
   * @return
   */
  @GetMapping(value = "")
  public UserSessionData queryExecs(@RequestAttribute(value = "session.user") User operator,
                                    @RequestParam(value = "startDate") long startDate,
                                    @RequestParam(value = "endDate") long endDate,
                                    @RequestParam(value = "projectName") String projectName,
                                    @RequestParam(value = "workflowName", required = false) String workflowName,
                                    @RequestParam(value = "status", required = false) String status,
                                    @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                    @RequestParam(value = "size", required = false, defaultValue = "100") int size,
                                    HttpServletResponse response) {
    logger.info("Operator user {}, query exec list, start date: {}, end date: {}, project name: {}, workflow name: {}, status: {}, from: {}, size: {}",
        operator.getName(), startDate, endDate, projectName, workflowName, status, from, size);

    return null;
  }

  /**
   * 查询工作流的执行详情
   *
   * @param operator
   * @param execId
   * @param response
   * @return
   */
  @GetMapping(value = "/{execId}")
  public UserSessionData queryExecDetail(@RequestAttribute(value = "session.user") User operator,
                                         @PathVariable(value = "execId") int execId,
                                         HttpServletResponse response) {
    logger.info("Operator user {}, query exec detail, exec id: {}",
        operator.getName(), execId);

    return null;
  }

  /**
   * 查询日志
   *
   * @param operator
   * @param jobId
   * @param from
   * @param size
   * @param response
   * @return
   */
  @GetMapping(value = "/{jobId}/logs")
  public UserSessionData queryLogs(@RequestAttribute(value = "session.user") User operator,
                                   @PathVariable(value = "jobId") int jobId,
                                   @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                   @RequestParam(value = "size", required = false, defaultValue = "100") int size,
                                   HttpServletResponse response) {
    logger.info("Operator user {}, query log, job id: {}, from: {}, size: {}",
        operator.getName(), jobId, from, size);

    return null;
  }

  /**
   * kill 某个运行的任务
   *
   * @param operator
   * @param execId
   * @param response
   */
  @PostMapping(value = "/{execId}/kill")
  public void killExec(@RequestAttribute(value = "session.user") User operator,
                       @PathVariable String execId,
                       HttpServletResponse response) {
    logger.info("Operator user {}, kill exec, exec id: {}",
        operator.getName(), execId);


  }
}
