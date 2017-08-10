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

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.common.job.struct.node.common.UdfsInfo;
import com.baifendian.swordfish.dao.enums.SqlEngineType;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.dto.AdHocDto;
import com.baifendian.swordfish.webserver.dto.AdHocLogDto;
import com.baifendian.swordfish.webserver.dto.AdHocResultDto;
import com.baifendian.swordfish.webserver.dto.ExecutorIdDto;
import com.baifendian.swordfish.webserver.exception.BadRequestException;
import com.baifendian.swordfish.webserver.service.AdhocService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
   */
  @PostMapping(value = "/projects/{projectName}/adHoc/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  public ExecutorIdDto execAdhoc(@RequestAttribute(value = "session.user") User operator,
      @PathVariable String projectName,
      @PathVariable String name,
      @RequestParam(value = "stms") String stms,
      @RequestParam(value = "limit", required = false, defaultValue = "1000") int limit,
      @RequestParam(value = "proxyUser") String proxyUser,
      @RequestParam(value = "queue") String queue,
      @RequestParam(value = "type", required = false) SqlEngineType type,
      @RequestParam(value = "udfs", required = false) String udfs,
      @RequestParam(value = "timeout", required = false) Integer timeout
  ) {
    logger.info(
        "Operator user {}, exec adhoc, project name: {}, adhoc name: {}, stms: {}, limit: {}, "
            + "proxyUser: {}, queue: {}, type: {}, udfs: {}, timeout: {}",
        operator.getName(), projectName, name, stms, limit, proxyUser, queue, type, udfs, timeout);

    // limit 的限制
    if (limit <= 0 || limit > 5000) {
      throw new BadRequestException("Argument is not valid, limit must be between (0, 5000]");
    }

    if (timeout == null) {
      timeout = Constants.TASK_MAX_TIMEOUT;
    }

    // timeout 的限制
    if (timeout <= 0 || timeout > Constants.TASK_MAX_TIMEOUT) {
      throw new BadRequestException(String
          .format("Argument is not valid, timeout must be between (0, %d]",
              Constants.TASK_MAX_TIMEOUT));
    }

    // 检验 udfs, 生成对象
    List<UdfsInfo> udfsInfos;

    try {
      udfsInfos = JsonUtil.parseObjectList(udfs, UdfsInfo.class);
    } catch (Exception e) {
      logger.error("Parse json exception.", e);
      throw new BadRequestException("Argument is not valid, udfs format is invalid.");
    }

    // 默认是 hive
    if (type == null) {
      type = SqlEngineType.HIVE;
    }

    return adhocService
        .execAdhoc(operator, projectName, name, stms, limit, proxyUser, type, queue, udfsInfos,
            timeout);
  }

  /**
   * 获取即席查询的日志
   */
  @GetMapping(value = "/adHoc/{execId}/logs")
  public AdHocLogDto queryLogs(@RequestAttribute(value = "session.user") User operator,
      @PathVariable int execId,
      @RequestParam(value = "index") int index,
      @RequestParam(value = "from", required = false, defaultValue = "0") int from,
      @RequestParam(value = "size", required = false, defaultValue = "100") int size,
      @RequestParam(value = "query", required = false) String query) {
    logger.info(
        "Operator user {}, get adhoc logs, exec id: {}, index: {}, from: {}, size: {}, query: {}",
        operator.getName(), execId, index, from, size, query);

    // index & from 的限制
    if (index < 0 || from < 0) {
      throw new BadRequestException(
          "Argument is not valid, index & from must be equal or more than zero");
    }

    // size 的限制
    if (size <= 0 || size > 1000) {
      throw new BadRequestException("Argument is not valid, size must be between (0, 1000]");
    }

    return adhocService.queryLogs(operator, execId, index, from, size, query);
  }

  /**
   * 获取即席查询的结果
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
   */
  @PostMapping(value = "/adHoc/{execId}/kill")
  public void killAdhoc(@RequestAttribute(value = "session.user") User operator,
      @PathVariable int execId) {
    logger.info("Operator user {}, kill adhoc result, exec id: {}",
        operator.getName(), execId);

    adhocService.killAdhoc(operator, execId);
  }

  /**
   * 根据名称查询一个即席查询的历史记录
   */
  @GetMapping(value = "/projects/{projectName}/adHoc/{name}")
  public List<AdHocDto> getAdhocQuery(@RequestAttribute(value = "session.user") User operator,
      @PathVariable String projectName,
      @PathVariable String name) {
    logger.info("Operator user {}, get adhoc query, project name: {}, adhoc name: {}",
        operator.getName(), projectName, name);

    return adhocService.getAdHoc(operator, projectName, name);
  }

  /**
   * 根据 name 删除一个即系查询记录
   */
  @DeleteMapping(value = "/projects/{projectName}/adHoc/{name}")
  public void deleteAdhocQuery(@RequestAttribute(value = "session.user") User operator,
      @PathVariable String projectName,
      @PathVariable String name) {
    logger.info("Operator user {}, delete adhoc query, project name: {}, adhoc name: {}",
        operator.getName(), projectName, name);

    adhocService.deleteAdHoc(operator, projectName, name);

  }
}
