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

import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.*;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.ScheduleMapper;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.rpc.client.MasterClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static com.baifendian.swordfish.dao.enums.ScheduleStatus.*;

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

  @Autowired
  private MasterServerMapper masterServerMapper;

  /**
   * 创建一个调度
   *
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
  @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
  public Schedule createSchedule(User operator, String projectName, String workflowName, String schedule, NotifyType notifyType, String notifyMails, int maxTryTimes, FailurePolicyType failurePolicy, String depWorkflows, DepPolicyType depPolicyType, int timeout, HttpServletResponse response){
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      logger.error("User {} has no right permission for the project {} to post schedule",operator.getName(),project.getName());
      return null;
    }

    //检查是否存在工作流
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      logger.error("User {} has no exist workflow {} for the project {} to post schedule",operator.getName(),workflowName,project.getName());
      return null;
    }

    Schedule scheduleObj = new Schedule();
    Date now = new Date();

    scheduleObj.setProjectName(projectName);
    scheduleObj.setFlowId(projectFlow.getId());
    scheduleObj.setFlowName(projectFlow.getName());

    try {
      Schedule.ScheduleParam scheduleParam = JsonUtil.parseObject(schedule, Schedule.ScheduleParam.class);
      scheduleObj.setStartDate(scheduleParam.getStartDate());
      scheduleObj.setEndDate(scheduleParam.getEndDate());
      scheduleObj.setCrontab(scheduleParam.getCrontab());
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
      scheduleObj.setScheduleStatus(OFFLINE);
    } catch (Exception e) {
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
   *
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
  @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
  public Schedule patchSchedule(User operator, String projectName, String workflowName, String schedule, NotifyType notifyType, String notifyMails, Integer maxTryTimes, FailurePolicyType failurePolicy, String depWorkflows, DepPolicyType depPolicyType, Integer timeout, ScheduleStatus scheduleStatus, HttpServletResponse response) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      logger.error("User {} has no right permission for the project {} to patch schedule",operator.getName(),project.getName());
      return null;
    }

    //检查是否存在工作流
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      logger.error("User {} has no exist workflow {} for the project {} to patch schedule",operator.getName(),workflowName,project.getName());
      return null;
    }

    //检查调度是否存在

    Schedule scheduleObj = scheduleMapper.selectByFlowId(projectFlow.getId());
    Date now = new Date();

    if (scheduleObj == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    //封装检查更新参数
    try {
      if (!StringUtils.isEmpty(schedule)) {
        Schedule.ScheduleParam scheduleParam = JsonUtil.parseObject(schedule, Schedule.ScheduleParam.class);
        scheduleObj.setStartDate(scheduleParam.getStartDate());
        scheduleObj.setEndDate(scheduleParam.getEndDate());
        scheduleObj.setCrontab(scheduleParam.getCrontab());
        scheduleObj.setSchedule(scheduleParam);
      }
      if (notifyType != null) {
        scheduleObj.setNotifyType(notifyType);
      }
      if (!StringUtils.isEmpty(notifyMails)) {
        scheduleObj.setNotifyMailsStr(notifyMails);
      }

      if (maxTryTimes != null) {
        scheduleObj.setMaxTryTimes(maxTryTimes);
      }

      if (failurePolicy != null) {
        scheduleObj.setFailurePolicy(failurePolicy);
      }

      if (!StringUtils.isEmpty(depWorkflows)) {
        scheduleObj.setDepWorkflowsStr(depWorkflows);
      }

      if (depPolicyType != null) {
        scheduleObj.setDepPolicy(depPolicyType);
      }

      if (timeout != null) {
        scheduleObj.setTimeout(timeout);
      }

      if (scheduleStatus != null) {
        scheduleObj.setScheduleStatus(scheduleStatus);
      }
      scheduleObj.setModifyTime(now);
    } catch (Exception e) {
      logger.error(e.toString());
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    scheduleMapper.update(scheduleObj);

    return scheduleObj;
  }

  /**
   * 创建并修改一个工作流
   *
   * @return
   */
  public Schedule putSchedule(User operator, String projectName, String workflowName, String schedule, NotifyType notifyType, String notifyMails, Integer maxTryTimes, FailurePolicyType failurePolicy, String depWorkflows, DepPolicyType depPolicyType, Integer timeout, HttpServletResponse response){
    Schedule scheduleObj = scheduleMapper.selectByFlowName(projectName, workflowName);
    if (scheduleObj == null) {
      return createSchedule(operator, projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout, response);
    } else {
      return patchSchedule(operator, projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout, null, response);
    }
  }

  /**
   * 设置一个调度的上下线
   */
  public void postScheduleStatus(User operator,String projectName,String workflowName,ScheduleStatus scheduleStatus,HttpServletResponse response) throws Exception{
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      logger.error("User {} has no right permission for the project {} to post schedule status",operator.getName(),project.getName());
      return;
    }

    //检查是否存在工作流
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      logger.error("User {} has no exist workflow {} for the project {} to post schedule status",operator.getName(),workflowName,project.getName());
      return;
    }

    // 查看 master 是否存在
    MasterServer masterServer = masterServerMapper.query();
    if (masterServer == null) {
      logger.error("Master server does not exist.");
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    //检查调度是否存在

    Schedule scheduleObj = scheduleMapper.selectByFlowId(projectFlow.getId());
    Date now = new Date();

    if (scheduleObj == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    scheduleObj.setScheduleStatus(scheduleStatus);

    scheduleMapper.update(scheduleObj);

    //链接execServer
    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());

    try {

      switch (scheduleStatus){
        case ONLINE:{
          logger.info("Call master client set schedule online , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
          if (!masterClient.setSchedule(project.getId(), projectFlow.getId())) {
            response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
            logger.error("Call master client set schedule online false , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
            throw new Exception("Call master client set schedule offline false");
          }
          break;
        }
        case OFFLINE:{
          logger.info("Call master client set schedule offline , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
          if (!masterClient.deleteSchedule(project.getId(), projectFlow.getId())) {
            response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
            logger.error("Call master client set schedule offline false , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
            throw new Exception("Call master client set schedule offline false");
          }
          break;
        }
        default:{
          response.setStatus(HttpStatus.SC_BAD_REQUEST);
          logger.error("unknown schedule status {}",scheduleStatus.toString());
        }
      }

    } catch (Exception e) {
      response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
      logger.error("Call master client set schedule error", e);
      throw e;
    }

    return;

  }

  /**
   * 查询一个工作流的调度
   *
   * @param operator
   * @param projectName
   * @param workflowName
   * @param response
   * @return
   */
  public Schedule querySchedule(User operator, String projectName, String workflowName, HttpServletResponse response) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      logger.error("User {} has no right permission for the project {} to get schedule",operator.getName(),project.getName());
      return null;
    }

    //检查是否存在工作流
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      return null;
    }

    return scheduleMapper.selectByFlowId(projectFlow.getId());
  }

  /**
   * 根据项目查询调度
   * @param operator
   * @param projectName
   * @param response
   * @return
   */
  public List<Schedule> queryAllSchedule(User operator, String projectName, HttpServletResponse response) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      logger.error("User {} has no right permission for the project {} to get all schedule",operator.getName(),project.getName());
      return null;
    }

    return scheduleMapper.selectByProject(projectName);
  }
}