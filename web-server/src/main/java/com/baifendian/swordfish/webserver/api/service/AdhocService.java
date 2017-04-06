/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.webserver.api.service;

import com.baifendian.swordfish.common.adhoc.AdHocParam;
import com.baifendian.swordfish.common.job.UdfsInfo;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.mapper.AdHocMapper;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.dao.model.MasterServer;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.client.MasterClient;
import com.baifendian.swordfish.webserver.api.dto.AdhocLogData;
import com.baifendian.swordfish.webserver.api.dto.AdhocResult;
import com.baifendian.swordfish.webserver.api.dto.ExecutorId;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
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
   * @param response
   * @return
   */
  @Transactional(value = "TransactionManager")
  public ExecutorId execAdhoc(User operator, String projectName, String stms, int limit, String proxyUser, String queue, List<UdfsInfo> udfs, int timeout, HttpServletResponse response) {

    // 查看用户对项目是否具备相应权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      logger.error("Project does not exist: {}", projectName);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), projectName);
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    // 查看 master 是否存在
    MasterServer masterServer = masterServerMapper.query();
    if (masterServer == null) {
      logger.error("Master server does not exist.");
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
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
      response.setStatus(HttpStatus.SC_CONFLICT);
      return null;
    }

    // 连接
    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());
    try {
      logger.info("Call master client, exec id: {}", adhoc.getId());

      RetInfo retInfo = masterClient.execAdHoc(adhoc.getId());

      if (retInfo.getStatus() != 0) {
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        return null;
      }
    } catch (Exception e) {
      logger.error("Adhoc exec exception.", e);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
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
   * @param response
   * @return
   */
  public AdhocLogData queryLogs(User operator, int execId, int index, int from, int size, HttpServletResponse response) {

    // 查看用户对项目是否具备相应权限
    Project project = adHocMapper.queryProjectByExecId(execId);

    if (project == null) {
      logger.error("Exec id: {} has no correspond project", execId);
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      logger.error("User {} has no right permission for the project {}", operator.getName(), project.getName());
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    // TODO:: 返回日志信息

    return new AdhocLogData();
  }

  /**
   * 查询结果
   *
   * @param operator
   * @param execId
   * @param index
   * @param response
   * @return
   */
  public AdhocResult queryResult(User operator, int execId, int index, HttpServletResponse response) {

    // 查看用户对项目是否具备相应权限
    Project project = adHocMapper.queryProjectByExecId(execId);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    // TODO:: 返回结果

    return null;
  }
}
