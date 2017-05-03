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

import com.baifendian.swordfish.common.job.struct.hql.AdHocParam;
import com.baifendian.swordfish.common.job.struct.hql.UdfsInfo;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.mapper.AdHocMapper;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.*;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.client.MasterClient;
import com.baifendian.swordfish.webserver.dto.AdHocLogData;
import com.baifendian.swordfish.webserver.dto.AdHocResultData;
import com.baifendian.swordfish.webserver.dto.ExecutorId;
import com.baifendian.swordfish.webserver.dto.LogResult;
import com.baifendian.swordfish.webserver.exception.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
  @Transactional(value = "TransactionManager")
  public ExecutorId execAdhoc(User operator, String projectName, String stms, int limit, String proxyUser, String queue, List<UdfsInfo> udfs, int timeout) {

    // 查看用户对项目是否具备相应权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      throw new NotFoundException("Not found project '{0}'", projectName);
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), projectName);
      throw new PermissionException("User '{0}' has no right permission for the project '{1}'", operator.getName(), projectName);
    }

    // 查看 master 是否存在
    MasterServer masterServer = masterServerMapper.query();
    if (masterServer == null) {
      logger.error("Master server does not exist.");
      throw new ServerErrorException("Master server does not exist.");
    }

    // 判断 proxyUser 是否合理的
    if (CollectionUtils.isEmpty(operator.getProxyUserList())) {
      logger.error("Proxy user list is empty, operator: {}", operator.getName());
      throw new ServerErrorException("Proxy user list of the operator is empty.");
    }

    // 如果不是代理所有用户, 且不包含代理的用户
    List<String> proxyUserList = operator.getProxyUserList();
    if (!proxyUserList.get(0).equals("*") && !proxyUserList.contains(proxyUser)) {
      logger.error("Proxy user '{}' not allowed for user '{}'", proxyUser, operator.getName());
      throw new BadRequestException("Proxy user '{0}' not allowed for user '{1}'", proxyUser, operator.getName());
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
      throw new NotModifiedException("Adhoc has exist, can't create again.");
    }

    // 连接
    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());
    try {
      logger.info("Call master client, exec id: {}, host: {}, port: {}", adhoc.getId(), masterServer.getHost(), masterServer.getPort());

      RetInfo retInfo = masterClient.execAdHoc(adhoc.getId());

      if (retInfo.getStatus() != 0) {
        throw new ServerErrorException("master server return error");
      }
    } catch (Exception e) {
      logger.error("Adhoc exec exception.", e);
      throw new ServerErrorException("Adhoc exec exception.");
    }

    return new ExecutorId(adhoc.getId());
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
  public AdHocLogData queryLogs(User operator, int execId, int index, int from, int size) {

    // 查看用户对项目是否具备相应权限
    Project project = adHocMapper.queryProjectByExecId(execId);

    if (project == null) {
      logger.error("Exec id: {} has no correspond project", execId);
      throw new NotFoundException("Exec id '{0}' has no correspond project", execId);
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User [{},{}] has no right permission for the project [{}], execId: {}", operator.getId(), operator.getName(), project.getName(), execId);
      throw new PermissionException("User ['{0}','{1}'] has no right permission for the project ['{2}'], execId '{3}'", operator.getId(), operator.getName(), project.getName(), execId);
    }

    // 返回日志信息
    // 1. 得到日志 id
    AdHoc adhoc = adHocMapper.selectById(execId);
    if (adhoc == null) {
      logger.error("Exec id not exist: {}", execId);
      throw new NotFoundException("Exec id '{0}' not exist", execId);
    }

    String jobId = adhoc.getJobId();

    if (StringUtils.isEmpty(jobId)) {
      logger.warn("Job id of exec: {} is null", execId);
      throw new ServerErrorException("Job id of exec '{0}' is null", execId);
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
    AdHocLogData adHocLogData = new AdHocLogData();

    adHocLogData.setStatus(adHocResult.getStatus());
    adHocLogData.setHasResult(adHocResult.getStatus().typeIsFinished());
    adHocLogData.setLastSql(index == results.size() - 1);
    adHocLogData.setLogContent(logResult);

    return adHocLogData;
  }

  /**
   * 查询结果
   *
   * @param operator
   * @param execId
   * @param index
   * @return
   */
  public AdHocResultData queryResult(User operator, int execId, int index) {

    // 查看用户对项目是否具备相应权限
    Project project = adHocMapper.queryProjectByExecId(execId);

    if (project == null) {
      logger.error("Exec id: {} has no correspond project", execId);
      throw new NotFoundException("Exec id '{0}' has no correspond project", execId);
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      throw new PermissionException("User '{0}' has no right permission for the project '{1}'", operator.getName(), project.getName());
    }

    // 返回结果
    AdHocResult adHocResult = adHocMapper.selectResultByIdAndIndex(execId, index);

    AdHocResultData adHocResultData = new AdHocResultData();

    adHocResultData.setStartTime(adHocResult.getStartTime());
    adHocResultData.setEndTime(adHocResult.getEndTime());
    adHocResultData.setStm(adHocResult.getStm());
    adHocResultData.setResults(JsonUtil.parseObject(adHocResult.getResult(), AdHocJsonObject.class));

    return adHocResultData;
  }
}
