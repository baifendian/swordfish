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

import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.FlowDao;
import com.baifendian.swordfish.common.job.FlowStatus;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.execserver.flow.FlowRunnerManager;
import com.baifendian.swordfish.execserver.result.ResultHelper;
import com.baifendian.swordfish.rpc.WorkerService.Iface;
import com.baifendian.swordfish.rpc.RetInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Date;

/**
 * ExecService 实现 <p>
 *
 * @author : dsfan
 * @date : 2016年10月25日
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

  /**
   * {@link FlowRunnerManager}
   */
  private final FlowRunnerManager flowRunnerManager;

  private String host;

  private int port;

  /**
   * constructor
   */
  public ExecServiceImpl(String host, int port) {
    this.flowDao = DaoFactory.getDaoInstance(FlowDao.class);
    this.flowRunnerManager = new FlowRunnerManager();
    this.host = host;
    this.port = port;
  }

  @Override
  public RetInfo execFlow(int projectId, long execId, String flowType) throws TException {
    if (StringUtils.isEmpty(flowType)) {
      return ResultHelper.createErrorResult("flowType 参数不能为空");
    }

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
  public RetInfo scheduleExecFlow(int projectId, long execId, String flowType, long scheduleDate) throws TException {
    if (StringUtils.isEmpty(flowType)) {
      return ResultHelper.createErrorResult("flowType 参数不能为空");
    }

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

  /**
   * 销毁资源 <p>
   */
  public void destory() {
    flowRunnerManager.destroy();
  }

  public RetInfo cancelExecFlow(int projectId, long execId, String flowType) throws TException {
    try {
      // 查询 ExecutionFlow
      ExecutionFlow executionFlow = flowDao.queryExecutionFlow(execId);
      if (executionFlow == null) {
        return ResultHelper.createErrorResult("execId 对应的记录不存在");
      }

      if (executionFlow.getStatus().typeIsFinished()) {
        return ResultHelper.createErrorResult("execId run finished");
      }

      flowRunnerManager.cancelFlow(execId, "user");
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return ResultHelper.createErrorResult(e.getMessage());
    }
    return ResultHelper.SUCCESS;

  }
}
