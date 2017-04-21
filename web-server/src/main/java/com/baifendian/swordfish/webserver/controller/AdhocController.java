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

import com.baifendian.swordfish.common.job.UdfsInfo;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.dto.AdHocLogData;
import com.baifendian.swordfish.webserver.dto.AdHocResultData;
import com.baifendian.swordfish.webserver.dto.ExecutorId;
import com.baifendian.swordfish.webserver.service.AdhocService;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 即席查询的服务入口
 */
@RestController
@RequestMapping("")
public class AdhocController {

  private static Logger logger = LoggerFactory.getLogger(AdhocController.class.getName());

  @Autowired
  private AdhocService adhocService;

  /**
   * 执行一个即席查询
   *
   * @param operator
   * @param projectName
   * @param stms
   * @param limit
   * @param proxyUser
   * @param queue
   * @param udfs
   * @param response
   * @return
   */
  @PostMapping(value = "/projects/{projectName}/adHoc")
  public ExecutorId execAdhoc(@RequestAttribute(value = "session.user") User operator,
                              @PathVariable String projectName,
                              @RequestParam(value = "stms") String stms,
                              @RequestParam(value = "limit", required = false, defaultValue = "1000") int limit,
                              @RequestParam(value = "proxyUser") String proxyUser,
                              @RequestParam(value = "queue") String queue,
                              @RequestParam(value = "udfs", required = false) String udfs,
                              @RequestParam(value = "timeout", required = false, defaultValue = "1800") int timeout,
                              HttpServletResponse response) {
    logger.info("Operator user {}, exec adhoc, project name: {}, stms: {}, limit: {}, proxyUser: {}, queue: {}, udfs: {}, timeout: {}",
        operator.getName(), projectName, stms, limit, proxyUser, queue, udfs, timeout);

    // limit 的限制
    if (limit <= 0 || limit > 5000) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      throw new IllegalArgumentException("Argument is not valid, limit must be between (0, 5000]");
    }

    // timeout 的限制
    if (timeout <= 0 || timeout > 14400) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      throw new IllegalArgumentException("Argument is not valid, timeout must be between (0, 14400]");
    }

    // 检验 udfs, 生成对象
    List<UdfsInfo> udfsInfos;

    try {
      udfsInfos = JsonUtil.parseObjectList(udfs, UdfsInfo.class);
    } catch (Exception e) {
      logger.error("Parse json exception.", e);
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      throw new IllegalArgumentException("Argument is not valid, udfs format not a valid.");
    }

    return adhocService.execAdhoc(operator, projectName, stms, limit, proxyUser, queue, udfsInfos, timeout);
  }

  /**
   * 获取即席查询的日志
   *
   * @param operator
   * @param execId
   * @param index
   * @param from
   * @param size
   * @param response
   * @return
   */
  @GetMapping(value = "/adHoc/{execId}/logs")
  public AdHocLogData queryLogs(@RequestAttribute(value = "session.user") User operator,
                                @PathVariable int execId,
                                @RequestParam(value = "index") int index,
                                @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                @RequestParam(value = "size", required = false, defaultValue = "100") int size,
                                HttpServletResponse response) {
    logger.info("Operator user {}, get adhoc logs, exec id: {}, index: {}, from: {}, size: {}",
        operator.getName(), execId, index, from, size);

    // index & from 的限制
    if (index < 0 || from < 0) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      throw new IllegalArgumentException("Argument is not valid, index & from must be equal or more than zero");
    }

    // size 的限制
    if (size <= 0 || size > 1000) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      throw new IllegalArgumentException("Argument is not valid, size must be between (0, 1000]");
    }

    return adhocService.queryLogs(operator, execId, index, from, size);
  }

  /**
   * 获取即席查询的结果
   *
   * @param operator
   * @param execId
   * @param index
   * @param response
   * @return
   */
  @GetMapping(value = "/adHoc/{execId}/result")
  public AdHocResultData queryResult(@RequestAttribute(value = "session.user") User operator,
                                     @PathVariable int execId,
                                     @RequestParam(value = "index") int index,
                                     HttpServletResponse response) {
    logger.info("Operator user {}, get adhoc result, exec id: {}, index: {}",
        operator.getName(), execId, index);

    // index 的限制
    if (index < 0) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      throw new IllegalArgumentException("Argument is not valid, index must be equal or more than zero");
    }

    return adhocService.queryResult(operator, execId, index);
  }
}
