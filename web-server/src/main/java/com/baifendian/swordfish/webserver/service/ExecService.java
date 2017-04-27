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
import com.baifendian.swordfish.dao.mapper.ExecutionFlowMapper;
import com.baifendian.swordfish.dao.mapper.ExecutionNodeMapper;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.rpc.ExecInfo;
import com.baifendian.swordfish.rpc.RetResultInfo;
import com.baifendian.swordfish.rpc.ScheduleInfo;
import com.baifendian.swordfish.rpc.client.MasterClient;
import com.baifendian.swordfish.webserver.dto.ExecutorId;
import com.baifendian.swordfish.webserver.dto.ExecutorIds;
import com.baifendian.swordfish.webserver.dto.ExecWorkflowsDto;
import com.baifendian.swordfish.webserver.dto.LogResult;
import com.baifendian.swordfish.webserver.dto.ExecutionFlowDto;
import com.baifendian.swordfish.webserver.dto.ExecutionNodeDto;
import com.baifendian.swordfish.webserver.dto.WorkflowDto;
import com.baifendian.swordfish.webserver.exception.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExecService {

  private static Logger logger = LoggerFactory.getLogger(ExecService.class.getName());

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private MasterServerMapper masterServerMapper;

  @Autowired
  private ExecutionFlowMapper executionFlowMapper;

  @Autowired
  private ExecutionNodeMapper executionNodeMapper;

  @Autowired
  private WorkflowService workflowService;

  @Autowired
  private LogHelper logHelper;

  @Autowired
  private FlowDao flowDao;

  public ExecutorIds postExecWorkflow(User operator, String projectName, String workflowName, String schedule, ExecType execType, String nodeName, NodeDepType nodeDep, NotifyType notifyType, String notifyMails, int timeout) {

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("project",projectName);
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to create project flow", operator.getName(), projectName);
      throw new PermissionException("project exec or project owner",operator.getName());
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      logger.error("User {} has no exist workflow {} for the project {} to exec workflow", operator.getName(), workflowName, project.getName());
      throw new NotFoundException("workflow",workflowName);
    }

    // 查看 master 是否存在
    MasterServer masterServer = masterServerMapper.query();
    if (masterServer == null) {
      logger.error("Master server does not exist.");
      throw new ServerErrorException("Master server does not exist.");
    }

    List<String> notifyMailList = new ArrayList<>();
    try {
      notifyMailList = JsonUtil.parseObjectList(notifyMails, String.class);
    } catch (Exception e) {
      logger.error("notify mail list des11n error", e);
      throw new ParameterException("notifyMail");
    }

    //链接execServer
    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());

    try {
      logger.info("Call master client exec workflow , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());

      switch (execType) {
        case DIRECT: {
          ExecInfo execInfo = new ExecInfo(nodeName, nodeDep!=null?nodeDep.getType():0, notifyType!=null?notifyType.getType():0, notifyMailList, timeout);
          RetResultInfo retInfo = masterClient.execFlow(project.getId(), projectFlow.getId(), new Date().getTime(), execInfo);
          if (retInfo == null || retInfo.getRetInfo().getStatus() != 0) {
            logger.error("Call master client exec workflow false , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
            throw new ServerErrorException("master server return error");
          }
          return new ExecutorIds(retInfo.getExecIds());
        }
        case COMPLEMENT_DATA: {
          ScheduleInfo scheduleInfo = null;
          try {
            scheduleInfo = JsonUtil.parseObject(schedule, ScheduleInfo.class);
          } catch (Exception e) {
            logger.error("scheduleInfo des11n error", e);
            throw new ParameterException("scheduleInfo");
          }

          RetResultInfo retInfo = masterClient.appendWorkFlow(project.getId(), projectFlow.getId(), scheduleInfo);
          if (retInfo == null || retInfo.getRetInfo().getStatus() != 0) {
            logger.error("Call master client append workflow data false , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
            throw new ServerErrorException("Call master client append workflow data false , project id: {}, flow id: {},host: {}, port: {}",project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
          }
          return new ExecutorIds(retInfo.getExecIds());
        }
        default: {
          logger.error("exec workflow no support exec type {}", execType.getType());
          throw new ParameterException("execType");
        }
      }


    } catch (Exception e) {
      logger.error("Call master client exec workflow error", e);
      throw e;
    }
  }

  /**
   * 直接执行一个工作流
   * @param operator
   * @param projectName
   * @param workflowName
   * @param proxyUser
   * @param queue
   * @param data
   * @param file
   * @param notifyType
   * @param notifyMails
   * @param timeout
   * @param extras
   * @return
   */
  public ExecutorId postExecWorkflowDirect(User operator, String projectName, String workflowName, String desc, String proxyUser, String queue, String data, MultipartFile file, NotifyType notifyType, String notifyMails, int timeout, String extras){
    logger.info("step1. create temp workflow");
    ProjectFlow projectFlow = workflowService.createWorkflow(operator, projectName, workflowName, desc, proxyUser, queue, data, file, extras, 1);
    if (projectFlow == null){
      throw new ServerErrorException("project workflow create return is null");
    }
    logger.info("step2. exec temp workflow");
    ExecutorIds executorIds = postExecWorkflow(operator,projectName,workflowName,null,ExecType.DIRECT,null,null,notifyType,notifyMails,timeout);
    if (CollectionUtils.isEmpty(executorIds.getExecIds())){
      throw new ServerErrorException("project workflow exec return is null");
    }
    return new ExecutorId(executorIds.getExecIds().get(0));
  }

  /**
   * 查询任务运行情况
   *
   * @return
   */
  public ExecWorkflowsDto getExecWorkflow(User operator, String projectName, String workflowName, Date startDate, Date endDate, String status, int from, int size) {

    List<String> workflowList;

    if (from < 0){
      throw new BadRequestException("from");
    }

    try{
      workflowList = JsonUtil.parseObjectList(workflowName,String.class);
    }catch (Exception e){
      logger.error("des11n workflow list error",e);
      throw new ParameterException("workflowName");
    }

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("project",projectName);
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to get exec project flow", operator.getName(), projectName);
      throw new PermissionException("project exec or project owner",operator.getName());
    }

    List<FlowStatus> flowStatusList;
    try {
      flowStatusList = JsonUtil.parseObjectList(status, FlowStatus.class);
    } catch (Exception e) {
      logger.error("flow status list des11n error", e);
      throw new ParameterException("status");
    }

    List<ExecutionFlow> executionFlowList = executionFlowMapper.selectByFlowIdAndTimesAndStatusLimit(projectName,workflowList, startDate, endDate, from, size, flowStatusList);
    List<ExecutionFlowDto> executionFlowResponseList = new ArrayList<>();
    for (ExecutionFlow executionFlow:executionFlowList){
      executionFlowResponseList.add(new ExecutionFlowDto(executionFlow));
    }

    int total = executionFlowMapper.sumByFlowIdAndTimesAndStatus(projectName,workflowList, startDate, endDate,  flowStatusList);
    return new ExecWorkflowsDto(total,size,executionFlowResponseList);
  }

  /**
   * 查询具体某个任务的运行情况
   *
   * @param operator
   * @param execId
   * @return
   */
  public ExecutionFlow getExecWorkflow(User operator, int execId) {

    ExecutionFlow executionFlow = executionFlowMapper.selectByExecId(execId);

    if (executionFlow == null) {
      logger.error("exec flow does not exist: {}", execId);
      throw new NotFoundException("execId",String.valueOf(execId));
    }

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(executionFlow.getProjectName());

    if (project == null) {
      logger.error("Project does not exist: {}", executionFlow.getProjectName());
      throw new NotFoundException("project",executionFlow.getProjectName());
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to get exec project flow", operator.getName(), project.getName());
      throw new PermissionException("project exec or project owner",operator.getName());
    }

    ExecutionFlowDto executionFlowResponse = new ExecutionFlowDto(executionFlow);
    List<ExecutionNode> executionNodeList = executionNodeMapper.selectExecNodeById(execId);

    for (ExecutionNodeDto executionNodeResponse:executionFlowResponse.getData().getNodes()){
      for (ExecutionNode executionNode:executionNodeList){
        if (StringUtils.equals(executionNodeResponse.getName(),executionNode.getName())){
          executionNodeResponse.mergeExecutionNode(executionNode);
        }
      }
    }

    return executionFlow;
  }

  /**
   * 查询日志信息
   *
   * @param operator
   * @param jobId
   * @param from
   * @param size
   * @return
   */
  public LogResult getEexcWorkflowLog(User operator, String jobId, int from, int size) {
    ExecutionNode executionNode = executionNodeMapper.selectExecNodeByJobId(jobId);

    if (executionNode == null) {
      logger.error("job id does not exist: {}", jobId);
      throw new NotFoundException("jobId",jobId);
    }

    ExecutionFlow executionFlow = executionFlowMapper.selectByExecId(executionNode.getExecId());

    if (executionFlow == null) {
      logger.error("execution flow does not exist: {}", executionNode.getExecId());
      throw new NotFoundException("execId",String.valueOf(executionNode.getExecId()));
    }

    Project project = projectMapper.queryByName(executionFlow.getProjectName());

    if (project == null) {
      logger.error("project does not exist: {}", executionFlow.getProjectName());
      throw new NotFoundException("project",executionFlow.getProjectName());
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to get exec project flow log", operator.getName(), project.getName());
      throw new PermissionException("project exec or project owner",operator.getName());
    }

    return logHelper.getLog(from, size, jobId);
  }

  /**
   * 停止运行
   *
   * @param operator
   * @param execId
   */
  public void postKillWorkflow(User operator, int execId) {
    ExecutionFlow executionFlow = executionFlowMapper.selectByExecId(execId);

    if (executionFlow == null) {
      logger.error("exec flow does not exist: {}", execId);
      throw new NotFoundException("execId",String.valueOf(execId));
    }

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(executionFlow.getProjectName());

    if (project == null) {
      logger.error("Project does not exist: {}", executionFlow.getProjectName());
      throw new NotFoundException("project",executionFlow.getProjectName());
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to get exec project flow", operator.getName(), project.getName());
      throw new PermissionException("project exec or project owenr",operator.getName());
    }

    MasterServer masterServer = masterServerMapper.query();
    if (masterServer == null) {
      logger.error("Master server does not exist.");
      throw new ServerErrorException("Master server does not exist.");
    }

    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());
    try {
      logger.info("Call master client kill workflow , project id: {}, flow id: {},host: {}, port: {}", project.getId(), executionFlow.getFlowName(), masterServer.getHost(), masterServer.getPort());
      if (!masterClient.cancelExecFlow(execId)) {
        logger.error("Call master client kill workflow false , project id: {}, exec flow id: {},host: {}, port: {}", project.getId(), execId, masterServer.getHost(), masterServer.getPort());
        throw new ServerErrorException("Call master client kill workflow false , project id: {}, exec flow id: {},host: {}, port: {}", project.getId(), execId, masterServer.getHost(), masterServer.getPort());
      }
    } catch (Exception e) {
      logger.error("Call master client set schedule error", e);
      throw e;
    }

  }
}