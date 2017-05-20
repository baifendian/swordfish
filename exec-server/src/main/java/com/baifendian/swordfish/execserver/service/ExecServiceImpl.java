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
import com.baifendian.swordfish.execserver.runner.adhoc.AdHocRunnerManager;
import com.baifendian.swordfish.execserver.runner.flow.FlowRunnerManager;
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

  private static Logger logger = LoggerFactory.getLogger(ExecServiceImpl.class);

  /**
   * {@link FlowDao}
   */
  private final FlowDao flowDao;

  /**
   * {@link AdHocDao}
   */
  private final AdHocDao adHocDao;

  /**
   * {@link FlowRunnerManager}
   */
  private final FlowRunnerManager flowRunnerManager;

  /**
   * {@link AdHocRunnerManager}
   */
  private final AdHocRunnerManager adHocRunnerManager;

  /**
   * 当前 executor 的 host
   */
  private String host;

  /**
   * 当前 executor 的 port
   */
  private int port;

  public ExecServiceImpl(String host, int port, Configuration conf) {
    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    this.adHocDao = DaoFactory.getDaoInstance(AdHocDao.class);
    this.flowRunnerManager = new FlowRunnerManager(conf);
    this.adHocRunnerManager = new AdHocRunnerManager(conf);

    this.host = host;
    this.port = port;
  }

  /**
   * 执行指定的工作流
   *
   * @param execId
   * @return
   * @throws TException
   */
  @Override
  public RetInfo execFlow(int execId) throws TException {
    try {
      // 查询 ExecutionFlow
      ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
      if (executionFlow == null) {
        return ResultHelper.createErrorResult("execId not find");
      }

      // 更新状态为 RUNNING
      String worker = String.format("%s:%d", host, port);
      flowDao.updateExecutionFlowStatus(execId, FlowStatus.RUNNING, worker);

      // 提交任务运行
      flowRunnerManager.submitFlow(executionFlow);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 调度执行一个工作流
   *
   * @param execId
   * @param scheduleDate
   * @return
   * @throws TException
   */
  @Override
  public RetInfo scheduleExecFlow(int execId, long scheduleDate) throws TException {
    try {
      // 查询 ExecutionFlow
      ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
      if (executionFlow == null) {
        return ResultHelper.createErrorResult("execId not find");
      }

      // 查询 Schedule
      Schedule schedule = flowDao.querySchedule(executionFlow.getFlowId());
      if (schedule == null) {
        return ResultHelper.createErrorResult("schedule information not find");
      }

      // 更新状态为 RUNNING
      String worker = String.format("%s:%d", host, port);
      flowDao.updateExecutionFlowStatus(execId, FlowStatus.RUNNING, worker);

      // 提交任务运行
      flowRunnerManager.submitFlow(executionFlow, schedule);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 执行即席查询
   *
   * @param adHocId
   * @return
   */
  @Override
  public RetInfo execAdHoc(int adHocId) {
    logger.info("exec ad hoc: {}", adHocId);

    AdHoc adHoc = adHocDao.getAdHoc(adHocId);

    if (adHoc == null) {
      logger.error("ad hoc id {} not exists", adHocId);
      return ResultHelper.createErrorResult("ad hoc id not exists");
    }

    if (adHoc.getStatus().typeIsFinished()) {
      logger.error("ad hoc id {} finished unexpected", adHocId);
      return ResultHelper.createErrorResult("task finished unexpected");
    }

    adHocRunnerManager.submitAdHoc(adHoc);
    return ResultHelper.SUCCESS;
  }

  /**
   * 取消工作流的执行
   *
   * @param execId
   * @return
   * @throws TException
   */
  public RetInfo cancelExecFlow(int execId) throws TException {
    logger.info("cancel exec flow {}", execId);

    try {
      // 查询 ExecutionFlow
      ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
      if (executionFlow == null) {
        return ResultHelper.createErrorResult("execId not exists");
      }

      if (executionFlow.getStatus().typeIsFinished()) {
        return ResultHelper.createErrorResult("execId run finished");
      }

      flowRunnerManager.cancelFlow(execId);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }

    return ResultHelper.SUCCESS;
  }

  /**
   * 销毁资源 <p>
   */
  public void destory() {
    flowRunnerManager.destroy();
    adHocRunnerManager.destory();
  }
}
