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

import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ExecutionFlowError;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.response.StatResponse;
import com.baifendian.swordfish.webserver.service.StatService;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/statistic")
public class StatController {

  private static Logger logger = LoggerFactory.getLogger(StatController.class.getName());

  @Autowired
  private StatService statService;

  /**
   * 统计状态信息
   *
   * @param operator
   * @param projectName
   * @param startTime
   * @param endTime
   * @param response
   * @return
   */
  @GetMapping(value = "/states")
  public List<StatResponse> queryStates(@RequestAttribute(value = "session.user") User operator,
                                        @RequestParam(value = "projectName") String projectName,
                                        @RequestParam(value = "startTime") long startTime,
                                        @RequestParam(value = "endTime") long endTime,
                                        HttpServletResponse response) {
    logger.info("Operator user {}, get states, project name: {}, start time: {}, end time: {}",
        operator.getName(), projectName, startTime, endTime);

    // TODO:: 检测时间跨度, 必须是 (0, 30]

    Date statDate = new Date(startTime);
    Date endDate = new Date(endTime);

    return statService.queryStates(operator, projectName, startTime, endTime);
  }

  /**
   * 返回小时维度的统计信息
   * @param operator
   * @param projectName
   * @param date
   * @param response
   * @return
   */
  @GetMapping(value = "/states-hour")
  public List<StatResponse> queryStatesHour(@RequestAttribute(value = "session.user") User operator,
                                            @RequestParam(value = "projectName") String projectName,
                                            @RequestParam(value = "date") long date,
                                            HttpServletResponse response){
    logger.info("Operator user {}, get states, project name: {}, date: {}",
            operator.getName(), projectName, date);

    return statService.queryStatesHour(operator,projectName,date);
  }

  /**
   * 返回消耗的时间排行
   *
   * @param operator
   * @param projectName
   * @param date
   * @param num
   * @param response
   */
  @GetMapping(value = "/consumes")
  public List<ExecutionFlow> queryTopConsumes(@RequestAttribute(value = "session.user") User operator,
                                              @RequestParam(value = "projectName") String projectName,
                                              @RequestParam(value = "date") long date,
                                              @RequestParam(value = "num") int num,
                                              HttpServletResponse response) {
    logger.info("Operator user {}, get top consumers of workflow,  project name: {}, date: {}, num: {}",
        operator.getName(), projectName, date, num);

    // 校验返回数目
    if (num <= 0 || num > 100) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      throw new IllegalArgumentException("Argument is not valid, num must be between (0, 100]");

    }

    return statService.queryConsumes(operator, projectName, date, num);
  }

  /**
   * 返回错误的排行信息
   *
   * @param operator
   * @param projectName
   * @param date
   * @param num
   * @param response
   */
  @GetMapping(value = "/errors")
  public List<ExecutionFlowError> queryTopErrors(@RequestAttribute(value = "session.user") User operator,
                                                 @RequestParam(value = "projectName") String projectName,
                                                 @RequestParam(value = "date") long date,
                                                 @RequestParam(value = "num") int num,
                                                 HttpServletResponse response) {
    logger.info("Operator user {}, get top errors of workflow, project name: {}, date: {}, num: {}",
        operator.getName(), projectName, date, num);

    // 校验返回数目
    if (num <= 0 || num > 100) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      throw new IllegalArgumentException("Argument is not valid, num must be between (0, 100]");

    }

    return statService.queryErrors(operator, projectName, date, num);
  }
}
