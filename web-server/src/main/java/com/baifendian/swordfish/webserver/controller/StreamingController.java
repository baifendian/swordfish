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
import com.baifendian.swordfish.webserver.dto.StreamingJobDto;
import com.baifendian.swordfish.webserver.service.StreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectName}")
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
   * @return
   */
  @PostMapping(value = "/streaming/{name}")
  @ResponseStatus(HttpStatus.CREATED)
  public StreamingJobDto createStreamingJob(@RequestAttribute(value = "session.user") User operator,
                                            @PathVariable String projectName,
                                            @PathVariable String name,
                                            @RequestParam(value = "desc", required = false) String desc,
                                            @RequestParam(value = "type") String type,
                                            @RequestParam(value = "parameter") String parameter,
                                            @RequestParam(value = "userDefParams", required = false) String userDefParams) {
    logger.info("Operator user {}, create streaming job, project name: {}, name: {}, desc: {}, type: {}, parameter: {}, user define parameters: {}",
        operator.getName(), projectName, name, desc, type, parameter, userDefParams);

    return new StreamingJobDto(streamingService.createStreamingJob(operator, projectName, name, desc, type, parameter, userDefParams));
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
   * @return
   */
  @PutMapping(value = "/streaming/{name}")
  public StreamingJobDto putStreamingJob(@RequestAttribute(value = "session.user") User operator,
                                         @PathVariable String projectName,
                                         @PathVariable String name,
                                         @RequestParam(value = "desc", required = false) String desc,
                                         @RequestParam(value = "type") String type,
                                         @RequestParam(value = "parameter") String parameter,
                                         @RequestParam(value = "userDefParams", required = false) String userDefParams) {
    logger.info("Operator user {}, modify and create streaming job, project name: {}, name: {}, desc: {}, type: {}, parameter: {}, user define parameters: {}",
        operator.getName(), projectName, name, desc, type, parameter, userDefParams);

    return new StreamingJobDto(streamingService.putStreamingJob(operator, projectName, name, desc, type, parameter, userDefParams));
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
   * @return
   */
  @PatchMapping(value = "/streaming/{name}")
  public StreamingJobDto patchStreamingJob(@RequestAttribute(value = "session.user") User operator,
                                           @PathVariable String projectName,
                                           @PathVariable String name,
                                           @RequestParam(value = "desc", required = false) String desc,
                                           @RequestParam(value = "parameter") String parameter,
                                           @RequestParam(value = "userDefParams", required = false) String userDefParams) {
    logger.info("Operator user {}, modify streaming job, project name: {}, name: {}, desc: {}, parameter: {}, user define parameters: {}",
        operator.getName(), projectName, name, desc, parameter, userDefParams);

    return new StreamingJobDto(streamingService.patchStreamingJob(operator, projectName, name, desc, parameter, userDefParams));
  }

  /**
   * 删除一个流任务
   *
   * @param operator
   * @param projectName
   * @param name
   */
  @DeleteMapping(value = "/streaming/{name}")
  public void deleteStreamingJob(@RequestAttribute(value = "session.user") User operator,
                                 @PathVariable String projectName,
                                 @PathVariable String name) {
    logger.info("Operator user {}, delete streaming job, project name: {}, name: {}",
        operator.getName(), projectName, name);

    streamingService.deleteStreamingJob(operator, projectName, name);
  }

  /**
   * 查询项目下的所有流任务
   *
   * @param operator
   * @param projectName
   */
  @GetMapping(value = "/streamings")
  public List<StreamingJobDto> queryProjectStreamingJobs(@RequestAttribute(value = "session.user") User operator,
                                                         @PathVariable String projectName) {
    logger.info("Operator user {}, query project streaming jobs, project name: {}",
        operator.getName(), projectName);

    return streamingService.queryProjectStreamingJobs(operator, projectName);
  }

  /**
   * 查询某个流任务详情
   *
   * @param operator
   * @param projectName
   */
  @GetMapping(value = "/streaming/{name}")
  public List<StreamingJobDto> queryStreamingJob(@RequestAttribute(value = "session.user") User operator,
                                                 @PathVariable String projectName,
                                                 @PathVariable String name) {
    logger.info("Operator user {}, query streaming job, project name: {}, name: {}",
        operator.getName(), projectName, name);

    return streamingService.queryStreamingJob(operator, projectName, name);
  }
}
