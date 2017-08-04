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

import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.ExecutorIdDto;
import com.baifendian.swordfish.webserver.dto.LogResult;
import com.baifendian.swordfish.webserver.dto.StreamingResultDto;
import com.baifendian.swordfish.webserver.dto.StreamingResultsDto;
import com.baifendian.swordfish.webserver.exception.BadRequestException;
import com.baifendian.swordfish.webserver.service.StreamingExecService;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/executors")
public class StreamingExecController {

  private static Logger logger = LoggerFactory.getLogger(StreamingExecController.class.getName());

  @Autowired
  private StreamingExecService streamingExecService;

  /**
   * 执行一个流任务
   */
  @PostMapping(value = "/streaming")
  @ResponseStatus(HttpStatus.CREATED)
  public ExecutorIdDto executeStreamingJob(@RequestAttribute(value = "session.user") User operator,
      @RequestParam(value = "projectName") String projectName,
      @RequestParam(value = "name") String name,
      @RequestParam(value = "proxyUser") String proxyUser,
      @RequestParam(value = "queue") String queue) {
    logger.info(
        "Operator user {}, execute streaming job, project name: {}, name: {}, proxy user: {}, queue: {}",
        operator.getName(), projectName, name, proxyUser, queue);

    return streamingExecService.executeStreamingJob(operator, projectName, name, proxyUser, queue);
  }

  /**
   * 关闭流任务
   */
  @PostMapping(value = "/streaming/{execId}/kill")
  public void killStreamingJob(@RequestAttribute(value = "session.user") User operator,
      @PathVariable int execId) {
    logger.info("Operator user {}, kill streaming job, exec id: {}",
        operator.getName(), execId);

    streamingExecService.killStreamingJob(operator, execId);
  }

  /**
   * 恢复一个暂停的流任务
   */
  @PostMapping(value = "/streaming/{execId}/activate")
  public void activateStreamingJob(@RequestAttribute(value = "session.user") User operator,
      @PathVariable int execId) {
    logger.info("Operator user {}, activate streaming job, exec id: {}",
        operator.getName(), execId);

    streamingExecService.activateStreamingJob(operator, execId);
  }

  /**
   * 暂停一个正在运行的流任务
   */
  @PostMapping(value = "/streaming/{execId}/deactivate")
  public void deactivateStreamingJob(@RequestAttribute(value = "session.user") User operator,
      @PathVariable int execId) {
    logger.info("Operator user {}, deactivate streaming job, exec id: {}",
        operator.getName(), execId);

    streamingExecService.deactivateStreamingJob(operator, execId);
  }

  /**
   * 查询项目下流任务及其结果
   */
  @GetMapping(value = "/streamings")
  public StreamingResultsDto queryStreamingExecs(
      @RequestAttribute(value = "session.user") User operator,
      @RequestParam(value = "startDate", required = false) Long startDate,
      @RequestParam(value = "endDate", required = false) Long endDate,
      @RequestParam(value = "projectName") String projectName,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "status", required = false) Integer status,
      @RequestParam(value = "from", required = false, defaultValue = "0") int from,
      @RequestParam(value = "size", required = false, defaultValue = "100") int size) {
    logger.info(
        "Operator user {}, query streaming job exec list, start date: {}, end date: {}, project name: {}, name: {}, status: {}, from: {}, size: {}",
        operator.getName(), startDate, endDate, projectName, name, status, from, size);

    // from 的限制
    if (from < 0) {
      throw new BadRequestException("Argument is not valid, from must be equal or more than zero");
    }

    // size 的限制
    if (size <= 0 || size > 1000) {
      throw new BadRequestException("Argument is not valid, size must be between (0, 1000]");
    }

    return streamingExecService.queryStreamingExecs(operator, projectName, name,
        (startDate == null) ? null : new Date(startDate),
        (endDate == null) ? null : new Date(endDate), status, from, size);
  }

  /**
   * 查询流任务最新运行详情
   */
  @GetMapping(value = "/streaming/latest")
  public List<StreamingResultDto> queryLatest(
      @RequestAttribute(value = "session.user") User operator,
      @RequestParam(value = "projectName") String projectName,
      @RequestParam(value = "names", required = false) String names) {
    logger.info(
        "Operator user {}, query streaming latest exec information, project name: {}, names: {}",
        operator.getName(), projectName, names);

    return streamingExecService.queryLatest(operator, projectName, names);
  }

  /**
   * 查询流任务运行的详情
   */
  @GetMapping(value = "/streaming/{execId}")
  public List<StreamingResultDto> queryDetail(
      @RequestAttribute(value = "session.user") User operator,
      @PathVariable int execId) {
    logger.info("Operator user {}, query streaming result detail, exec id: {}",
        operator.getName(), execId);

    return streamingExecService.queryDetail(operator, execId);
  }

  /**
   * 查询日志接口
   */
  @GetMapping(value = "/streaming/{execId}/logs")
  public LogResult queryLogs(@RequestAttribute(value = "session.user") User operator,
      @PathVariable(value = "execId") int execId,
      @RequestParam(value = "from", required = false, defaultValue = "0") int from,
      @RequestParam(value = "size", required = false, defaultValue = "100") int size,
      @RequestParam(value = "query", required = false) String query) {
    logger.info(
        "Operator user {}, query streaming job log, exec id: {}, from: {}, size: {}, query: {}",
        operator.getName(), execId, from, size, query);

    // from 的限制
    if (from < 0) {
      throw new BadRequestException("Argument is not valid, from must be equal or more than zero");
    }

    // size 的限制
    if (size <= 0 || size > 1000) {
      throw new BadRequestException("Argument is not valid, size must be between (0, 1000]");
    }

    return streamingExecService.queryLogs(operator, execId, from, size, query);
  }
}
