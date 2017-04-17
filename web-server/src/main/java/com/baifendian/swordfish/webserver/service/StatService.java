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
package com.baifendian.swordfish.webserver.service;

import com.baifendian.swordfish.dao.mapper.ExecutionFlowMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.webserver.dto.StatResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StatService {

  private static Logger logger = LoggerFactory.getLogger(StatService.class.getName());

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private ExecutionFlowMapper executionFlowMapper;

  /**
   * 查询状态信息
   *
   * @param operator
   * @param projectName
   * @param startTime
   * @param endTime
   * @param response
   */
  public List<StatResponse> queryStates(User operator, String projectName, long startTime, long endTime, HttpServletResponse response) {

    long timeInt = (endTime - startTime)/86400000;
    if (timeInt > 30){
      logger.error("time interval > 30: {}", timeInt);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    Date startDate = new Date(startTime);
    Date endDate = new Date(endTime);

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to query exec states", operator.getName(), projectName);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    List<ExecutionState> executionStateList = executionFlowMapper.selectStateByProject(project.getId(),startDate,endDate);

    List<StatResponse> statResponseList = new ArrayList<>();

    for (ExecutionState executionState:executionStateList){
      statResponseList.add(new StatResponse(executionState));
    }

    return statResponseList;
  }

  /**
   * 返回查询排行
   *
   * @param operator
   * @param projectName
   * @param date
   * @param num
   * @param response
   */
  public List<ExecutionFlow> queryConsumes(User operator, String projectName, long date, int num, HttpServletResponse response) {

    Date datetime = new Date(date);

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to create project flow", operator.getName(), projectName);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    return executionFlowMapper.selectConsumesByProject(project.getId(),num,datetime);
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
  public List<ExecutionFlowError> queryErrors(User operator, String projectName, long date, int num, HttpServletResponse response) {
    Date datetime = new Date(date);

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to create project flow", operator.getName(), projectName);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    return executionFlowMapper.selectErrorsByProject(project.getId(),num,datetime);
  }
}
