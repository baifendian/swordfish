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
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.client.MasterClient;
import com.baifendian.swordfish.webserver.dto.ExecutorIdDto;
import com.baifendian.swordfish.webserver.dto.LogResult;
import com.baifendian.swordfish.webserver.dto.StreamingResultDto;
import com.baifendian.swordfish.webserver.dto.StreamingResultsDto;
import com.baifendian.swordfish.webserver.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StreamingExecService {

  private static Logger logger = LoggerFactory.getLogger(StreamingExecService.class.getName());

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private StreamingJobMapper streamingJobMapper;

  @Autowired
  private StreamingResultMapper streamingResultMapper;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private MasterServerMapper masterServerMapper;

  @Autowired
  private LogHelper logHelper;

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

    // 应该有项目执行权限
    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" exec permission", operator.getName(), project.getName());
    }

    // 对于执行流任务的情况, 必须是预先存在的
    StreamingJob streamingJob = streamingJobMapper.findByProjectNameAndName(projectName, name);

    if (streamingJob == null) {
      logger.error("Not found streaming job {} in project {}", name, project.getName());
      throw new NotFoundException("Not found streaming job \"{0}\" in project \"{1}\"", name, project.getName());
    }

    // 如果最新一条信息是显示没有执行, 插入或者更新一条记录
    StreamingResult streamingResult = streamingResultMapper.findLatestDetailByStreamingId(streamingJob.getId());
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
    streamingResult.setParameter(streamingJob.getParameter());
    streamingResult.setUserDefinedParams(streamingJob.getUserDefinedParams());
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

    int execId = streamingResult.getExecId();

    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());

    logger.info("Call master client, exec id: {}, host: {}, port: {}", execId, masterServer.getHost(), masterServer.getPort());

    RetInfo retInfo = masterClient.execStreamingJob(execId);

    if (retInfo == null || retInfo.getStatus() != 0) {
      // 查询状态, 如果还是 INIT, 则需要更新为 FAILED
      StreamingResult streamingResult1 = streamingResultMapper.selectById(execId);

      if (streamingResult1 != null && streamingResult1.getStatus() == FlowStatus.INIT) {
        streamingResult1.setStatus(FlowStatus.FAILED);
        streamingResultMapper.updateResult(streamingResult1);
      }

      logger.error("call master server error");
      throw new ServerErrorException("master server return error");
    }

    return new ExecutorIdDto(streamingResult.getExecId());
  }

  /**
   * 删除一个流任务
   *
   * @param operator
   * @param execId
   */
  public void killStreamingJob(User operator, int execId) {

    Project project = streamingResultMapper.queryProject(execId);

    if (project == null) {
      logger.error("Exec does not exist: {}", execId);
      throw new NotFoundException("Exec does not exist \"{0}\"", execId);
    }

    // 应该有项目执行权限
    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" exec permission", operator.getName(), project.getName());
    }

    // kill 流任务
    MasterServer masterServer = masterServerMapper.query();

    if (masterServer == null) {
      logger.error("Master server does not exist.");
      throw new ServerErrorException("Master server does not exist.");
    }

    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());

    try {
      logger.info("Call master client kill streaming job , project id: {}, exec id: {}, host: {}, port: {}", project.getId(), execId, masterServer.getHost(), masterServer.getPort());
      if (!masterClient.cancelStreamingJob(execId)) {
        logger.error("Call master client kill streaming job false , project id: {}, exec id: {}, host: {}, port: {}", project.getId(), execId, masterServer.getHost(), masterServer.getPort());
        throw new ServerErrorException("Call master client kill streaming job false , project id: \"{0}\", exec flow id: \"{1}\", host: \"{2}\", port: \"{3}\"", project.getId(), execId, masterServer.getHost(), masterServer.getPort());
      }
    } catch (Exception e) {
      logger.error("Call master client set schedule error", e);
      throw e;
    }
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
  public StreamingResultsDto queryStreamingExecs(User operator,
                                                 String projectName,
                                                 String name,
                                                 Date startDate,
                                                 Date endDate,
                                                 Integer status,
                                                 int from,
                                                 int size) {

    // 查询项目, 如果不存在, 返回错误
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 应该有项目执行权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    // 查询数目
    List<StreamingResultDto> streamingResultDtos = new ArrayList<>();

    List<StreamingResult> streamingResults = streamingResultMapper.findByMultiCondition(project.getId(), name, startDate, endDate, status, from, size);

    for (StreamingResult streamingResult : streamingResults) {
      streamingResultDtos.add(new StreamingResultDto(streamingResult));
    }

    int total = streamingResultMapper.findCountByMultiCondition(project.getId(), name, startDate, endDate, status);

    return new StreamingResultsDto(total, from, streamingResultDtos);
  }

  /**
   * 查询流任务最新的运行详情
   *
   * @param operator
   * @param projectName
   * @param names
   * @return
   */
  public List<StreamingResultDto> queryLatest(User operator, String projectName, String names) {

    // 查询项目, 如果不存在, 返回错误
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }

    // 应该有项目执行权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    List<String> nameList;

    try {
      nameList = JsonUtil.parseObjectList(names, String.class);
    } catch (Exception e) {
      logger.error("Parameter name:{} invalid", names);
      throw new ParameterException("Parameter name \"{0}\" invalid", names);
    }

    List<StreamingResult> streamingResults = streamingResultMapper.findDetailByProjectAndNames(
        project.getId(),
        nameList);

    List<StreamingResultDto> streamingResultDtos = new ArrayList<>();

    for (StreamingResult streamingResult : streamingResults) {
      streamingResultDtos.add(new StreamingResultDto(streamingResult));
    }

    return streamingResultDtos;
  }

  /**
   * 查询任务运行的详情
   *
   * @param operator
   * @param execId
   * @return
   */
  public List<StreamingResultDto> queryDetail(User operator, int execId) {

    Project project = streamingResultMapper.queryProject(execId);

    if (project == null) {
      logger.error("Exec does not exist: {}", execId);
      throw new NotFoundException("Exec does not exist \"{0}\"", execId);
    }

    // 应该有项目执行权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    StreamingResult streamingResult = streamingResultMapper.findDetailByExecId(execId);

    List<StreamingResultDto> streamingResultDtos = new ArrayList<>();

    streamingResultDtos.add(new StreamingResultDto(streamingResult));

    return streamingResultDtos;
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

  public LogResult queryLogs(User operator, int execId, int from, int size) {
    Project project = streamingResultMapper.queryProject(execId);

    // 注意, 这里实际上执行信息没有
    if (project == null) {
      logger.error("Exec does not exist: {}", execId);
      throw new NotFoundException("Exec does not exist \"{0}\"", execId);
    }

    // 应该有项目执行权限
    if (!projectService.hasReadPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" read permission", operator.getName(), project.getName());
    }

    StreamingResult streamingResult = streamingResultMapper.selectById(execId);

    if (streamingResult == null) {
      logger.error("Exec does not exist: {}", execId);
      throw new NotFoundException("Exec does not exist \"{0}\"", execId);
    }

    return logHelper.getLog(from, size, streamingResult.getJobId());
  }
}
