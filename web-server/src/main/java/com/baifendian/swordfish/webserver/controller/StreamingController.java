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
import com.baifendian.swordfish.webserver.dto.StreamingJobDto;
import com.baifendian.swordfish.webserver.dto.StreamingResultDto;
import com.baifendian.swordfish.webserver.exception.BadRequestException;
import com.baifendian.swordfish.webserver.service.StreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
public class StreamingController {

  private static Logger logger = LoggerFactory.getLogger(StreamingController.class.getName());

  @Autowired
  private StreamingService streamingService;

  /**
   * 创建流任务
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param type
   * @param parameter
   * @param userDefParams
   * @param extras
   * @return
   */
  @PostMapping(value = "/projects/{projectName}/streaming/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  public StreamingJobDto createStreamingJob(@RequestAttribute(value = "session.user") User operator,
                                            @PathVariable String projectName,
                                            @PathVariable String name,
                                            @RequestParam(value = "desc", required = false) String desc,
                                            @RequestParam(value = "type") String type,
                                            @RequestParam(value = "parameter") String parameter,
                                            @RequestParam(value = "userDefParams", required = false) String userDefParams,
                                            @RequestParam(value = "extras", required = false) String extras) {
    logger.info("Operator user {}, create streaming job, project name: {}, name: {}, desc: {}, type: {}, parameter: {}, user define parameters: {}, extras: {}",
        operator.getName(), projectName, name, desc, type, parameter, userDefParams, extras);

    return new StreamingJobDto(streamingService.createStreamingJob(operator, projectName, name, desc, type, parameter, userDefParams, extras));
  }

  /**
   * 修改或创建一个流任务, 不存在则创建
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param type
   * @param parameter
   * @param userDefParams
   * @param extras
   * @return
   */
  @PutMapping(value = "/projects/{projectName}/streaming/{name}")
  public StreamingJobDto putStreamingJob(@RequestAttribute(value = "session.user") User operator,
                                         @PathVariable String projectName,
                                         @PathVariable String name,
                                         @RequestParam(value = "desc", required = false) String desc,
                                         @RequestParam(value = "type") String type,
                                         @RequestParam(value = "parameter") String parameter,
                                         @RequestParam(value = "userDefParams", required = false) String userDefParams,
                                         @RequestParam(value = "extras", required = false) String extras) {
    logger.info("Operator user {}, modify and create streaming job, project name: {}, name: {}, desc: {}, type: {}, parameter: {}, user define parameters: {}, extras: {}",
        operator.getName(), projectName, name, desc, type, parameter, userDefParams, extras);

    return new StreamingJobDto(streamingService.putStreamingJob(operator, projectName, name, desc, type, parameter, userDefParams, extras));
  }

  /**
   * 修改一个流任务
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param parameter
   * @param userDefParams
   * @param extras
   * @return
   */
  @PatchMapping(value = "/projects/{projectName}/streaming/{name}")
  public StreamingJobDto patchStreamingJob(@RequestAttribute(value = "session.user") User operator,
                                           @PathVariable String projectName,
                                           @PathVariable String name,
                                           @RequestParam(value = "desc", required = false) String desc,
                                           @RequestParam(value = "parameter") String parameter,
                                           @RequestParam(value = "userDefParams", required = false) String userDefParams,
                                           @RequestParam(value = "extras", required = false) String extras) {
    logger.info("Operator user {}, modify streaming job, project name: {}, name: {}, desc: {}, parameter: {}, user define parameters: {}, extras: {}",
        operator.getName(), projectName, name, desc, parameter, userDefParams, extras);

    return new StreamingJobDto(streamingService.patchStreamingJob(operator, projectName, name, desc, parameter, userDefParams, extras));
  }

  /**
   * 删除一个流任务
   *
   * @param operator
   * @param projectName
   * @param name
   */
  @DeleteMapping(value = "/projects/{projectName}/streaming/{name}")
  public void deleteStreamingJob(@RequestAttribute(value = "session.user") User operator,
                                 @PathVariable String projectName,
                                 @PathVariable String name) {
    logger.info("Operator user {}, delete streaming job, project name: {}, name: {}",
        operator.getName(), projectName, name);

    streamingService.deleteStreamingJob(operator, projectName, name);
  }

  /**
   * 执行一个流任务
   *
   * @param operator
   * @param projectName
   * @param name
   * @param proxyUser
   * @param queue
   */
  @PostMapping(value = "/executors/streaming")
  public ExecutorIdDto executeStreamingJob(@RequestAttribute(value = "session.user") User operator,
                                           @RequestParam(value = "projectName") String projectName,
                                           @RequestParam(value = "name") String name,
                                           @RequestParam(value = "proxyUser") String proxyUser,
                                           @RequestParam(value = "queue") String queue) {
    logger.info("Operator user {}, execute streaming job, project name: {}, name: {}, proxy user: {}, queue: {}",
        operator.getName(), projectName, name, proxyUser, queue);

    return streamingService.executeStreamingJob(operator, projectName, name, proxyUser, queue);
  }

  /**
   * 关闭流任务
   *
   * @param operator
   * @param execId
   * @return
   */
  @PostMapping(value = "/executors/streaming/{execId}/kill")
  public void killStreamingJob(@RequestAttribute(value = "session.user") User operator,
                               @PathVariable int execId) {
    logger.info("Operator user {}, kill streaming job, exec id: {}",
        operator.getName(), execId);

    streamingService.killStreamingJob(operator, execId);
  }

  /**
   * 查询项目下流任务及其结果
   *
   * @param operator
   * @param projectName
   * @return
   */
  @GetMapping(value = "/projects/{projectName}/streamings")
  public List<StreamingResultDto> queryProjectStreamingJobAndResult(@RequestAttribute(value = "session.user") User operator,
                                                                    @PathVariable String projectName) {
    logger.info("Operator user {}, query streaming job and result of project, project name: {}",
        operator.getName(), projectName);

    return streamingService.queryProjectStreamingJobAndResult(operator, projectName);
  }

  /**
   * 查询具体某个流任务的详情
   *
   * @param operator
   * @param execId
   * @return
   */
  @GetMapping(value = "/executors/streaming/{execId}")
  public List<StreamingResultDto> queryStreamingJobAndResult(@RequestAttribute(value = "session.user") User operator,
                                                             @PathVariable int execId) {
    logger.info("Operator user {}, query streaming job and result, exec id: {}",
        operator.getName(), execId);

    return streamingService.queryStreamingJobAndResult(operator, execId);
  }

  /**
   * 查询日志接口
   *
   * @param operator
   * @param execId
   * @param from
   * @param size
   * @return
   */
  @GetMapping(value = "/executors/streaming/{execId}/logs")
  public LogResult queryLogs(@RequestAttribute(value = "session.user") User operator,
                             @PathVariable(value = "execId") String execId,
                             @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                             @RequestParam(value = "size", required = false, defaultValue = "100") int size) {
    logger.info("Operator user {}, query streaming job log, exec id: {}, from: {}, size: {}",
        operator.getName(), execId, from, size);

    // from 的限制
    if (from < 0) {
      throw new BadRequestException("Argument is not valid, from must be equal or more than zero");
    }

    // size 的限制
    if (size <= 0 || size > 1000) {
      throw new BadRequestException("Argument is not valid, size must be between (0, 1000]");
    }

    return streamingService.getStreamingJobLog(operator, execId, from, size);
  }
}
