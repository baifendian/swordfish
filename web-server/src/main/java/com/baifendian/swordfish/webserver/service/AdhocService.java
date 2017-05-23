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

import com.baifendian.swordfish.common.job.struct.node.adhoc.AdHocParam;
import com.baifendian.swordfish.common.job.struct.node.common.UdfsInfo;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.mapper.AdHocMapper;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.client.MasterClient;
import com.baifendian.swordfish.webserver.dto.AdHocLogDto;
import com.baifendian.swordfish.webserver.dto.AdHocResultDto;
import com.baifendian.swordfish.webserver.dto.ExecutorIdDto;
import com.baifendian.swordfish.webserver.dto.LogResult;
import com.baifendian.swordfish.webserver.exception.NotFoundException;
import com.baifendian.swordfish.webserver.exception.PermissionException;
import com.baifendian.swordfish.webserver.exception.ServerErrorException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.baifendian.swordfish.webserver.utils.ParamVerify.verifyProxyUser;

@Service
public class AdhocService {

  private static Logger logger = LoggerFactory.getLogger(AdhocService.class.getName());

  @Autowired
  private ProjectMapper projectMapper;

  @Autowired
  private ProjectService projectService;

  @Autowired
  private AdHocMapper adHocMapper;

  @Autowired
  private MasterServerMapper masterServerMapper;

  @Autowired
  private LogHelper logHelper;

