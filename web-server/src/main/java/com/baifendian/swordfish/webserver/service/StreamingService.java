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

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.StreamingJobMapper;
import com.baifendian.swordfish.dao.mapper.StreamingResultMapper;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.model.flow.Property;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.client.MasterClient;
import com.baifendian.swordfish.webserver.dto.ExecutorIdDto;
import com.baifendian.swordfish.webserver.dto.LogResult;
import com.baifendian.swordfish.webserver.dto.StreamingResultDto;
import com.baifendian.swordfish.webserver.exception.*;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.baifendian.swordfish.webserver.utils.ParamVerify.*;

@Service
public class StreamingService {

  private static Logger logger = LoggerFactory.getLogger(StreamingService.class.getName());

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private StreamingJobMapper streamingJobMapper;

  @Autowired
  private StreamingResultMapper streamingResultMapper;

  @Autowired
  private MasterServerMapper masterServerMapper;

  @Autowired
  private ProjectService projectService;

  /**
   * 插入一个任务
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param type
   * @param parameter
   * @param userDefParams
   * @param extras
   * @return
   */
  public StreamingJob createStreamingJob(User operator,
                                         String projectName,
                                         String name,
                                         String desc,
                                         String type,
                                         String parameter,
                                         String userDefParams,
                                         String extras) {

    // 校验变量
    verifyStreamingName(name);
    verifyDesc(desc);
    verifyExtras(extras);

    // 查询项目, 如果不存在, 返回错误
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

    // 对节点进行解析
    if (!flowNodeParamCheck(parameter, type)) {
      logger.error("Streaming job parameter:{} invalid", parameter);
      throw new ParameterException("Streaming job parameter: \"{0}\" invalid", parameter);
    }

    // 对自定义参数进行解析
    try {
      JsonUtil.parseObjectList(userDefParams, Property.class);
    } catch (Exception e) {
      logger.error("Streaming job user define parameters:{} invalid", userDefParams);
      throw new ParameterException("Streaming job user define parameters: \"{0}\" invalid", userDefParams);
    }

    StreamingJob streamingJob = new StreamingJob();
    Date now = new Date();

    // 组装新建数据流实体
    try {
      streamingJob.setOwnerId(operator.getId());
      streamingJob.setOwner(operator.getName());
      streamingJob.setProjectId(project.getId());
      streamingJob.setProjectName(projectName);
      streamingJob.setName(name);
      streamingJob.setCreateTime(now);
      streamingJob.setModifyTime(now);
      streamingJob.setDesc(desc);
      streamingJob.setType(type);
      streamingJob.setParameter(parameter);
      streamingJob.setUserDefinedParams(userDefParams);
      streamingJob.setExtras(extras);
    } catch (Exception e) {
      logger.error("Str set value error", e);
      throw new BadRequestException("Project flow set value error", e);
    }

    try {
      streamingJobMapper.insertAndGetId(streamingJob);
    } catch (DuplicateKeyException e) {
      logger.error("Streaming job has exist, can't create again.", e);
      throw new ServerErrorException("Streaming job has exist, can't create again.");
    } catch (Exception e) {
      logger.error("Streaming job create has error", e);
      throw new ServerErrorException("Streaming job create has error", e);
    }

    return streamingJob;
  }


  /**
   * 更新并插入一条任务
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param type
   * @param parameter
   * @param userDefParams
   * @param extras
   * @return
   */
  public StreamingJob putStreamingJob(User operator,
                                      String projectName,
                                      String name,
                                      String desc,
                                      String type,
                                      String parameter,
                                      String userDefParams,
                                      String extras) {
    StreamingJob streamingJob = streamingJobMapper.findByProjectNameAndName(projectName, name);

    if (streamingJob == null) {
      return createStreamingJob(operator, projectName, name, desc, type, parameter, userDefParams, extras);
    }

    return patchStreamingJob(operator, projectName, name, desc, parameter, userDefParams, extras);
  }

