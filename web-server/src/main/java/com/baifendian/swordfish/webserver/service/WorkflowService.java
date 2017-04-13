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

import com.baifendian.swordfish.common.utils.graph.Graph;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.mapper.FlowNodeMapper;
import com.baifendian.swordfish.dao.mapper.ProjectFlowMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.ResourceMapper;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.dto.NodeParamMR;
import com.baifendian.swordfish.webserver.dto.NodeParamMR.MainJar;
import com.baifendian.swordfish.webserver.dto.enums.MRScope;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

@Service
public class WorkflowService {

  private static Logger logger = LoggerFactory.getLogger(WorkflowService.class.getName());

  @Autowired
  private ProjectFlowMapper projectFlowMapper;

  @Autowired
  private FlowNodeMapper flowNodeMapper;

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private ResourceMapper resourceMapper;

  @Autowired
  private FlowDao flowDao;

  /**
   * 创建一个工作流, 需要具备项目的 'w' 权限。
   *
   * @param operator    操作用户实体
   * @param projectName 工作流所在项目名称
   * @param name        工作流名称
   * @param desc        工作流描述
   * @param proxyUser   工作流执行代理用户名称
   * @param queue       工作流所在队列名称
   * @param data        工作流定义json
   * @param file        工作流定义文件
   * @param response    操作请求response
   * @return 已经创建的工作流实体
   */
  public ProjectFlow createWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, HttpServletResponse response) {

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

    // 对工作流详定义json进行反序列化
    ProjectFlow.ProjectFlowData projectFlowData = projectFlowDataDes(data, file);

    if (projectFlowData == null) {
      logger.error("Project flow data or file not valid");
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    // 检测工作流节点是否正常
    List<FlowNode> flowNodes = projectFlowData.getNodes();

    if (CollectionUtils.isEmpty(flowNodes)) {
      logger.error("flow node information is empty");
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    // 检测工作节点是否存在闭环
    if (graphHasCycle(flowNodes)) {
      logger.error("Proejct flow DAG has cycle");
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    // 检测工作流节点定义json是否正常
    for (FlowNode flowNode : flowNodes) {
      // TODO:: 这个检测不是很合理, 需要修改, 不太完备
      if (!flowNodeParamCheck(flowNode.getParameter(), flowNode.getType())) {
        logger.error("Flow node {} parameter invalid", flowNode.getName());
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        return null;
      }

      //工作流预处理
      if (!preNodeParam(project.getId(),flowNode.getParameter(),flowNode.getType())) {
        logger.error("Flow node {} pre parameter error", flowNode.getName());
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        return null;
      }
    }



    ProjectFlow projectFlow = new ProjectFlow();
    Date now = new Date();

    // 组装新建数据流实体
    try {
      projectFlow.setData(projectFlowData);
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
    } catch (Exception e) {
      logger.error("Project flow set value error", e);
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      return null;
    }

    try {
      flowDao.createProjectFlow(projectFlow);
    } catch (DuplicateKeyException e) {
      logger.error("Workflow has exist, can't create again.", e);
      response.setStatus(HttpStatus.SC_CONFLICT);
      return null;
    } catch (Exception e) {
      logger.error("Workflow create has error", e);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
    }

    return projectFlow;
  }

  /**
   * 覆盖式修改一个工作流，如果不存在就创建。需要具备项目的 'w' 权限。
   * <p>
   * 覆盖式的更新所有可以更新的字段,对于传null的值也会覆盖写入。
   * </p>
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
  public ProjectFlow putWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, HttpServletResponse response) {
    ProjectFlow projectFlow = flowDao.projectFlowFindByPorjectNameAndName(projectName, name);

    if (projectFlow == null) {
      return createWorkflow(operator, projectName, name, desc, proxyUser, queue, data, file, response);
    }

    return patchWorkflow(operator, projectName, name, desc, proxyUser, queue, data, file, response);
  }

  /**
   * 非覆盖式修改一个已经存在的工作流。需要具备项目的 'w' 权限。
   * <p>
   * 非覆盖式的更新所有可以更新的字段，对于传null的值不写入。
   * </p>
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
  public ProjectFlow patchWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, HttpServletResponse response) {

    // 查询项目是否存在以及是否具备相应权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to patch project flow", operator.getName(), projectName);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    // 查询工作流信息
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);
    Date now = new Date();

    if (projectFlow == null) {
      logger.error("Workflow does not exist: {}", name);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    // 解析
    ProjectFlow.ProjectFlowData projectFlowData = projectFlowDataDes(data, file);

    if (projectFlowData != null) {
      if (!StringUtils.isEmpty(projectFlowData.getExtras())) {
        projectFlow.setExtras(projectFlowData.getExtras());
      }

      if (!CollectionUtils.isEmpty(projectFlowData.getUserDefParams())) {
        projectFlow.setUserDefinedParams(JsonUtil.toJsonString(projectFlowData.getUserDefParams()));
      }

      List<FlowNode> flowNodeList = projectFlowData.getNodes();
      if (flowNodeList != null) {
        projectFlow.setFlowsNodes(projectFlowData.getNodes());

        // 闭环检测
        if (graphHasCycle(flowNodeList)) {
          logger.error("Graph has cycle");
          response.setStatus(HttpStatus.SC_BAD_REQUEST);
          return null;
        }

        // parameter 检测
        for (FlowNode flowNode : flowNodeList) {
          // TODO::参数检测存在问题
          if (!flowNodeParamCheck(flowNode.getParameter(), flowNode.getType())) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            return null;
          }

          //工作流预处理
          if (!preNodeParam(project.getId(),flowNode.getParameter(),flowNode.getType())) {
            logger.error("Flow node {} pre parameter error", flowNode.getName());
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            return null;
          }
        }

      }
    }

    if (StringUtils.isNotEmpty(name)) {
      projectFlow.setName(name);
    }

    if (StringUtils.isNotEmpty(desc)) {
      projectFlow.setDesc(desc);
    }

    projectFlow.setModifyTime(now);

    if (StringUtils.isNotEmpty(proxyUser)) {
      projectFlow.setProxyUser(proxyUser);
    }

    if (StringUtils.isNotEmpty(queue)) {
      projectFlow.setQueue(queue);
    }

    projectFlow.setOwnerId(operator.getId());
    projectFlow.setOwner(operator.getName());

    try {
      flowDao.modifyProjectFlow(projectFlow);
    } catch (Exception e) {
      logger.error("Workflow modify has error", e);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

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
  public void deleteProjectFlow(User operator, String projectName, String name, HttpServletResponse response) {

    // 查询项目是否存在以及是否具备相应权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return;
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);

    if (projectFlow == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    projectFlowMapper.deleteByProjectAndName(project.getId(), name);

    // TODO 删除调度，删除日志等

    return;
  }

  /**
   * 修改一个项目下所有工作流的配置
   *
   * @param operator
   * @param projectName
   * @param queue
   * @param proxyUser
   */
  public void modifyWorkflowConf(User operator, String projectName, String queue, String proxyUser, HttpServletResponse response) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return;
    }

    projectFlowMapper.updateProjectConf(project.getId(), queue, proxyUser);
  }

  /**
   * 查询一个项目下所有工作流
   *
   * @param operator
   * @param projectName
   * @param response
   * @return
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

    return flowDao.projectFlowFindByProject(project.getId());

  }

  /**
   * 查询某一个具体工作流的详情
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

    return flowDao.projectFlowfindByName(project.getId(), name);
  }

  /**
   * @param operator
   * @param projectName
   * @param name
   * @param response
   * @return
   */
  public Resource downloadProjectFlowFile(User operator, String projectName, String name, HttpServletResponse response) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);

    String json = "";

    try {
      json = JsonUtil.toJsonString(projectFlow.getData());
    } catch (RuntimeException e) {
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
  private ProjectFlow.ProjectFlowData projectFlowDataDes(String data, MultipartFile file) {
    ProjectFlow.ProjectFlowData projectFlowData = null;

    if (file != null && !file.isEmpty()) {
      // TODO::
      // 先将文件存放到本地

      // 解压

      // 读取 workflow.json 文件
//
//        ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
//        String jsonString = IOUtils.toString(stream, "UTF-8");
      String jsonString = "";
      projectFlowData = JsonUtil.parseObject(jsonString, ProjectFlow.ProjectFlowData.class);
    } else if (data != null) {
      projectFlowData = JsonUtil.parseObject(data, ProjectFlow.ProjectFlowData.class);
    }

    return projectFlowData;
  }

  /**
   * 闭环检测
   *
   * @param flowNodeList
   * @return
   */
  private boolean graphHasCycle(List<FlowNode> flowNodeList) {
    Graph<String, FlowNode, String> graph = new Graph<>();

    // 填充顶点
    for (FlowNode flowNode : flowNodeList) {
      graph.addVertex(flowNode.getName(), flowNode);
    }

    // 填充边关系
    for (FlowNode flowNode : flowNodeList) {
      if (CollectionUtils.isNotEmpty(flowNode.getDepList())) {
        for (String dep : flowNode.getDepList()) {
          graph.addEdge(dep, flowNode.getName());
        }
      }
    }

    return graph.hasCycle();
  }

  /**
   * 检测 flowNode parameter 格式是否正常
   *
   * @param parameter
   * @param type
   * @return
   */
  private boolean flowNodeParamCheck(String parameter, String type) {
    /*ObjectMapper mapper = new ObjectMapper();

    try {
      switch (type) {
        case "MR":
        case "mr":
          mapper.readValue(parameter, NodeParamMR.class);
          break;
        default:
          return false;
      }
    } catch (Exception e) {
      logger.error(e.toString());
      return false;
    }*/
    return true;
  }

  //节点预处理
  private boolean preNodeParam(int projectId,String parameter, String type){
    try{
      switch (type){
        case "MR":
        case "mr":{
          NodeParamMR nodeParamMR = JsonUtil.parseObject(parameter, NodeParamMR.class);
          if (nodeParamMR.getMainJar()!=null && nodeParamMR.getMainJar().getScope() == MRScope.PROJECT){
            addSuffix(nodeParamMR.getMainJar(),projectId);
          }
          if (nodeParamMR.getLibJars()!=null){
            for (NodeParamMR.File file:nodeParamMR.getLibJars()){
              addSuffix(file,projectId);
            }
          }
        }
      }
    }catch (Exception e){
      logger.error("pre node param error",e);
      return false;
    }
    return true;
  }

  /**
   *
   * @param file
   * @param projectId
   * @param <T>
   */
  private <T extends MainJar> void addSuffix(T file,int projectId){
    MainJar mainJar = (MainJar) file;
    com.baifendian.swordfish.dao.model.Resource resource = resourceMapper.queryResourceDetail(projectId,file.getRes());
    if (resource!=null){
      file.setRes(mainJar.getRes()+resource.getSuffix());
    }
  }

}
