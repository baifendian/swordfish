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
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.mapper.ExecutionFlowMapper;
import com.baifendian.swordfish.dao.mapper.ExecutionNodeMapper;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.rpc.ExecInfo;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.RetResultInfo;
import com.baifendian.swordfish.rpc.ScheduleInfo;
import com.baifendian.swordfish.rpc.client.MasterClient;
import com.baifendian.swordfish.webserver.dto.ExecutorIds;
import com.baifendian.swordfish.webserver.dto.LogResult;
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
  private LogHelper logHelper;

  @Autowired
  private FlowDao flowDao;

  public List<Integer> postExecWorkflow(User operator, String projectName, String workflowName, String schedule, ExecType execType, String nodeName, DepPolicyType nodeDep, NotifyType notifyType, String notifyMails, int timeout, HttpServletResponse response){

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to create project flow", operator.getName(), projectName);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      logger.error("User {} has no exist workflow {} for the project {} to exec workflow", operator.getName(), workflowName, project.getName());
      return null;
    }

    // 查看 master 是否存在
    MasterServer masterServer = masterServerMapper.query();
    if (masterServer == null) {
      logger.error("Master server does not exist.");
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    List<String> notifyMailList = new ArrayList<>();
    try{
      notifyMailList = JsonUtil.parseObjectList(notifyMails,String.class);
    }catch (Exception e) {
      logger.error("notify mail list des11n error",e);
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    //链接execServer
    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());



    try {
      logger.info("Call master client exec workflow , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());

      switch (execType){
        case DIRECT:{
          ExecInfo execInfo = new ExecInfo(nodeName,nodeDep.getType(),notifyType.getType(),notifyMailList,timeout);
          RetResultInfo retInfo = masterClient.execFlow(project.getId(), projectFlow.getId(), new Date().getTime(),execInfo);
          if (retInfo == null || retInfo.getRetInfo().getStatus() != 0){
            response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
            logger.error("Call master client exec workflow false , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
            return null;
          }
          return retInfo.getExecIds();
        }
        case COMPLEMENT_DATA:{
          ScheduleInfo scheduleInfo = null;
          try {
            scheduleInfo = JsonUtil.parseObject(schedule, ScheduleInfo.class);
          }catch (Exception e){
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            logger.error("scheduleInfo des11n error",e);
            return null;
          }

          RetResultInfo retInfo = masterClient.appendWorkFlow(project.getId(),projectFlow.getId(),scheduleInfo);
          if (retInfo == null || retInfo.getRetInfo().getStatus() != 0){
            response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
            logger.error("Call master client append workflow data false , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
            return null;
          }
          return retInfo.getExecIds();
        }
        default:{
          logger.error("exec workflow no support exec type {}",execType.getType());
        }
      }


    } catch (Exception e) {
      response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
      logger.error("Call master client exec workflow error", e);
      return null;
    }
  }

  /**
   * 查询任务运行情况
   * @return
   */
  public List<ExecutionFlow> getExecWorkflow(User operator, String projectName, String workflowName, Date startDate, Date endDate, String status,int from,int size, HttpServletResponse response){

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to get exec project flow", operator.getName(), projectName);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), workflowName);

    if (projectFlow == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      logger.error("User {} has no exist workflow {} for the project {} to get exec workflow", operator.getName(), workflowName, project.getName());
      return null;
    }

    List<FlowStatus> flowStatusList;
    try{
      flowStatusList = JsonUtil.parseObjectList(status,FlowStatus.class);
    }catch (Exception e){
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      logger.error("flow status list des11n error",e);
      return null;
    }

    return executionFlowMapper.selectByFlowIdAndTimesAndStatusLimit(projectFlow.getId(),startDate,endDate,from,size,flowStatusList);
  }

  /**
   * 查询具体某个任务的运行情况
   * @param operator
   * @param execId
   * @param response
   * @return
   */
  public ExecutionFlow getExecWorkflow(User operator, int execId, HttpServletResponse response){

    ExecutionFlow executionFlow = executionFlowMapper.selectByExecId(execId);

    if (executionFlow == null){
      logger.error("exec flow does not exist: {}", execId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return null;
    }

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(executionFlow.getProjectName());

    if (project == null) {
      logger.error("Project does not exist: {}", executionFlow.getProjectName());
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to get exec project flow", operator.getName(), project.getName());
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    return executionFlow;
  }

  /**
   * 查询日志信息
   * @param operator
   * @param jobId
   * @param from
   * @param size
   * @return
   */
  public LogResult getEexcWorkflowLog(User operator,String jobId,int from,int size, HttpServletResponse response){
    ExecutionNode executionNode = executionNodeMapper.selectExecNodeByJobId(jobId);

    if (executionNode == null){
      logger.error("job id does not exist: {}", jobId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return null;
    }

    ExecutionFlow executionFlow = executionFlowMapper.selectByExecId(executionNode.getExecId());

    if (executionFlow == null){
      logger.error("execution flow does not exist: {}", executionNode.getExecId());
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return null;
    }

    Project project = projectMapper.queryByName(executionFlow.getProjectName());

    if (project == null){
      logger.error("project does not exist: {}", executionFlow.getProjectName());
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to get exec project flow log", operator.getName(), project.getName());
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    return logHelper.getLog(from, size, jobId);
  }

  /**
   * 停止运行
   * @param operator
   * @param execId
   * @param response
   */
  public void postKillWorkflow(User operator,int execId, HttpServletResponse response){
    ExecutionFlow executionFlow = executionFlowMapper.selectByExecId(execId);

    if (executionFlow == null){
      logger.error("exec flow does not exist: {}", execId);
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return;
    }

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(executionFlow.getProjectName());

    if (project == null) {
      logger.error("Project does not exist: {}", executionFlow.getProjectName());
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      return;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to get exec project flow", operator.getName(), project.getName());
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return;
    }

    MasterServer masterServer = masterServerMapper.query();
    if (masterServer == null) {
      logger.error("Master server does not exist.");
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());
    try {
      logger.info("Call master client kill workflow , project id: {}, flow id: {},host: {}, port: {}", project.getId(), executionFlow.getFlowName(), masterServer.getHost(), masterServer.getPort());
      if(!masterClient.cancelExecFlow(execId)){
        logger.error("Call master client kill workflow false , project id: {}, exec flow id: {},host: {}, port: {}", project.getId(), execId, masterServer.getHost(), masterServer.getPort());
        response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
      }
    } catch (Exception e) {
      response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
      logger.error("Call master client set schedule error", e);
    }

  }
}