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
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.MasterServer;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.rpc.client.MasterClient;
import com.baifendian.swordfish.webserver.dto.ExecutorIds;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

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
  private FlowDao flowDao;

  public ExecutorIds execExistWorkflow(User operator, String projectName, String workflowName, String schedule, ExecType execType, String nodeName, String nodeDep, NotifyType notifyType, String notifyMails, int timeout, HttpServletResponse response) throws Exception {

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

    //链接execServer
    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());

    try {
      logger.info("Call master client set schedule , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
      if (!masterClient.execFlow(project.getId(), projectFlow.getId(), new Date().getTime())) {
        response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
        logger.error("Call master client set schedule false , project id: {}, flow id: {},host: {}, port: {}", project.getId(), projectFlow.getId(), masterServer.getHost(), masterServer.getPort());
        throw new Exception("Call master client set schedule false");
      }
    } catch (Exception e) {
      response.setStatus(HttpStatus.SC_SERVICE_UNAVAILABLE);
      logger.error("Call master client set schedule error", e);
      throw e;
    }


    return null;
  }
}