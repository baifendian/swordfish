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

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.mapper.AdHocMapper;
import com.baifendian.swordfish.dao.mapper.MasterServerMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.dao.model.MasterServer;
import com.baifendian.swordfish.dao.model.Project;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.client.MasterClient;
import com.baifendian.swordfish.webserver.api.dto.AdhocLogData;
import com.baifendian.swordfish.webserver.api.dto.AdhocResult;
import com.baifendian.swordfish.webserver.api.dto.ExecutorId;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

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
  public ExecutorId execAdhoc(User operator, String projectName, String stms, int limit, String proxyUser, String queue, String udfs, int timeout, HttpServletResponse response) {

    // 查看用户对项目是否具备相应权限
    Project project = projectMapper.queryByName(projectName);

    if (project == null) {
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      return null;
    }

    // 插入数据库记录, 得到 exec id
    AdHoc adhoc = new AdHoc();
    Date now = new Date();

    adhoc.setProjectId(project.getId());
    adhoc.setOwner(operator.getId());

    // TODO:: 构造 parameter
    // adhoc.setParameter();

    adhoc.setProxyUser(proxyUser);
    adhoc.setQueue(queue);
    adhoc.setStatus(FlowStatus.INIT);
    adhoc.setTimeout(timeout);
    adhoc.setCreateTime(now);

    adHocMapper.insert(adhoc);

    MasterServer masterServer = masterServerMapper.query();
    if(masterServer == null){
      // TODO:: 这个要返回什么代码待确认。
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    MasterClient masterClient = new MasterClient(masterServer.getHost(), masterServer.getPort());
    try {
      RetInfo retInfo = masterClient.execAdHoc(adhoc.getId());
    } catch (Exception e){

    }

    // TODO:: 写 ad_hoc_results db

    // TODO:: 调用 exec-server, 执行即席查询

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
      response.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return null;
    }

    if (!projectService.hasExecPerm(operator.getId(), project)) {
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
