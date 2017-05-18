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
import com.baifendian.swordfish.webserver.dto.StatDto;
import com.baifendian.swordfish.webserver.exception.NotFoundException;
import com.baifendian.swordfish.webserver.exception.ParameterException;
import com.baifendian.swordfish.webserver.exception.PermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StatService {

  private static Logger logger = LoggerFactory.getLogger(StatService.class.getName());

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
   */
  public List<StatDto> queryStates(User operator, String projectName, long startTime, long endTime) {

    long timeInt = (endTime - startTime) / 86400000;
    if (timeInt > 30) {
      logger.error("time interval > 30: {}", timeInt);
      throw new ParameterException("time \"{0}\" > 30", timeInt);
    }

    Date startDate = new Date(startTime);
    Date endDate = new Date(endTime);

    Project project = projectService.existProjectName(projectName);

    //需要有project 执行权限
    projectService.hasExecPerm(operator, project);

    List<ExecutionState> executionStateList = executionFlowMapper.selectStateByProject(project.getId(), startDate, endDate);

    List<StatDto> statResponseList = new ArrayList<>();

    for (ExecutionState executionState : executionStateList) {
      statResponseList.add(new StatDto(executionState));
    }

    return statResponseList;
  }

  /**
   * 小时维度的查询状态信息
   *
   * @param operator
   * @param projectName
   * @param day
   * @return
   */
  public List<StatDto> queryStatesHour(User operator, String projectName, long day) {
    Date date = new Date(day);
    // 查看是否对项目具备相应的权限
    Project project = projectService.existProjectName(projectName);

    projectService.hasExecPerm(operator, project);

    List<ExecutionState> executionStateList = executionFlowMapper.selectStateHourByProject(project.getId(), date);

    List<StatDto> statResponseList = new ArrayList<>();

    for (ExecutionState executionState : executionStateList) {
      statResponseList.add(new StatDto(executionState));
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
   */
  public List<ExecutionFlow> queryConsumes(User operator, String projectName, long date, int num) {

    Date datetime = new Date(date);

    Project project = projectService.existProjectName(projectName);
    //必须要有project 执行权限
    projectService.hasExecPerm(operator, project);
    return executionFlowMapper.selectDurationsByProject(project.getId(), num, datetime);
  }

  /**
   * 返回错误的排行信息
   *
   * @param operator
   * @param projectName
   * @param date
   * @param num
   */
  public List<ExecutionFlowError> queryErrors(User operator, String projectName, long date, int num) {
    Date datetime = new Date(date);

    Project project = projectService.existProjectName(projectName);
    //必须有project执行权限
    projectService.hasExecPerm(operator, project);
    return executionFlowMapper.selectErrorsByProject(project.getId(), num, datetime);
  }
}