  /**
   * 执行即席查询
   *
   * @param operator
   * @param projectName
   * @param stms
   * @param limit
   * @param proxyUser
   * @param queue
   * @param udfs
   * @param timeout
   * @return
   */
  public ExecutorIdDto execAdhoc(User operator, String projectName, String stms, int limit, String proxyUser, String queue, List<UdfsInfo> udfs, int timeout) {

    // 查看用户对项目是否具备相应权限
    Project project = projectMapper.queryByName(projectName);
    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project \"{0}\"", projectName);
    }
    // 必须要有project执行权限
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    // 判断 proxyUser 是否合理的
    verifyProxyUser(operator.getProxyUserList(), proxyUser);

    // 查看 master 是否存在
    MasterServer masterServer = masterServerMapper.query();
    if (masterServer == null) {
      logger.error("Master server does not exist.");
      throw new ServerErrorException("Master server does not exist.");
    }

    // 插入数据库记录, 得到 exec id
    AdHoc adhoc = new AdHoc();
    Date now = new Date();

    // 这里不涉及到项目名称
    adhoc.setProjectId(project.getId());

    // 这里不涉及到 owner 名称
    adhoc.setOwner(operator.getId());

    // 构造 parameter, {"stms": "", limit: xxx, udfs: {}}
    AdHocParam adHocParam = new AdHocParam();

    adHocParam.setLimit(limit);
    adHocParam.setStms(stms);
    adHocParam.setUdfs(udfs);

    adhoc.setParameter(JsonUtil.toJsonString(adHocParam));

    adhoc.setProxyUser(proxyUser);
    adhoc.setQueue(queue);
    adhoc.setStatus(FlowStatus.INIT);
    adhoc.setTimeout(timeout);
    adhoc.setCreateTime(now);

    // 插入即席查询
    try {
      adHocMapper.insert(adhoc);
    } catch (DuplicateKeyException e) {
      logger.error("Adhoc has exist, can't create again.", e);
      throw new ServerErrorException("Adhoc has exist, can't create again.");
    }

    // 连接
    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());

    logger.info("Call master client, exec id: {}, host: {}, port: {}", adhoc.getId(), masterServer.getHost(), masterServer.getPort());

    RetInfo retInfo = masterClient.execAdHoc(adhoc.getId());

    if (retInfo == null || retInfo.getStatus() != 0) {
      // 查询状态, 如果还是 INIT, 则需要更新为 FAILED
      AdHoc adHoc = adHocMapper.selectById(adhoc.getId());

      if (adHoc != null && adHoc.getStatus() == FlowStatus.INIT) {
        adHoc.setStatus(FlowStatus.FAILED);
        adHocMapper.updateStatus(adHoc);
      }

      logger.error("call master server error");
      throw new ServerErrorException("master server return error");
    }

    return new ExecutorIdDto(adhoc.getId());
  }

  /**
   * 查询日志
   *
   * @param operator
   * @param execId
   * @param index
   * @param from
   * @param size
   * @return
   */
  public AdHocLogDto queryLogs(User operator, int execId, int index, int from, int size) {

    // 查看用户对项目是否具备相应权限
    Project project = adHocMapper.queryProjectByExecId(execId);

    if (project == null) {
      logger.error("Exec id: {} has no correspond project", execId);
      throw new NotFoundException("Exec id \"{0}\" has no correspond project", execId);
    }

    //必须要有project执行权限
    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" exec permission", operator.getName(), project.getName());
    }

    // 返回日志信息
    // 1. 得到日志 id
    AdHoc adhoc = adHocMapper.selectById(execId);
    if (adhoc == null) {
      logger.error("Exec id not exist: {}", execId);
      throw new NotFoundException("Exec id \"{0}\" not exist", execId);
    }

    // 查看是否分配了 jobId
    String jobId = adhoc.getJobId();

    if (StringUtils.isEmpty(jobId)) {
      logger.warn("Job id of exec: {} is null", execId);
      throw new ServerErrorException("Job id of exec \"{0}\" is null", execId);
    }

    // 2. 先判断有多少个语句, index 如果超过了, 返回异常
    List<AdHocResult> results = adHocMapper.selectResultById(execId);

    // 3. 得到需要的 index
    if (results == null || index >= results.size()) {
      logger.error("Result is empty or index equal or more than results size");
      throw new ServerErrorException("Result is empty or index equal or more than results size");
    }

    // 3. 查看结果
    AdHocResult adHocResult = results.get(index);

    // 4. 获取日志信息
    LogResult logResult = logHelper.getLog(from, size, jobId);

    // 构造结果返回
    AdHocLogDto adHocLogDto = new AdHocLogDto();

    // 如果任务被 kill 了或者是某些异常导致失败
    if (adHocResult.getStatus().typeIsNotFinished() && adhoc.getStatus().typeIsFinished()) {
      adHocLogDto.setStatus(adhoc.getStatus());
      adHocLogDto.setHasResult(true);
    } else {
      adHocLogDto.setStatus(adHocResult.getStatus());
      adHocLogDto.setHasResult(adHocResult.getStatus().typeIsFinished());
    }

    adHocLogDto.setLastSql(index == results.size() - 1);
    adHocLogDto.setLogContent(logResult);

    return adHocLogDto;
  }

  /**
   * 查询结果
   *
   * @param operator
   * @param execId
   * @param index
   * @return
   */
  public AdHocResultDto queryResult(User operator, int execId, int index) {

    // 查看用户对项目是否具备相应权限
    Project project = adHocMapper.queryProjectByExecId(execId);

    if (project == null) {
      logger.error("Exec id: {} has no correspond project", execId);
      throw new NotFoundException("Exec id \"{0}\" has no correspond project", execId);
    }

    //如果有项目的执行去哪先才可以kill
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    // 返回结果
    AdHocResult adHocResult = adHocMapper.selectResultByIdAndIndex(execId, index);

    AdHocResultDto adHocResultDto = new AdHocResultDto();

    adHocResultDto.setStartTime(adHocResult.getStartTime());
    adHocResultDto.setEndTime(adHocResult.getEndTime());
    adHocResultDto.setStm(adHocResult.getStm());
    adHocResultDto.setResults(JsonUtil.parseObject(adHocResult.getResult(), AdHocJsonObject.class));

    return adHocResultDto;
  }

  /**
   * kill 即席查询任务, 只有在没有运行完的任务才可以 kill
   *
   * @param operator
   * @param execId
   */
  public void killAdhoc(User operator, int execId) {

    // 查看用户对项目是否具备相应权限
    Project project = adHocMapper.queryProjectByExecId(execId);

    if (project == null) {
      logger.error("Exec id: {} has no correspond project", execId);
      throw new NotFoundException("Exec id \"{0}\" has no correspond project", execId);
    }

    //如果有项目的执行去哪先才可以kill
    if (!projectService.hasWritePerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User \"{0}\" is not has project \"{1}\" write permission", operator.getName(), project.getName());
    }

    // kill 即席, 通过数据库来做到
    AdHoc adhoc = adHocMapper.selectById(execId);
    if (adhoc == null) {
      logger.error("Exec id not exist: {}", execId);
      throw new NotFoundException("Exec id \"{0}\" not exist", execId);
    }

    // 如果没有完成, 则更新为 kill
    if (adhoc.getStatus().typeIsNotFinished()) {
      adhoc.setStatus(FlowStatus.KILL);
      adHocMapper.updateStatus(adhoc);
    }
  }
}
