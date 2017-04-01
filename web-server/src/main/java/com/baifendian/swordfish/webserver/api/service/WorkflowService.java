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
package com.baifendian.swordfish.webserver.api.service;

import com.baifendian.swordfish.common.utils.graph.Graph;
import com.baifendian.swordfish.dao.mapper.FlowNodeMapper;
import com.baifendian.swordfish.dao.mapper.ProjectFlowMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.api.dto.NodeParamMR;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowService {

  @Autowired
  private ProjectFlowMapper projectFlowMapper;

  @Autowired
  private FlowNodeMapper flowNodeMapper;

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectService projectService;

  private static Logger logger = LoggerFactory.getLogger(WorkflowService.class.getName());

  /**
   * 创建一个工作流
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param proxyUser
   * @param queue
   * @param data
   * @param file
   * @param response
   * @return
   */
  @Transactional
  public ProjectFlow createWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, HttpServletResponse response) {

    ProjectFlow.ProjectFlowData projectFlowData = ProjectFlowDataDes11n(data, file);

    if (projectFlowData == null) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    List<FlowNode> flowNodes = projectFlowData.getNodes();

    if (flowNodes!=null){
      //闭环检测未通过
      if (graphCheck(flowNodes)){
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        return null;
      }
      //parameter检测
      for (FlowNode flowNode:flowNodes){
        if(!flowNodeParamCheck(flowNode.getParameter(),flowNode.getType())){
          response.setStatus(HttpStatus.SC_BAD_REQUEST);
          return null;
        }
      }
    }


    ProjectFlow projectFlow = new ProjectFlow();
    Date now = new Date();

    projectFlow.setName(name);
    projectFlow.setProjectId(project.getId());
    projectFlow.setProjectName(projectName);
    projectFlow.setDesc(desc);
    projectFlow.setFlowsNodes(flowNodes);
    projectFlow.setCreateTime(now);
    projectFlow.setModifyTime(now);
    projectFlow.setProxyUser(proxyUser);
    projectFlow.setQueue(queue);
    projectFlow.setOwnerId(operator.getId());
    projectFlow.setOwner(operator.getName());
    projectFlow.setUserDefinedParams(projectFlowData.getUserDefParams());
    projectFlow.setExtras(projectFlowData.getExtras());

    try {
      projectFlowMapper.insertAndGetId(projectFlow);
    } catch (DuplicateKeyException e) {
      logger.error("Workflow has exist, can't create again.", e);
      response.setStatus(HttpStatus.SC_CONFLICT);
      return null;
    }


    if (flowNodes != null) {
      for (FlowNode flowNode : flowNodes) {
        flowNode.setFlowId(projectFlow.getId());
        flowNodeMapper.insert(flowNode);
      }
    }

    return projectFlow;
  }

  /**
   * 修改工作流，如果不存在就创建。
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param proxyUser
   * @param queue
   * @param data
   * @param file
   * @param response
   * @return
   */
  public ProjectFlow putWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, HttpServletResponse response){
    ProjectFlow projectFlow = projectFlowMapper.findByProjectNameAndName(projectName, name);
    if (projectFlow == null){
      return createWorkflow(operator,projectName,name,desc,proxyUser,queue,data,file,response);
    } else {
      return patchWorkflow(operator,projectName,name,desc,proxyUser,queue,data,file,response);
    }
  }

  /**
   * 修改工作流
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param proxyUser
   * @param queue
   * @param data
   * @param file
   * @param response
   * @return
   */
  @Transactional
  public ProjectFlow patchWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, HttpServletResponse response) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    ProjectFlow projectFlow = projectFlowMapper.findByName(project.getId(), name);
    Date now = new Date();

    if (projectFlow == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    ProjectFlow.ProjectFlowData projectFlowData = ProjectFlowDataDes11n(data, file);

    if (projectFlowData != null) {
      if (!projectFlow.getExtras().isEmpty()) {
        projectFlow.setExtras(projectFlow.getExtras());
      }
      if (!projectFlow.getUserDefinedParams().isEmpty()) {
        projectFlow.setUserDefinedParams(projectFlow.getUserDefinedParams());
      }
      List<FlowNode> flowNodeList = projectFlowData.getNodes();
      if (flowNodeList != null) {
        projectFlow.setFlowsNodes(projectFlowData.getNodes());
        flowNodeMapper.deleteByFlowId(projectFlow.getId());

        //闭环检测
        if (!graphCheck(flowNodeList)){
          response.setStatus(HttpStatus.SC_BAD_REQUEST);
          return null;
        }
        //parameter检测
        for (FlowNode flowNode:flowNodeList){
          if(!flowNodeParamCheck(flowNode.getParameter(),flowNode.getType())){
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            return null;
          }
        }

        for (FlowNode flowNode : flowNodeList) {
          flowNode.setFlowId(projectFlow.getId());
          flowNodeMapper.insert(flowNode);
        }
      }

    }

    if (!name.isEmpty()) {
      projectFlow.setName(name);
    }
    if (!desc.isEmpty()) {
      projectFlow.setDesc(desc);
    }
    projectFlow.setModifyTime(now);
    if (!proxyUser.isEmpty()) {
      projectFlow.setProxyUser(proxyUser);
    }
    if (!queue.isEmpty()) {
      projectFlow.setQueue(queue);
    }

    projectFlowMapper.updateByName(projectFlow);

    return projectFlow;

  }

  /**
   * 删除一个工作流
   *
   * @param operator
   * @param projectName
   * @param name
   * @param response
   */
  @Transactional
  public void deleteProjectFlow(User operator, String projectName, String name, HttpServletResponse response) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return;
    }

    ProjectFlow projectFlow = projectFlowMapper.findByName(project.getId(), name);

    if (projectFlow == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    projectFlowMapper.deleteByProjectAndName(project.getId(), name);
    flowNodeMapper.deleteByFlowId(projectFlow.getId());

    return;
  }

  /**
   * 查询一个项目下所有工作流
   *
   * @param operator
   */
  public List<ProjectFlow> queryAllProjectFlow(User operator, String projectName, HttpServletResponse response) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    return projectFlowMapper.findByProject(project.getId());

  }

  /**
   * 查询某一个具体工作流
   *
   * @param operator
   * @param projectName
   * @param name
   * @param response
   * @return
   */
  public ProjectFlow queryProjectFlow(User operator, String projectName, String name, HttpServletResponse response) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    return projectFlowMapper.findByName(project.getId(), name);

  }

  public Resource queryProjectFlowFile(User operator, String projectName, String name, HttpServletResponse response){
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    ProjectFlow projectFlow = projectFlowMapper.findByName(project.getId(), name);

    ObjectMapper mapper = new ObjectMapper();

    String json = "";

    try {
      json = mapper.writeValueAsString(projectFlow.getData());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }

    try {
      InputStreamResource resource = new InputStreamResource(new FileInputStream(json));
      return resource;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * project flow data 反序列化
   *
   * @param data
   * @param file
   * @return
   */
  public ProjectFlow.ProjectFlowData ProjectFlowDataDes11n(String data, MultipartFile file) {
    ProjectFlow.ProjectFlowData projectFlowData = null;
    ObjectMapper mapper = new ObjectMapper();
    try {
      projectFlowData = mapper.readValue(data, ProjectFlow.ProjectFlowData.class);
    } catch (Exception e) {
      logger.error(e.toString());
    }

    if (file != null && !file.isEmpty()) {
      try {
        ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
        String jsonString = IOUtils.toString(stream, "UTF-8");
        projectFlowData = mapper.readValue(jsonString, ProjectFlow.ProjectFlowData.class);
      } catch (Exception e) {
        logger.error(e.toString());
      }
    }
    return projectFlowData;
  }

  /**
   * 闭环检测
   * @param flowNodeList
   * @return
   */
  public boolean graphCheck(List<FlowNode> flowNodeList){
    Graph<Integer, String,String> graph = new Graph<>();

    //缓存边关系
    Map<String,Integer> flowNodeIdMap = new HashMap<>();

    //填充顶点
    int index = 0;
    for (FlowNode flowNode:flowNodeList){
      graph.addVertex(index,flowNode.getName());
      flowNodeIdMap.put(flowNode.getName(),index++);
    }

    //填充边关系
    index = 0;
    for (FlowNode flowNode:flowNodeList){
      for(String dep:flowNode.getDepList()){
        graph.addEdge(flowNodeIdMap.get(dep),index);
      }
    }

    return graph.hasCycle();
  }

  /**
   * 检测flowNode parameter格式是否正常
   * @return
   */
  public boolean flowNodeParamCheck(String parameter, String type){
    ObjectMapper mapper = new ObjectMapper();

    try {
      switch (type) {
        case "MR":
        case "mr":
          mapper.readValue(parameter, NodeParamMR.class);
          break;
        default:return false;
      }
    }catch (Exception e){
      logger.error(e.toString());
      return false;
    }
    return true;
  }
}
