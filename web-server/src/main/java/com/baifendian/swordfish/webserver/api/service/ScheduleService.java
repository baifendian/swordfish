/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.webserver.api.service;

import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.NodeType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.ScheduleMapper;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.dao.model.User;
import org.apache.commons.httpclient.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
public class ScheduleService {

  private static Logger logger = LoggerFactory.getLogger(ScheduleService.class.getName());

  @Autowired
  private ScheduleMapper scheduleMapper;

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private FlowDao flowDao;

  /**
   * 创建一个调度
   * @param operator
   * @param projectName
   * @param workflowName
   * @param schedule
   * @param notifyType
   * @param maxTryTimes
   * @param failurePolicy
   * @param depWorkflows
   * @param depPolicyType
   * @param timeout
   * @param response
   * @return
   */
  public Schedule createSchedule(User operator, String projectName, String workflowName, String schedule, String, NotifyType notifyType, String notifyMails, int maxTryTimes, FailurePolicyType failurePolicy, String  depWorkflows, DepPolicyType depPolicyType, int timeout, HttpServletResponse response){
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    //检查是否存在工作流
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(),workflowName);

    if (projectFlow == null){
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    Schedule scheduleObj = new Schedule();
    Date now = new Date();

    scheduleObj.setProjectName(projectName);
    scheduleObj.setFlowId(projectFlow.getId());
    scheduleObj.setFlowName(projectFlow.getName());

    try {
      ObjectMapper mapper = new ObjectMapper();
      Schedule.ScheduleParam scheduleParam = mapper.readValue(schedule,Schedule.ScheduleParam.class);
      scheduleObj.setStartDate(scheduleParam.getStatDate());
      scheduleObj.setEndDate(scheduleParam.getEndDate());
      scheduleObj.setCrontabStr(scheduleParam.getCrontab());
      scheduleObj.setSchedule(scheduleParam);
      scheduleObj.setNotifyType(notifyType);
      scheduleObj.setNotifyMailsStr(notifyMails);
      scheduleObj.setMaxTryTimes(maxTryTimes);
      scheduleObj.setFailurePolicy(failurePolicy);
      scheduleObj.setDepWorkflowsStr(depWorkflows);
      scheduleObj.setDepPolicy(depPolicyType);
      scheduleObj.setTimeout(timeout);
      scheduleObj.setCreateTime(now);
      scheduleObj.setModifyTime(now);
      scheduleObj.setOwnerId(operator.getId());
      scheduleObj.setOwner(operator.getName());
      scheduleObj.setLastModifyById(operator.getId());
      scheduleObj.setLastModifyBy(operator.getName());
    }catch (Exception e){
      logger.error(e.toString());
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    try {
      scheduleMapper.insert(scheduleObj);
    } catch (DuplicateKeyException e) {
      logger.error("schedule has exist, can't create again.", e);
      response.setStatus(HttpStatus.SC_CONFLICT);
      return null;
    }

    return scheduleObj;
  }

  /**
   * 修改一个调度
   * @param operator
   * @param projectName
   * @param workflowName
   * @param schedule
   * @param notifyType
   * @param notifyMails
   * @param maxTryTimes
   * @param failurePolicy
   * @param depWorkflows
   * @param depPolicyType
   * @param timeout
   * @param response
   * @return
   */
  public Schedule modifySchedule(User operator, String projectName, String workflowName, String schedule, String, NotifyType notifyType, String notifyMails, int maxTryTimes, FailurePolicyType failurePolicy, String  depWorkflows, DepPolicyType depPolicyType, int timeout, HttpServletResponse response){
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    //检查是否存在工作流
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(),workflowName);

    if (projectFlow == null){
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }


  }

}