  /**
   * 修改一条流任务信息
   *
   * @param operator
   * @param projectName
   * @param name
   * @param desc
   * @param parameter
   * @param userDefParams
   * @param extras
   * @return
   */
  public StreamingJob patchStreamingJob(User operator,
                                        String projectName,
                                        String name,
                                        String desc,
                                        String parameter,
                                        String userDefParams,
                                        String extras) {

    verifyDesc(desc);
    verifyExtras(extras);

    // 查询项目, 如果不存在, 返回错误
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

    // 对于修改流任务的情况, 必须是预先存在的
    StreamingJob streamingJob = streamingJobMapper.findByProjectNameAndName(projectName, name);

    if (streamingJob == null) {
      logger.error("Not found streaming job {} in project {}", name, project.getName());
      throw new NotFoundException("Not found streaming job \"{0}\" in project \"{1}\"", name, project.getName());
    }

    Date now = new Date();

    // 对节点进行解析
    if (StringUtils.isNotEmpty(parameter)) {
      if (!flowNodeParamCheck(parameter, streamingJob.getType())) {
        logger.error("Streaming job parameter:{} invalid", parameter);
        throw new ParameterException("Streaming job parameter: \"{0}\" invalid", parameter);
      }

      streamingJob.setParameter(parameter);
    }

    // 对自定义参数进行解析
    if (StringUtils.isNotEmpty(userDefParams)) {
      try {
        JsonUtil.parseObjectList(userDefParams, Property.class);
      } catch (Exception e) {
        logger.error("Streaming job user define parameters:{} invalid", userDefParams);
        throw new ParameterException("Streaming job user define parameters: \"{0}\" invalid", userDefParams);
      }

      streamingJob.setUserDefinedParams(userDefParams);
    }

    if (StringUtils.isNotEmpty(extras)) {
      streamingJob.setExtras(extras);
    }

    if (StringUtils.isNotEmpty(desc)) {
      streamingJob.setDesc(desc);
    }

    streamingJob.setModifyTime(now);
    streamingJob.setOwnerId(operator.getId());
    streamingJob.setOwner(operator.getName());

    try {
      streamingJobMapper.updateStreamingJob(streamingJob);
    } catch (Exception e) {
      logger.error("Streaming job modify has error", e);
      throw new ServerErrorException("Streaming job modify has error", e);
    }

    return streamingJob;
  }

  /**
   * 删除一个工作流
   *
   * @param operator
   * @param projectName
   * @param name
   */
  public void deleteStreamingJob(User operator, String projectName, String name) {

    // 查询项目, 如果不存在, 返回错误
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

    // 对于修改流任务的情况, 必须是预先存在的
    StreamingJob streamingJob = streamingJobMapper.findByProjectNameAndName(projectName, name);

    if (streamingJob == null) {
      logger.error("Not found streaming job {} in project {}", name, project.getName());
      throw new NotFoundException("Not found streaming job \"{0}\" in project \"{1}\"", name, project.getName());
    }

    // 必须是停止运行的才能删除
    StreamingResult streamingResult = streamingResultMapper.findLatestByStreamingId(streamingJob.getId());

    if (streamingResult != null && streamingResult.getStatus().typeIsNotFinished()) {
      logger.error("Streaming job is not finished yet: {}", streamingJob.getId());
      throw new PreFailedException("Project \"{0}\", streaming job \"{1}\" not stop, must stop first", operator.getName(), project.getName());
    }

    // 删除工作流
    streamingJobMapper.deleteById(streamingJob.getId());
  }

  /**
   * 执行一个流任务
   *
   * @param operator
   * @param projectName
   * @param name
   * @param proxyUser
   * @param queue
   * @return
   */
  @Transactional(value = "TransactionManager")
  public ExecutorIdDto executeStreamingJob(User operator, String projectName, String name, String proxyUser, String queue) {

    // 查询项目, 如果不存在, 返回错误
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

    // 对于执行流任务的情况, 必须是预先存在的
    StreamingJob streamingJob = streamingJobMapper.findByProjectNameAndName(projectName, name);

    if (streamingJob == null) {
      logger.error("Not found streaming job {} in project {}", name, project.getName());
      throw new NotFoundException("Not found streaming job \"{0}\" in project \"{1}\"", name, project.getName());
    }

    // 如果最新一条信息是显示没有执行, 插入或者更新一条记录
    StreamingResult streamingResult = streamingResultMapper.findLatestByStreamingId(streamingJob.getId());
    Date now = new Date();

    if (streamingResult != null && streamingResult.getStatus().typeIsNotFinished()) {
      logger.error("Streaming job is not finished yet: {}", streamingJob.getId());
      throw new PreFailedException("Project \"{0}\", streaming job \"{1}\" not stop, must stop first", operator.getName(), project.getName());
    }

    // 查看 master 是否存在
    MasterServer masterServer = masterServerMapper.query();
    if (masterServer == null) {
      logger.error("Master server does not exist.");
      throw new ServerErrorException("Master server does not exist.");
    }

    streamingResult = new StreamingResult();

    streamingResult.setStreamingId(streamingJob.getId());
    streamingResult.setSubmitUserId(operator.getId());
    streamingResult.setSubmitTime(now);
    streamingResult.setQueue(queue);
    streamingResult.setProxyUser(proxyUser);
    streamingResult.setScheduleTime(now);
    streamingResult.setStatus(FlowStatus.INIT);

    // 调用 master 进行执行
    try {
      streamingResultMapper.insert(streamingResult);
    } catch (DuplicateKeyException e) {
      logger.error("Streaming create failed.", e);
      throw new ServerErrorException("Streaming create failed.");
    }

    // 连接
    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());

