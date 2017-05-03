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
package com.baifendian.swordfish.execserver.service;

import com.baifendian.swordfish.dao.AdHocDao;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.AdHoc;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.execserver.adhoc.AdHocRunnerManager;
import com.baifendian.swordfish.execserver.flow.FlowRunnerManager;
import com.baifendian.swordfish.execserver.utils.ResultHelper;
import com.baifendian.swordfish.rpc.RetInfo;
import com.baifendian.swordfish.rpc.WorkerService.Iface;
import org.apache.commons.configuration.Configuration;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * ExecService 实现 <p>
 */
public class ExecServiceImpl implements Iface {

  /**
   * LOGGER
   */
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  /**
   * {@link FlowDao}
   */
  private final FlowDao flowDao;

  private final AdHocDao adHocDao;

  /**
   * {@link FlowRunnerManager}
   */
  private final FlowRunnerManager flowRunnerManager;

  private final AdHocRunnerManager adHocRunnerManager;

  private String host;

  private int port;

  private Configuration conf;

  /**
   * constructor
   */
  public ExecServiceImpl(String host, int port, Configuration conf) {
    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    this.adHocDao = DaoFactory.getDaoInstance(AdHocDao.class);
    this.flowRunnerManager = new FlowRunnerManager(conf);
    this.adHocRunnerManager = new AdHocRunnerManager(conf);
    this.host = host;
    this.port = port;
    this.conf = conf;
  }

  @Override
  public RetInfo execFlow(int execId) throws TException {
    try {
      // 查询 ExecutionFlow
      ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
      if (executionFlow == null) {
        return ResultHelper.createErrorResult("execId 对应的记录不存在");
      }

      // 更新状态为 RUNNING
      String worker = String.format("%s:%d", host, port);
      flowDao.updateExecutionFlowStatus(execId, FlowStatus.RUNNING, worker);

      // 提交任务运行
      flowRunnerManager.submitFlow(executionFlow);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
    return ResultHelper.SUCCESS;
  }

  @Override
  public RetInfo scheduleExecFlow(int execId, long scheduleDate) throws TException {
    try {
      // 查询 ExecutionFlow
      ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
      if (executionFlow == null) {
        return ResultHelper.createErrorResult("execId 对应的记录不存在");
      }

      // 查询 Schedule
      Schedule schedule = flowDao.querySchedule(executionFlow.getFlowId());
      if (schedule == null) {
        return ResultHelper.createErrorResult("对应的调度信息不存在");
      }

      // 更新状态为 RUNNING
      String worker = String.format("%s:%d", host, port);
      flowDao.updateExecutionFlowStatus(execId, FlowStatus.RUNNING, worker);

      // 提交任务运行
      flowRunnerManager.submitFlow(executionFlow, schedule, new Date(scheduleDate));
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
    return ResultHelper.SUCCESS;
  }

  @Override
  public RetInfo execAdHoc(int adHocId){
    AdHoc adHoc = adHocDao.getAdHoc(adHocId);
    if(adHoc == null){
      LOGGER.error("adhoc id {} not exists", adHocId);
      return ResultHelper.createErrorResult("adhoc id not exists");
    }
    adHocRunnerManager.submitAdHoc(adHoc);
    return ResultHelper.SUCCESS;
  }

  /**
   * 销毁资源 <p>
   */
  public void destory() {
    flowRunnerManager.destroy();
    adHocRunnerManager.destory();
  }

  public RetInfo cancelExecFlow(int execId)  throws TException {
    LOGGER.debug("cancel exec flow {}", execId);
    try {
      // 查询 ExecutionFlow
      ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
      if (executionFlow == null) {
        return ResultHelper.createErrorResult("execId not exists");
      }

      if (executionFlow.getStatus().typeIsFinished()) {
        return ResultHelper.createErrorResult("execId run finished");
      }

      flowRunnerManager.cancelFlow(execId, "master");
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
    return ResultHelper.SUCCESS;

  }
}
