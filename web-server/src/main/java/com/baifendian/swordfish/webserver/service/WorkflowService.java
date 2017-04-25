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

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.utils.graph.Graph;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.mapper.FlowNodeMapper;
import com.baifendian.swordfish.dao.mapper.ProjectFlowMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.ResourceMapper;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.dto.WorkflowData;
import com.baifendian.swordfish.webserver.dto.response.WorkflowResponse;
import com.baifendian.swordfish.webserver.exception.*;
import com.baifendian.swordfish.webserver.service.storage.FileSystemStorageService;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.lingala.zip4j.core.ZipFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
  private FileSystemStorageService fileSystemStorageService;

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
   * @return 已经创建的工作流实体
   */
  public WorkflowResponse createWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, String extras, Integer flag) {

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("project", projectName);
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to create project flow", operator.getName(), projectName);
      throw new PermissionException("project write or project owenr", operator.getName());
    }

    // 对工作流详定义json进行反序列化
    WorkflowData workflowData = workflowDataDes(data, file, name, project);

    if (workflowData == null) {
      logger.error("Project flow data or file not valid");
      throw new ParameterException("data");
    }

    // 检测工作流节点是否正常
    List<FlowNode> flowNodes = workflowData.getNodes();

    if (CollectionUtils.isEmpty(flowNodes)) {
      logger.error("flow node information is empty");
      throw new ParameterException("data");
    }

    // 检测工作节点是否存在闭环
    if (graphHasCycle(flowNodes)) {
      logger.error("Proejct flow DAG has cycle");
      throw new ParameterException("data");
    }

    // 检测工作流节点定义json是否正常
    for (FlowNode flowNode : flowNodes) {
      // TODO:: 这个检测不是很合理, 需要修改, 不太完备
      if (!flowNodeParamCheck(flowNode.getParameter(), flowNode.getType())) {
        logger.error("Flow node {} parameter invalid", flowNode.getName());
        throw new ParameterException("data");
      }
    }


    ProjectFlow projectFlow = new ProjectFlow();
    Date now = new Date();

    // 组装新建数据流实体
    try {
      projectFlow.setExtras(extras);
      projectFlow.setFlowsNodes(workflowData.getNodes());
      projectFlow.setUserDefinedParamList(workflowData.getUserDefParams());
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
      projectFlow.setFlag(flag);
    } catch (Exception e) {
      logger.error("Project flow set value error", e);
      throw new BadRequestException("Project flow set value error");
    }

    try {
      flowDao.createProjectFlow(projectFlow);
    } catch (DuplicateKeyException e) {
      logger.error("Workflow has exist, can't create again.", e);
      throw new NotModifiedException("Workflow has exist, can't create again.");
    } catch (Exception e) {
      logger.error("Workflow create has error", e);
      throw new ServerErrorException("Workflow create has error");
    }

    return new WorkflowResponse(projectFlow);
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
   * @return
   */
  public WorkflowResponse putWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, String extras) {
    ProjectFlow projectFlow = flowDao.projectFlowFindByPorjectNameAndName(projectName, name);

    if (projectFlow == null) {
      return createWorkflow(operator, projectName, name, desc, proxyUser, queue, data, file, extras, null);
    }

    return patchWorkflow(operator, projectName, name, desc, proxyUser, queue, data, file, extras);
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
   * @return
   */
  public WorkflowResponse patchWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, String extras) {

    // 查询项目是否存在以及是否具备相应权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("project", projectName);
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {} to patch project flow", operator.getName(), projectName);
      throw new PermissionException("project write or project owner", operator.getName());
    }

    // 查询工作流信息
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);
    Date now = new Date();

    if (projectFlow == null) {
      logger.error("Workflow does not exist: {}", name);
      throw new NotFoundException("workflow", name);
    }

    // 解析
    WorkflowData workflowData = workflowDataDes(data, file, name, project);

    if (workflowData != null) {

      if (!CollectionUtils.isEmpty(workflowData.getUserDefParams())) {
        projectFlow.setUserDefinedParamList(workflowData.getUserDefParams());
      }

      List<FlowNode> flowNodeList = workflowData.getNodes();
      if (CollectionUtils.isNotEmpty(flowNodeList)) {
        // 闭环检测
        if (graphHasCycle(flowNodeList)) {
          logger.error("Graph has cycle");
          throw new BadRequestException("Graph has cycle");
        }

        // parameter 检测
        for (FlowNode flowNode : flowNodeList) {
          // TODO::参数检测存在问题
          if (!flowNodeParamCheck(flowNode.getParameter(), flowNode.getType())) {
            throw new BadRequestException("workflow node data not valid");
          }
        }
        projectFlow.setFlowsNodes(flowNodeList);

      }
    }

    if (StringUtils.isNotEmpty(extras)) {
      projectFlow.setExtras(extras);
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
      throw new ServerErrorException("Workflow modify has error");
    }

    return new WorkflowResponse(projectFlow);
  }

  /**
   * 拷贝一个工作流
   *
   * @param operator
   * @param projectName
   * @param srcWorkflowName
   * @param destWorkflowName
   * @return
   */
  public WorkflowResponse postWorkflowCopy(User operator, String projectName, String srcWorkflowName, String destWorkflowName) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      throw new NotFoundException("project", projectName);
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      throw new PermissionException("project write or project owner", operator.getName());
    }

    // 获取数据库信息

    ProjectFlow srcProjectFlow = flowDao.projectFlowfindByName(project.getId(), srcWorkflowName);

    if (srcProjectFlow == null) {
      throw new NotFoundException("workflow", srcWorkflowName);
    }

    String data = JsonUtil.toJsonString(new WorkflowData(srcProjectFlow.getFlowsNodes(),srcProjectFlow.getUserDefinedParamList()));

    // 尝试拷贝文件
    String srcHdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), srcWorkflowName);
    String destHdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), destWorkflowName);

    logger.info("try copy workflow file {} to workflow file {}", srcHdfsFilename, destHdfsFilename);
    try {
      if (HdfsClient.getInstance().exists(srcHdfsFilename)) {
        HdfsClient.getInstance().copy(srcHdfsFilename, destHdfsFilename, false, true);
      } else {
        logger.info("workflow file {} not exists no copy required", srcHdfsFilename);
      }
    } catch (IOException e) {
      logger.error("copy hdfs file error", e);
      throw new ServerErrorException("copy hdfs file error");
    }

    return putWorkflow(operator, projectName, destWorkflowName, srcProjectFlow.getDesc(), srcProjectFlow.getProxyUser(), srcProjectFlow.getQueue(), data, null, srcProjectFlow.getExtras());
  }

  /**
   * 删除一个工作流
   *
   * @param operator
   * @param projectName
   * @param name
   */
  public void deleteProjectFlow(User operator, String projectName, String name) {

    // 查询项目是否存在以及是否具备相应权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      throw new NotFoundException("project", projectName);
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      throw new PermissionException("project write or project owner", operator.getName());
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);

    if (projectFlow == null) {
      throw new NotFoundException("workflow", name);
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
  public void modifyWorkflowConf(User operator, String projectName, String queue, String proxyUser) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      throw new NotFoundException("project", projectName);
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      throw new PermissionException("project write or project owner", operator.getName());
    }

    projectFlowMapper.updateProjectConf(project.getId(), queue, proxyUser);
  }

  /**
   * 查询一个项目下所有工作流
   *
   * @param operator
   * @param projectName
   * @return
   */
  public List<WorkflowResponse> queryAllProjectFlow(User operator, String projectName) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      throw new NotFoundException("project", projectName);
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      throw new PermissionException("project read or project owener", operator.getName());
    }

    List<ProjectFlow> projectFlowList = flowDao.projectFlowFindByProject(project.getId());
    List<WorkflowResponse> workflowResponseList = new ArrayList<>();
    for (ProjectFlow projectFlow:projectFlowList){
      workflowResponseList.add(new WorkflowResponse(projectFlow));
    }
    return workflowResponseList;
  }

  /**
   * 查询某一个具体工作流的详情
   *
   * @param operator
   * @param projectName
   * @param name
   * @return
   */
  public WorkflowResponse queryProjectFlow(User operator, String projectName, String name) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      throw new NotFoundException("project", projectName);
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      throw new PermissionException("project read or project owner", operator.getName());
    }

    return new WorkflowResponse(flowDao.projectFlowfindByName(project.getId(), name));
  }

  /**
   * @param operator
   * @param projectName
   * @param name
   * @return
   */
  public org.springframework.core.io.Resource downloadProjectFlowFile(User operator, String projectName, String name) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      throw new NotFoundException("project", projectName);
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      throw new PermissionException("project read or project owner", operator.getName());
    }

    // 尝试从hdfs下载
    logger.info("try download workflow {} file from hdfs", name);
    String localFilename = BaseConfig.getLocalWorkflowFilename(project.getId(), UUID.randomUUID().toString());
    String hdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), name);

    logger.info("download hdfs {} to local {}", hdfsFilename, localFilename);
    HdfsClient.getInstance().copyHdfsToLocal(hdfsFilename, localFilename, false, true);

    org.springframework.core.io.Resource file = fileSystemStorageService.loadAsResource(localFilename);

    if (file != null) {
      return file;
    }

    // 从数据库中导出
    logger.info("try download workflow {} file from db", name);
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);

    try {
      String json = JsonUtil.toJsonString(new WorkflowData(projectFlow.getFlowsNodes(),projectFlow.getUserDefinedParamList()));
      InputStreamResource resource = new InputStreamResource(new FileInputStream(json));
      return resource;
    } catch (Exception e) {
      logger.error("download workflow file from db error", e);
      throw new ServerErrorException("download workflow file from db error");
    }
  }

  /**
   * project flow data 反序列化
   *
   * @param data
   * @param file
   * @return
   */
  private WorkflowData workflowDataDes(String data, MultipartFile file, String workflowName, Project project) {
    WorkflowData workflowData = null;

    if (file != null && !file.isEmpty()) {
      // TODO::
      String filename = UUID.randomUUID().toString();
      String localFilename = BaseConfig.getLocalWorkflowFilename(project.getId(), filename); // 随机的一个文件名称
      String localExtDir = BaseConfig.getLocalWorkflowExtDir(project.getId(), filename);
      String workflowJson = BaseConfig.getLocalWorkflowJson(project.getId(), filename);
      String hdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), workflowName);
      try {
        // 先将文件存放到本地
        logger.info("save workflow file {} to local {}", workflowName, localFilename);
        fileSystemStorageService.store(file, localFilename);
        // 解压 并读取workflow.json
        ZipFile zipFile = new ZipFile(localFilename);
        logger.info("ext file {} to {}", localFilename, localExtDir);
        zipFile.extractAll(localExtDir);
        String jsonString = fileSystemStorageService.readFileToString(workflowJson);
        workflowData = JsonUtil.parseObject(jsonString, WorkflowData.class);
        // 上传文件到HDFS
        if (workflowData != null) {
          logger.info("update workflow local file {} to hdfs {}", localFilename, hdfsFilename);
          HdfsClient.getInstance().copyLocalToHdfs(localFilename, hdfsFilename, true, true);
        }
      } catch (ZipException e) {
        logger.error("ext file error", e);
        return null;
      } catch (IOException e) {
        logger.error("read workflow.json error", e);
        return null;
      } catch (Exception e) {
        logger.error("workflow file process error", e);
        return null;
      }
//
//        ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
//        String jsonString = IOUtils.toString(stream, "UTF-8");

    } else if (data != null) {
      workflowData = JsonUtil.parseObject(data, WorkflowData.class);
    }

    return workflowData;
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
}