    logger.info("Call master client, exec id: {}, host: {}, port: {}", streamingResult.getId(), masterServer.getHost(), masterServer.getPort());

//    RetInfo retInfo = masterClient.execAdHoc(adhoc.getId());
//
//    if (retInfo == null || retInfo.getStatus() != 0) {
//      // 查询状态, 如果还是 INIT, 则需要更新为 FAILED
//      AdHoc adHoc = adHocMapper.selectById(adhoc.getId());
//
//      if (adHoc != null && adHoc.getStatus() == FlowStatus.INIT) {
//        adHoc.setStatus(FlowStatus.FAILED);
//        adHocMapper.updateStatus(adHoc);
//      }
//
//      logger.error("call master server error");
//      throw new ServerErrorException("master server return error");
//    }
//
//    return new ExecutorIdDto(adhoc.getId());
    return null;
  }

  /**
   * 删除一个流任务
   *
   * @param operator
   * @param execId
   */
  public void killStreamingJob(User operator, int execId) {

  }

  /**
   * 查询项目下所有流任务
   *
   * @param operator
   * @param projectName
   * @param name
   * @param startDate
   * @param endDate
   * @param status
   * @param from
   * @param size
   * @return
   */
  public List<StreamingResultDto> queryProjectStreamingJobAndResult(User operator,
                                                                    String projectName,
                                                                    String name,
                                                                    Date startDate,
                                                                    Date endDate,
                                                                    FileStatus status,
                                                                    int from,
                                                                    int size) {
    return null;
  }

  /**
   * 查询具体某个流任务的详情
   *
   * @param operator
   * @param execId
   * @return
   */
  public List<StreamingResultDto> queryStreamingJobAndResult(User operator, int execId) {
    return null;
  }

  /**
   * 查询日志信息
   *
   * @param operator
   * @param execId
   * @param from
   * @param size
   * @return
   */
  public LogResult getStreamingJobLog(User operator, String execId, int from, int size) {
//    StreamingJob streamingJob = streamingJobMapper.findByProjectNameAndName(projectName, name);

//    ExecutionNode executionNode = streaming_result.selectExecNodeByJobId(jobId);
//
//    if (executionNode == null) {
//      logger.error("job id does not exist: {}", jobId);
//      throw new NotFoundException("Not found jobId \"{0}\"", jobId);
//    }
//
//    ExecutionFlow executionFlow = executionFlowMapper.selectByExecId(executionNode.getExecId());
//
//    if (executionFlow == null) {
//      logger.error("exec flow does not exist: {}", executionNode.getExecId());
//      throw new NotFoundException("Not found execId \"{0}\"", executionNode.getExecId());
//    }
//
//    Project project = projectMapper.queryByName(executionFlow.getProjectName());
//
//    if (project == null) {
//      logger.error("Project does not exist: {}", executionFlow.getProjectName());
//      throw new NotFoundException("Not found project \"{0}\"", executionFlow.getProjectName());
//    }
//
//    // 必须有 project 执行权限
//    if (!projectService.hasExecPerm(operator.getId(), project)) {
//      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
//      throw new PermissionException("User \"{0}\" is not has project \"{1}\" exec permission", operator.getName(), project.getName());
//    }
//
//    return logHelper.getLog(from, size, jobId);
    return null;
  }
}