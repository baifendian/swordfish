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
import com.baifendian.swordfish.dao.mapper.*;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.dto.WorkflowData;
import com.baifendian.swordfish.webserver.dto.WorkflowNodeDto;
import com.baifendian.swordfish.webserver.exception.*;
import com.baifendian.swordfish.webserver.service.storage.FileSystemStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.baifendian.swordfish.webserver.utils.ParamVerify.verifyDesc;
import static com.baifendian.swordfish.webserver.utils.ParamVerify.verifyWorkflowName;

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

  @Autowired
  private ScheduleMapper scheduleMapper;

  /**
   * 创建一个工作流, 需要具备项目的 \"w\" 权限。
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
  public ProjectFlow createWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, String extras, Integer flag) {

    // 校验变量
    verifyWorkflowName(name);
    verifyDesc(desc);

    // 查看是否对项目具备相应的权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found PROJECT \"{0}\"", projectName);
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the PROJECT {} to create PROJECT flow", operator.getName(), projectName);
      throw new PermissionException("User \"{0}\" is not has PROJECT \"{1}\" write permission", operator.getName(), projectName);
    }

    // 对工作流详定义json进行反序列化
    WorkflowData workflowData = workflowDataDes(data, file, name, project);

    if (workflowData == null) {
      logger.error("Project flow data or file not valid");
      throw new ParameterException("Data \"{0}\" not valid", data);
    }

    // 检测工作流节点是否正常
    List<WorkflowNodeDto> flowNodes = workflowData.getNodes();

    if (CollectionUtils.isEmpty(flowNodes)) {
      logger.error("flow node information is empty");
      throw new ParameterException("Data \"{0}\" is null", data);
    }

    // 检测工作节点是否存在闭环
    if (graphHasCycle(flowNodes)) {
      logger.error("Proejct flow DAG has cycle");
      throw new ParameterException("Flow node has cycle");
    }

    // 检测工作流节点定义json是否正常
    for (WorkflowNodeDto flowNode : flowNodes) {
      // TODO:: 这个检测不是很合理, 需要修改, 不太完备
      if (!flowNodeParamCheck(flowNode.getParameter(), flowNode.getType())) {
        logger.error("Flow node {} parameter invalid", flowNode.getName());
        throw new ParameterException("Flow node parameter not valid");
      }
    }


    ProjectFlow projectFlow = new ProjectFlow();
    Date now = new Date();

    // 组装新建数据流实体
    try {
      List<FlowNode> flowNodeList = new ArrayList<>();

      for (WorkflowNodeDto flowNode : flowNodes) {
        flowNodeList.add(flowNode.convertFlowNode());
      }

      projectFlow.setExtras(extras);
      projectFlow.setFlowsNodes(flowNodeList);
      projectFlow.setUserDefinedParamList(workflowData.getUserDefParams());
      projectFlow.setName(name);
      projectFlow.setProjectId(project.getId());
      projectFlow.setProjectName(projectName);
      projectFlow.setDesc(desc);
      projectFlow.setCreateTime(now);
      projectFlow.setModifyTime(now);
      projectFlow.setProxyUser(proxyUser);
      projectFlow.setQueue(queue);
      projectFlow.setOwnerId(operator.getId());
      projectFlow.setOwner(operator.getName());
      projectFlow.setFlag(flag);
    } catch (Exception e) {
      logger.error("Project flow set value error", e);
      throw new BadRequestException("Project flow set value error", e);
    }

    try {
      flowDao.createProjectFlow(projectFlow);
    } catch (DuplicateKeyException e) {
      logger.error("Workflow has exist, can't create again.", e);
      throw new NotModifiedException("Workflow has exist, can't create again.");
    } catch (Exception e) {
      logger.error("Workflow create has error", e);
      throw new ServerErrorException("Workflow create has error", e);
    }

    return projectFlow;
  }

  /**
   * 覆盖式修改一个工作流，如果不存在就创建。需要具备项目的 \"w\" 权限。
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
  public ProjectFlow putWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, String extras) {
    ProjectFlow projectFlow = flowDao.projectFlowFindByPorjectNameAndName(projectName, name);

    if (projectFlow == null) {
      return createWorkflow(operator, projectName, name, desc, proxyUser, queue, data, file, extras, null);
    }

    return patchWorkflow(operator, projectName, name, desc, proxyUser, queue, data, file, extras);
  }

  /**
   * 非覆盖式修改一个已经存在的工作流。需要具备项目的 \"w\" 权限。
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
  public ProjectFlow patchWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, String extras) {

    verifyDesc(desc);

    // 查询项目是否存在以及是否具备相应权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found PROJECT \"{0}\"", projectName);
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the PROJECT {} to patch PROJECT flow", operator.getName(), projectName);
      throw new PermissionException("User \"{0}\" is not has PROJECT \"{1}\" write permission", operator.getName(), projectName);
    }

    // 查询工作流信息
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);
    Date now = new Date();

    if (projectFlow == null) {
      logger.error("Workflow does not exist: {}", name);
      throw new NotFoundException("Not found PROJECT \"{0}\"", projectName);
    }

    // 解析
    WorkflowData workflowData = workflowDataDes(data, file, name, project);

    if (workflowData != null) {

      if (!CollectionUtils.isEmpty(workflowData.getUserDefParams())) {
        projectFlow.setUserDefinedParamList(workflowData.getUserDefParams());
      }

      List<WorkflowNodeDto> workflowNodeDTOList = workflowData.getNodes();
      if (CollectionUtils.isNotEmpty(workflowNodeDTOList)) {
        // 闭环检测
        if (graphHasCycle(workflowNodeDTOList)) {
          logger.error("Graph has cycle");
          throw new BadRequestException("Flow node has cycle");
        }

        // parameter 检测
        for (WorkflowNodeDto workflowNodeDTO : workflowNodeDTOList) {
          // TODO::参数检测存在问题
          if (!flowNodeParamCheck(workflowNodeDTO.getParameter(), workflowNodeDTO.getType())) {
            throw new BadRequestException("WORKFLOW node parameter not valid");
          }
        }

        //拼装flowNode
        List<FlowNode> flowNodeList = new ArrayList<>();
        for (WorkflowNodeDto workflowNodeDTO : workflowNodeDTOList) {
          try {
            flowNodeList.add(workflowNodeDTO.convertFlowNode());
          } catch (JsonProcessingException e) {
            logger.error("WORKFLOW node dto convert flowNode error", e);
            throw new BadRequestException("WORKFLOW node parameter not valid");
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
      throw new ServerErrorException("Workflow modify has error", e);
    }

    return projectFlow;
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
  public ProjectFlow postWorkflowCopy(User operator, String projectName, String srcWorkflowName, String destWorkflowName) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      throw new NotFoundException("Not found PROJECT \"{0}\"", projectName);
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has PROJECT \"{1}\" write permission", operator.getName(), projectName);
    }

    // 获取数据库信息

    ProjectFlow srcProjectFlow = flowDao.projectFlowfindByName(project.getId(), srcWorkflowName);

    if (srcProjectFlow == null) {
      throw new NotFoundException("Not found WORKFLOW \"{0}\"", srcWorkflowName);
    }

    String data = JsonUtil.toJsonString(new WorkflowData(srcProjectFlow.getFlowsNodes(), srcProjectFlow.getUserDefinedParamList(), FlowNode.class));

    // 尝试拷贝文件
    String srcHdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), srcWorkflowName);
    String destHdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), destWorkflowName);

    logger.info("try copy WORKFLOW file {} to WORKFLOW file {}", srcHdfsFilename, destHdfsFilename);
    try {
      if (HdfsClient.getInstance().exists(srcHdfsFilename)) {
        HdfsClient.getInstance().copy(srcHdfsFilename, destHdfsFilename, false, true);
      } else {
        logger.info("WORKFLOW file {} not exists no copy required", srcHdfsFilename);
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
      throw new NotFoundException("Not found PROJECT \"{0}\"", projectName);
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has PROJECT \"{1}\" write permission", operator.getName(), projectName);
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);

    if (projectFlow == null) {
      throw new NotFoundException("Not found WORKFLOW \"{0}\"", name);
    }

    //删除工作流
    flowDao.deleteWorkflow(projectFlow.getId());

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
      throw new NotFoundException("Not found PROJECT \"{0}\"", projectName);
    }

    if (!projectService.hasWritePerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has PROJECT \"{1}\" write permission", operator.getName(), projectName);
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
  public List<ProjectFlow> queryAllProjectFlow(User operator, String projectName) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      throw new NotFoundException("Not found PROJECT \"{0}\"", projectName);
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has PROJECT \"{1}\" read permission", operator.getName(), projectName);
    }

    return flowDao.projectFlowFindByProject(project.getId());
  }

  /**
   * 查询某一个具体工作流的详情
   *
   * @param operator
   * @param projectName
   * @param name
   * @return
   */
  public ProjectFlow queryProjectFlow(User operator, String projectName, String name) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      throw new NotFoundException("Not found PROJECT \"{0}\"", projectName);
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has PROJECT \"{1}\" read permission", operator.getName(), projectName);
    }

    return flowDao.projectFlowfindByName(project.getId(), name);
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
      throw new NotFoundException("Not found PROJECT \"{0}\"", projectName);
    }

    if (!projectService.hasReadPerm(operator.getId(), project)) {
      throw new PermissionException("User \"{0}\" is not has PROJECT \"{1}\" read permission", operator.getName(), projectName);
    }

    // 尝试从hdfs下载
    logger.info("try download WORKFLOW {} file from hdfs", name);
    String localFilename = BaseConfig.getLocalWorkflowFilename(project.getId(), UUID.randomUUID().toString());
    String hdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), name);

    logger.info("download hdfs {} to local {}", hdfsFilename, localFilename);
    HdfsClient.getInstance().copyHdfsToLocal(hdfsFilename, localFilename, false, true);

    org.springframework.core.io.Resource file = fileSystemStorageService.loadAsResource(localFilename);

    if (file != null) {
      return file;
    }

    // 从数据库中导出
    logger.info("try download WORKFLOW {} file from db", name);
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);

    try {
      String json = JsonUtil.toJsonString(new WorkflowData(projectFlow.getFlowsNodes(), projectFlow.getUserDefinedParamList(), FlowNode.class));
      InputStreamResource resource = new InputStreamResource(new FileInputStream(json));
      return resource;
    } catch (Exception e) {
      logger.error("download WORKFLOW file from db error", e);
      throw new ServerErrorException("download WORKFLOW file from db error");
    }
  }

  /**
   * PROJECT flow data 反序列化
   *
   * @param data
   * @param file
   * @return
   */
  private WorkflowData workflowDataDes(String data, MultipartFile file, String workflowName, Project project) {
    WorkflowData workflowData = null;

    if (file != null && !file.isEmpty()) {
      //生成路径
      String filename = UUID.randomUUID().toString();
      String localFilename = BaseConfig.getLocalWorkflowFilename(project.getId(), filename); // 随机的一个文件名称
      String localExtDir = BaseConfig.getLocalWorkflowExtractDir(project.getId(), filename);
      String workflowJson = null;

      String hdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), workflowName);
      try {
        // 先将文件存放到本地
        logger.info("save WORKFLOW file {} to local {}", workflowName, localFilename);
        fileSystemStorageService.store(file, localFilename);
        // 解压 并读取workflow.json
        ZipFile zipFile = new ZipFile(localFilename);
        logger.info("ext file {} to {}", localFilename, localExtDir);
        zipFile.extractAll(localExtDir);
        String jsonString = fileSystemStorageService.readFileToString(workflowJson);
        workflowData = JsonUtil.parseObject(jsonString, WorkflowData.class);
        // 上传文件到HDFS
        if (workflowData != null) {
          logger.info("update WORKFLOW local file {} to hdfs {}", localFilename, hdfsFilename);
          HdfsClient.getInstance().copyLocalToHdfs(localFilename, hdfsFilename, true, true);
        }
      } catch (ZipException e) {
        logger.error("ext file error", e);
        return null;
      } catch (IOException e) {
        logger.error("read WORKFLOW.json error", e);
        return null;
      } catch (Exception e) {
        logger.error("WORKFLOW file process error", e);
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
   * @param workflowNodeResponseList
   * @return
   */
  private boolean graphHasCycle(List<WorkflowNodeDto> workflowNodeResponseList) {
    Graph<String, WorkflowNodeDto, String> graph = new Graph<>();

    // 填充顶点
    for (WorkflowNodeDto workflowNodeResponse : workflowNodeResponseList) {
      graph.addVertex(workflowNodeResponse.getName(), workflowNodeResponse);
    }

    // 填充边关系
    for (WorkflowNodeDto workflowNodeResponse : workflowNodeResponseList) {
      if (CollectionUtils.isNotEmpty(workflowNodeResponse.getDep())) {
        for (String dep : workflowNodeResponse.getDep()) {
          graph.addEdge(dep, workflowNodeResponse.getName());
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
