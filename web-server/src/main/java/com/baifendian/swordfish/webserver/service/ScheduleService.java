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
import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.enums.ScheduleStatus;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.ScheduleMapper;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.rpc.client.MasterClient;
import com.baifendian.swordfish.webserver.dto.ScheduleParam;
import com.baifendian.swordfish.webserver.exception.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.baifendian.swordfish.dao.enums.ScheduleStatus.OFFLINE;

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
   * @param notifyMails
   * @param maxTryTimes
   * @param failurePolicy
   * @param depWorkflows
   * @param depPolicyType
   * @param timeout
   * @return
   */
  @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
  public Schedule createSchedule(User operator, String projectName, String workflowName, String schedule, NotifyType notifyType, String notifyMails, int maxTryTimes, FailurePolicyType failurePolicy, String depWorkflows, DepPolicyType depPolicyType, int timeout) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 执行权限
    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" exec permission", operator.getName(), project.getName());
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      logger.error("Not found project flow {} in project {}", workflowName, project.getName());
      throw new NotFoundException("Not found project flow \"{0}\" in project \"{1}\"", workflowName, project.getName());
    }

    Schedule scheduleObj = new Schedule();
    Date now = new Date();

    scheduleObj.setProjectName(projectName);
    scheduleObj.setFlowId(projectFlow.getId());
    scheduleObj.setFlowName(projectFlow.getName());

    try {
      ScheduleParam scheduleParam = JsonUtil.parseObject(schedule, ScheduleParam.class);
      scheduleObj.setStartDate(scheduleParam.getStartDate());
      scheduleObj.setEndDate(scheduleParam.getEndDate());
      scheduleObj.setCrontab(scheduleParam.getCrontab());
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
      throw new BadRequestException("create schedule param object error");
    }

    try {
      scheduleMapper.insert(scheduleObj);
    } catch (DuplicateKeyException e) {
      logger.error("schedule has exist, can't create again.", e);
      throw new ServerErrorException("schedule has exist, can't create again.");
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
   * @param scheduleStatus
   * @return
   */
  @Transactional(value = "TransactionManager", rollbackFor = Exception.class)
  public Schedule patchSchedule(User operator, String projectName, String workflowName, String schedule, NotifyType notifyType, String notifyMails, Integer maxTryTimes, FailurePolicyType failurePolicy, String depWorkflows, DepPolicyType depPolicyType, Integer timeout, ScheduleStatus scheduleStatus) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 执行权限
    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" exec permission", operator.getName(), project.getName());
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      logger.error("Not found project flow {} in project {}", workflowName, project.getName());
      throw new NotFoundException("Not found project flow \"{0}\" in project \"{1}\"", workflowName, project.getName());
    }

    // 检查调度是否存在
    Schedule scheduleObj = scheduleMapper.selectByFlowId(projectFlow.getId());

    if (scheduleObj == null) {
      logger.error("Not found schedule for workflow {}", projectFlow.getId());
      throw new NotFoundException("Not found schedule for workflow \"{0}\"", projectFlow.getId());
    }

    // 上线状态的调度禁止修改
    if (scheduleObj.getScheduleStatus() == ScheduleStatus.ONLINE) {
      logger.error("Can not modify online schedule");
      throw new PreFailedException("ProjectFlow \"{0}\" schedule is online can not modify!", projectFlow.getName());
    }

    Date now = new Date();

    // 封装检查更新参数
    try {
      if (StringUtils.isNotEmpty(schedule)) {
        ScheduleParam scheduleParam = JsonUtil.parseObject(schedule, ScheduleParam.class);
        scheduleObj.setStartDate(scheduleParam.getStartDate());
        scheduleObj.setEndDate(scheduleParam.getEndDate());
        scheduleObj.setCrontab(scheduleParam.getCrontab());
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
      throw new ParameterException("create schedule param object error", e);
    }

    scheduleMapper.update(scheduleObj);

    return scheduleObj;
  }

  /**
   * 创建并修改一个工作流
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
   * @return
   */
  public Schedule putSchedule(User operator, String projectName, String workflowName, String schedule, NotifyType notifyType, String notifyMails, Integer maxTryTimes, FailurePolicyType failurePolicy, String depWorkflows, DepPolicyType depPolicyType, Integer timeout) {
    Schedule scheduleObj = scheduleMapper.selectByFlowName(projectName, workflowName);

    if (scheduleObj == null) {
      return createSchedule(operator, projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout);
    }

    return patchSchedule(operator, projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout, null);
  }


  /**
   * 设置一个调度的上下线
   *
   * @param operator
   * @param projectName
   * @param workflowName
   * @param scheduleStatus
   * @throws Exception
   */
  @Transactional(value = "TransactionManager")
  public void postScheduleStatus(User operator, String projectName, String workflowName, ScheduleStatus scheduleStatus) throws Exception {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 执行权限
    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" exec permission", operator.getName(), project.getName());
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      logger.error("Not found project flow {} in project {}", workflowName, project.getName());
      throw new NotFoundException("Not found project flow \"{0}\" in project \"{1}\"", workflowName, project.getName());
    }

    // 查看 master 是否存在
    MasterServer masterServer = masterServerMapper.query();
    if (masterServer == null) {
      logger.error("Master server does not exist.");
      throw new ServerErrorException("Master server does not exist.");
    }

    // 检查调度是否存在
    Schedule scheduleObj = scheduleMapper.selectByFlowId(projectFlow.getId());

    if (scheduleObj == null) {
      logger.error("Not found schedule for workflow {}", projectFlow.getId());
      throw new NotFoundException("Not found schedule for workflow \"{0}\"", projectFlow.getId());
    }

    // 设置状态
    scheduleObj.setScheduleStatus(scheduleStatus);

    scheduleMapper.update(scheduleObj);

    // 链接 execServer
    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());

    try {
      switch (scheduleStatus) {
        case ONLINE: {
          logger.info("Call master client set schedule online , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());

          if (!masterClient.setSchedule(project.getId(), projectFlow.getId())) {
            logger.error("Call master client set schedule online false , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
            throw new ServerErrorException("Call master client set schedule online false , project id: \"{0}\", flow id: \"{1}\",host: \"{2}\", port: \"{3}\"", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
          }

          break;
        }
        case OFFLINE: {
          logger.info("Call master client set schedule offline , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());

          if (!masterClient.deleteSchedule(project.getId(), projectFlow.getId())) {
            logger.error("Call master client set schedule offline false , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
            throw new ServerErrorException("Call master client set schedule offline false , project id: \"{0}\", flow id: \"{1}\",host: \"{2}\", port: \"{3}\"", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
          }

          break;
        }
        default: {
          logger.error("unknown schedule status {}", scheduleStatus.toString());
          throw new ParameterException("Schedule status \"{0}\" not valid", scheduleStatus);
        }
      }
    } catch (Exception e) {
      logger.error("Call master client set schedule error", e);
      throw e;
    }
  }

  /**
   * 查询一个工作流的调度
   *
   * @param operator
   * @param projectName
   * @param workflowName
   * @return
   */
  public Schedule querySchedule(User operator, String projectName, String workflowName) {

    Project project = projectMapper.queryByName(projectName);
    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 读权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      logger.error("Not found project flow {} in project {}", workflowName, project.getName());
      throw new NotFoundException("Not found project flow \"{0}\" in project \"{1}\"", workflowName, project.getName());
    }

    return scheduleMapper.selectByFlowId(projectFlow.getId());
  }

  /**
   * 根据项目查询调度
   *
   * @param operator
   * @param projectName
   * @return
   */
  public List<Schedule> queryAllSchedule(User operator, String projectName) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 读权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    return scheduleMapper.selectByProject(projectName);
  }
}