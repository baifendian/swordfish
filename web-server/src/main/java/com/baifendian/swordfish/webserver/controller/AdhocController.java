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

import com.baifendian.swordfish.common.job.struct.node.common.UdfsInfo;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.dto.AdHocLogDto;
import com.baifendian.swordfish.webserver.dto.AdHocResultDto;
import com.baifendian.swordfish.webserver.dto.ExecutorIdDto;
import com.baifendian.swordfish.webserver.exception.BadRequestException;
import com.baifendian.swordfish.webserver.service.AdhocService;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.commons.httpclient.HttpStatus.SC_CREATED;

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
   * @param timeout
   * @return
   */
  @PostMapping(value = "/projects/{projectName}/adHoc")
  @ResponseStatus(HttpStatus.CREATED)
  public ExecutorIdDto execAdhoc(@RequestAttribute(value = "session.user") User operator,
                                 @PathVariable String projectName,
                                 @RequestParam(value = "stms") String stms,
                                 @RequestParam(value = "limit", required = false, defaultValue = "1000") int limit,
                                 @RequestParam(value = "proxyUser") String proxyUser,
                                 @RequestParam(value = "queue") String queue,
                                 @RequestParam(value = "udfs", required = false) String udfs,
                                 @RequestParam(value = "timeout", required = false, defaultValue = "43200") int timeout
                                 ) {
    logger.info("Operator user {}, exec adhoc, project name: {}, stms: {}, limit: {}, proxyUser: {}, queue: {}, udfs: {}, timeout: {}",
            operator.getName(), projectName, stms, limit, proxyUser, queue, udfs, timeout);

    // limit 的限制
    if (limit <= 0 || limit > 5000) {
      throw new BadRequestException("Argument is not valid, limit must be between (0, 5000]");
    }

    // timeout 的限制
    if (timeout <= 0 || timeout > 43200) {
      throw new BadRequestException("Argument is not valid, timeout must be between (0, 43200]");
    }

    // 检验 udfs, 生成对象
    List<UdfsInfo> udfsInfos;

    try {
      udfsInfos = JsonUtil.parseObjectList(udfs, UdfsInfo.class);
    } catch (Exception e) {
      logger.error("Parse json exception.", e);
      throw new BadRequestException("Argument is not valid, udfs format is invalid.");
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
   * @return
   */
  @GetMapping(value = "/adHoc/{execId}/logs")
  public AdHocLogDto queryLogs(@RequestAttribute(value = "session.user") User operator,
                               @PathVariable int execId,
                               @RequestParam(value = "index") int index,
                               @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                               @RequestParam(value = "size", required = false, defaultValue = "100") int size) {
    logger.info("Operator user {}, get adhoc logs, exec id: {}, index: {}, from: {}, size: {}",
            operator.getName(), execId, index, from, size);

    // index & from 的限制
    if (index < 0 || from < 0) {
      throw new BadRequestException("Argument is not valid, index & from must be equal or more than zero");
    }

    // size 的限制
    if (size <= 0 || size > 1000) {
      throw new BadRequestException("Argument is not valid, size must be between (0, 1000]");
    }

    return adhocService.queryLogs(operator, execId, index, from, size);
  }

  /**
   * 获取即席查询的结果
   *
   * @param operator
   * @param execId
   * @param index
   * @return
   */
  @GetMapping(value = "/adHoc/{execId}/result")
  public AdHocResultDto queryResult(@RequestAttribute(value = "session.user") User operator,
                                    @PathVariable int execId,
                                    @RequestParam(value = "index") int index) {
    logger.info("Operator user {}, get adhoc result, exec id: {}, index: {}",
            operator.getName(), execId, index);

    // index 的限制
    if (index < 0) {
      throw new BadRequestException("Argument is not valid, index must be equal or more than zero");
    }

    return adhocService.queryResult(operator, execId, index);
  }

  /**
   * 关闭即席查询
   *
   * @param operator
   * @param execId
   */
  @PostMapping(value = "/adHoc/{execId}/kill")
  public void killAdhoc(@RequestAttribute(value = "session.user") User operator,
                        @PathVariable int execId) {
    logger.info("Operator user {}, kill adhoc result, exec id: {}",
            operator.getName(), execId);

    adhocService.killAdhoc(operator, execId);
  }
}
