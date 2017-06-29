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
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.BaseParamFactory;
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpBuilder;
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.FileReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.setting.Setting;
import com.baifendian.swordfish.common.job.struct.node.impexp.setting.Speed;
import com.baifendian.swordfish.common.utils.CommonUtil;
import com.baifendian.swordfish.common.utils.graph.Graph;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.mapper.ProjectFlowMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.dto.ExecutorIdDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.baifendian.swordfish.common.job.struct.node.JobType.IMPEXP;
import static com.baifendian.swordfish.webserver.utils.ParamVerify.*;

@Service
public class WorkflowService {

  private static Logger logger = LoggerFactory.getLogger(WorkflowService.class.getName());

  private static final String WorkflowJson = "workflow.json";

  @Autowired
  private ProjectFlowMapper projectFlowMapper;

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private FileSystemStorageService fileSystemStorageService;

  @Autowired
  private ExecService execService;

  @Autowired
  private FlowDao flowDao;

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
   * @param extras      工作流的额外参数
   * @param flag        工作流的标示, 0 表示正常途径创建的, 1 表示是临时的工作流, 后期可能根据一定规则清理
   * @return 已经创建的工作流实体
   */
  public ProjectFlow createWorkflow(User operator, String projectName, String name, String desc, String proxyUser, String queue, String data, MultipartFile file, String extras, Integer flag) {

    // 校验变量
    verifyWorkflowName(name);
    verifyDesc(desc);
    verifyExtras(extras);

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // project 是否存在写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    // 判断 proxyUser 是否合理的
    verifyProxyUser(operator.getProxyUserList(), proxyUser);

    // 对工作流详定义 json 进行反序列化
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

    // 检测工作流节点定义 json 是否正常
    for (WorkflowNodeDto flowNode : flowNodes) {
      if (!flowNodeParamCheck(flowNode.getParameter(), flowNode.getType())) {
        logger.error("Flow node {} parameter invalid", flowNode.getName());
        throw new ParameterException("Flow node \"{0}\" parameter invalid ", flowNode.getName());
      }

      // 校验节点的额外参数
      verifyExtras(flowNode.getExtras());
    }

    ProjectFlow projectFlow = new ProjectFlow();
    Date now = new Date();

    // 组装新建数据流实体
    try {
      List<FlowNode> flowNodeList = new ArrayList<>();

      for (WorkflowNodeDto flowNode : flowNodes) {
        flowNodeList.add(flowNode.convertFlowNode());
      }

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
      projectFlow.setExtras(extras);
      projectFlow.setFlowsNodes(flowNodeList);
      projectFlow.setUserDefinedParamList(workflowData.getUserDefParams());
      projectFlow.setFlag(flag);
    } catch (Exception e) {
      logger.error("Project flow set value error", e);
      throw new BadRequestException("Project flow set value error", e);
    }

    try {
      flowDao.createProjectFlow(projectFlow);
    } catch (DuplicateKeyException e) {
      logger.error("Workflow has exist, can't create again.", e);
      throw new ServerErrorException("Workflow has exist, can't create again.");
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
    verifyExtras(extras);

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    // 判断 proxyUser 是否合理的
    if (StringUtils.isNotEmpty(proxyUser)) {
      verifyProxyUser(operator.getProxyUserList(), proxyUser);
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);

    if (projectFlow == null) {
      logger.error("Not found project flow {} in project {}", name, project.getName());
      throw new NotFoundException("Not found project flow \"{0}\" in project \"{1}\"", name, project.getName());
    }

    Date now = new Date();

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
          if (!flowNodeParamCheck(workflowNodeDTO.getParameter(), workflowNodeDTO.getType())) {
            logger.error("Flow node {} parameter invalid", workflowNodeDTO.getName());
            throw new BadRequestException("workflow node parameter not valid");
          }

          // 校验节点的额外参数
          verifyExtras(workflowNodeDTO.getExtras());
        }

        // 拼装 flowNode
        List<FlowNode> flowNodeList = new ArrayList<>();
        for (WorkflowNodeDto workflowNodeDTO : workflowNodeDTOList) {
          try {
            flowNodeList.add(workflowNodeDTO.convertFlowNode());
          } catch (JsonProcessingException e) {
            logger.error("workflow node dto convert flowNode error", e);
            throw new BadRequestException("workflow node parameter not valid");
          }
        }

        projectFlow.setFlowsNodes(flowNodeList);
      }
    }

    if (StringUtils.isNotEmpty(extras)) {
      projectFlow.setExtras(extras);
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
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    ProjectFlow srcProjectFlow = flowDao.projectFlowfindByName(project.getId(), srcWorkflowName);

    if (srcProjectFlow == null) {
      logger.error("Not found project flow {} in project {}", srcWorkflowName, project.getName());
      throw new NotFoundException("Not found project flow \"{0}\" in project \"{1}\"", srcWorkflowName, project.getName());
    }

    String data = JsonUtil.toJsonString(new WorkflowData(srcProjectFlow.getFlowsNodes(), srcProjectFlow.getUserDefinedParamList(), FlowNode.class));

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

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 应该有项目写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);

    if (projectFlow == null) {
      logger.error("Not found project flow {} in project {}", name, project.getName());
      throw new NotFoundException("Not found project flow \"{0}\" in project \"{1}\"", name, project.getName());
    }

    // 删除工作流
    flowDao.deleteWorkflow(projectFlow.getId());

    // 删除工作流相关的资源信息
    String hdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), name);
    HdfsClient.getInstance().delete(hdfsFilename, true);
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
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 写权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
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
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 读权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
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
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 读权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    // 如果projectFlow 抛出异常
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);
    if (projectFlow == null) {
      throw new NotFoundException("Not found Workflow \"{0}\"", name);
    }

    return projectFlow;
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
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 读权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    // 尝试从 hdfs 下载
    logger.info("try download workflow {} file from hdfs", name);

    String localFilename = BaseConfig.getLocalWorkflowFilename(project.getId(), UUID.randomUUID().toString());
    String hdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), name);

    logger.info("download hdfs {} to local {}", hdfsFilename, localFilename);

    try {
      HdfsClient.getInstance().copyHdfsToLocal(hdfsFilename, localFilename, false, true);
    } catch (Exception e) {
      logger.error("try download workflow {} file error", e);
    }

    org.springframework.core.io.Resource file = fileSystemStorageService.loadAsResource(localFilename);

    if (file != null) {
      return file;
    }

    // 从数据库中导出
    logger.info("try download workflow {} file from db", name);
    ProjectFlow projectFlow = flowDao.projectFlowfindByName(project.getId(), name);

    try {
      String json = JsonUtil.toJsonString(new WorkflowData(projectFlow.getFlowsNodes(), projectFlow.getUserDefinedParamList(), FlowNode.class));
      InputStreamResource resource = new InputStreamResource(new FileInputStream(json));
      return resource;
    } catch (Exception e) {
      logger.error("download workflow file from db error", e);
      throw new ServerErrorException("download workflow file from db error");
    }
  }

  /**
   * 工作流 data 反序列化
   *
   * @param data
   * @param file
   * @param workflowName
   * @param project
   * @return
   */
  private WorkflowData workflowDataDes(String data, MultipartFile file, String workflowName, Project project) {
    WorkflowData workflowData = null;

    if (file != null && !file.isEmpty()) {
      // 生成路径
      String filename = UUID.randomUUID().toString();
      // 下载到本地的路径, 带 ".zip"
      String localFilename = BaseConfig.getLocalWorkflowFilename(project.getId(), filename);
      // 解压出来的文件目录
      String localExtractDir = BaseConfig.getLocalWorkflowExtractDir(project.getId(), filename);
      // hdfs 的路径
      String hdfsFilename = BaseConfig.getHdfsWorkflowFilename(project.getId(), workflowName);

      try {
        // 先将文件存放到本地
        logger.info("save workflow file {} to local {}", workflowName, localFilename);
        fileSystemStorageService.store(file, localFilename);

        // 解压 并读取
        ZipFile zipFile = new ZipFile(localFilename);
        logger.info("ext file {} to {}", localFilename, localExtractDir);
        zipFile.extractAll(localExtractDir);

        String jsonString = fileSystemStorageService.readFileToString(WorkflowJson);
        workflowData = JsonUtil.parseObject(jsonString, WorkflowData.class);

        // 上传文件到 HDFS
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
   * 本地文件上传到hdfs
   *
   * @param hdfsPath
   * @param file
   */
  public void fileToHdfs(User operator, String projectName, String hdfsPath, MultipartFile file, String proxyUser) {

    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 读权限
    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    if (StringUtils.isEmpty(hdfsPath)) {
      logger.error("HdfsPath:{} not valid!", hdfsPath);
      throw new BadRequestException("HdfsPath:\"{0}\" not valid!", hdfsPath);
    }

    if (file == null || file.isEmpty()) {
      logger.error("File must be not null!");
      throw new BadRequestException("File must be not null!");
    }

    String fileSuffix = CommonUtil.fileSuffix(file.getOriginalFilename());


    // 生成路径
    String filename = MessageFormat.format("{0}.{1}", UUID.randomUUID().toString(), fileSuffix);
    // 下载到本地的路径, 使用本地资源的缓存文件夹
    String localFilename = BaseConfig.getLocalResourceFilename(project.getId(), filename);


  }

  /**
   * 本地文件上传到hive中
   *
   * @param projectName
   * @param data
   * @param file
   */
  public ExecutorIdDto fileToHive(User operator, String projectName, String data, String userDefParams, MultipartFile file, String proxyUser, String queue) {
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 必须有 project 执行权限
    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    if (file == null || file.isEmpty()) {
      logger.error("File must be not null!");
      throw new BadRequestException("File must be not null!");
    }

    //生成UUID
    String uuid = UUID.randomUUID().toString();
    //伪造execId
    long execId = uuid.hashCode();
    //自动生成工作流名称
    String workflowName = uuid;
    String nodeName = uuid;
    String hdfsPath = BaseConfig.getHdfsImpExpDir(project.getId(), execId, nodeName);
    logger.info("Create execId: {}, nodeName: {},hdfsPath: {}", workflowName, nodeName, hdfsPath);
    // 1.上传文件到hdfs
    // 生成路径
    String fileSuffix = CommonUtil.fileSuffix(file.getOriginalFilename());
    String filename = MessageFormat.format("{0}.{1}", UUID.randomUUID().toString(), fileSuffix);
    // 下载到本地的路径, 使用本地资源的缓存文件夹
    String localFilename = BaseConfig.getLocalResourceFilename(project.getId(), filename);

    try {
      logger.info("Start save file in local cache: {}", localFilename);
      //先把文件缓存在本地
      fileSystemStorageService.store(file, localFilename);
      logger.info("Finish save file in local cache");

      logger.info("Start upload local file: {} to hdfs: {} ", localFilename, hdfsPath);
      //再上传到hdfs
      HdfsClient.getInstance().copyLocalToHdfs(localFilename, hdfsPath, true, true);
      logger.info("Finish upload local file to hdfs");
    } catch (Exception e) {
      logger.error("workflow file process error", e);
      throw new ServerErrorException("workflow file process error:{0}", e.getMessage());
    }

    //增加默认setting 避免检测
    ImpExpParam impExpParam = (ImpExpParam) BaseParamFactory.getBaseParam(IMPEXP, data);
    ((FileReader) impExpParam.getReader()).setHdfsPath(hdfsPath);
    impExpParam.setSetting(new Setting(new Speed()));

    //生成一个工作流
    String workflowData = "{\"nodes\":[{\"name\":\"{0}\",\"desc\":\"file to hive temp workflow\",\"type\":\"IMPEXP\",\"parameter\":{1}}],\"userDefParams\":{2}}";
    workflowData = MessageFormat.format(workflowData, nodeName, JsonUtil.toJsonString(impExpParam), userDefParams);

    logger.info("Create workflow data: {}", workflowData);

    return execService.postExecWorkflowDirect(operator, projectName, workflowName, null, proxyUser, queue, workflowData, null, null, null, 1800, null);
  }
}
