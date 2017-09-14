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
package com.baifendian.swordfish.webserver.controller;

import com.baifendian.swordfish.common.consts.Constants;
import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.enums.ScheduleStatus;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.ScheduleDto;
import com.baifendian.swordfish.webserver.exception.BadRequestException;
import com.baifendian.swordfish.webserver.service.ScheduleService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调度设置和管理入口
 */
@RestController
@RequestMapping("/projects/{projectName}/workflows")
public class ScheduleController {

  @Autowired
  private ScheduleService scheduleService;

  private static Logger logger = LoggerFactory.getLogger(ScheduleController.class.getName());

  /**
   * 创建一个调度
   *
   * @param operator
   * @param projectName
   * @param workflowName
   * @param schedule
   * @param notifyType
   * @param maxTryTimes
   * @param failurePolicy
   * @param depWorkflows
   * @param depPolicyType
   * @param timeout
   * @return
   */
  @PostMapping("/{workflowName}/schedules")
  @ResponseStatus(HttpStatus.CREATED)
  public ScheduleDto createSchedule(@RequestAttribute(value = "session.user") User operator,
                                    @PathVariable String projectName,
                                    @PathVariable String workflowName,
                                    @RequestParam(value = "schedule") String schedule,
                                    @RequestParam(value = "notifyType", required = false) NotifyType notifyType,
                                    @RequestParam(value = "notifyMails", required = false) String notifyMails,
                                    @RequestParam(value = "maxTryTimes", required = false, defaultValue = "0") int maxTryTimes,
                                    @RequestParam(value = "failurePolicy", required = false, defaultValue = "END") FailurePolicyType failurePolicy,
                                    @RequestParam(value = "depWorkflows", required = false) String depWorkflows,
                                    @RequestParam(value = "depPolicy", required = false, defaultValue = "NO_DEP_PRE") DepPolicyType depPolicyType,
                                    @RequestParam(value = "timeout", required = false) Integer timeout) {
    logger.info("Operator user {}, exec workflow, project name: {}, workflow name: {}, schedule: {}, notify type: {}, notify mails: {}, max try times: {}," +
                    "failure policy: {}, dep workflows: {}, dep policy: {}, timeout: {}",
            operator.getName(), projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout);

    if (timeout == null) {
      timeout = Constants.TASK_MAX_TIMEOUT;
    }

    // timeout 的限制
    if (timeout <= 0 || timeout > Constants.TASK_MAX_TIMEOUT) {
      throw new BadRequestException(String
          .format("Argument is not valid, timeout must be between (0, %d]",
              Constants.TASK_MAX_TIMEOUT));
    }

    // maxTryTimes 的限制
    if(maxTryTimes < 0 || maxTryTimes > 2) {
      throw new BadRequestException("Argument is not valid, max try times must be between [0, 2]");
    }

    return new ScheduleDto(scheduleService.createSchedule(operator, projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout));
  }

  /**
   * 新增或修改一个调度
   *
   * @param operator
   * @param projectName
   * @param workflowName
   * @param schedule
   * @param notifyType
   * @param maxTryTimes
   * @param failurePolicy
   * @param depWorkflows
   * @param depPolicyType
   * @param timeout
   * @return
   */
  @PutMapping("/{workflowName}/schedules")
  public ScheduleDto createAndModifySchedule(@RequestAttribute(value = "session.user") User operator,
                                             @PathVariable String projectName,
                                             @PathVariable String workflowName,
                                             @RequestParam(value = "schedule") String schedule,
                                             @RequestParam(value = "notifyType", required = false, defaultValue = "None") NotifyType notifyType,
                                             @RequestParam(value = "notifyMails", required = false) String notifyMails,
                                             @RequestParam(value = "maxTryTimes", required = false, defaultValue = "0") int maxTryTimes,
                                             @RequestParam(value = "failurePolicy", required = false, defaultValue = "END") FailurePolicyType failurePolicy,
                                             @RequestParam(value = "depWorkflows", required = false) String depWorkflows,
                                             @RequestParam(value = "depPolicy", required = false, defaultValue = "NO_DEP_PRE") DepPolicyType depPolicyType,
                                             @RequestParam(value = "timeout", required = false) Integer timeout) {
    logger.info("Operator user {}, exec workflow, project name: {}, workflow name: {}, schedule: {}, notify type: {}, notify mails: {}, max try times: {}," +
                    "failure policy: {}, dep workflows: {}, dep policy: {}, timeout: {}",
            operator.getName(), projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout);

    if (timeout == null) {
      timeout = Constants.TASK_MAX_TIMEOUT;
    }

    // timeout 的限制
    if (timeout <= 0 || timeout > Constants.TASK_MAX_TIMEOUT) {
      throw new BadRequestException(String
          .format("Argument is not valid, timeout must be between (0, %d]",
              Constants.TASK_MAX_TIMEOUT));
    }

    // maxTryTimes 的限制
    if(maxTryTimes < 0 || maxTryTimes > 2) {
      throw new BadRequestException("Argument is not valid, max try times must be between [0, 2]");
    }

    return new ScheduleDto(scheduleService.putSchedule(operator, projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout));
  }

  /**
   * 修改一个调度
   *
   * @param operator
   * @param projectName
   * @param workflowName
   * @param schedule
   * @param notifyType
   * @param maxTryTimes
   * @param failurePolicy
   * @param depWorkflows
   * @param depPolicyType
   * @param timeout
   * @return
   */
  @PatchMapping("/{workflowName}/schedules")
  public ScheduleDto patchSchedule(@RequestAttribute(value = "session.user") User operator,
                                   @PathVariable String projectName,
                                   @PathVariable String workflowName,
                                   @RequestParam(value = "schedule") String schedule,
                                   @RequestParam(value = "notifyType", required = false, defaultValue = "None") NotifyType notifyType,
                                   @RequestParam(value = "notifyMails", required = false) String notifyMails,
                                   @RequestParam(value = "maxTryTimes", required = false, defaultValue = "0") int maxTryTimes,
                                   @RequestParam(value = "failurePolicy", required = false, defaultValue = "END") FailurePolicyType failurePolicy,
                                   @RequestParam(value = "depWorkflows", required = false) String depWorkflows,
                                   @RequestParam(value = "depPolicy", required = false, defaultValue = "NO_DEP_PRE") DepPolicyType depPolicyType,
                                   @RequestParam(value = "timeout", required = false) Integer timeout) {
    logger.info("Operator user {}, exec workflow, project name: {}, workflow name: {}, schedule: {}, notify type: {}, notify mails: {}, max try times: {}," +
                    "failure policy: {}, dep workflows: {}, dep policy: {}, timeout: {}",
            operator.getName(), projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout);

    if (timeout == null) {
      timeout = Constants.TASK_MAX_TIMEOUT;
    }

    // timeout 的限制
    if (timeout <= 0 || timeout > Constants.TASK_MAX_TIMEOUT) {
      throw new BadRequestException(String
          .format("Argument is not valid, timeout must be between (0, %d]",
              Constants.TASK_MAX_TIMEOUT));
    }

    // maxTryTimes 的限制
    if(maxTryTimes < 0 || maxTryTimes > 2) {
      throw new BadRequestException("Argument is not valid, max try times must be between [0, 2]");
    }

    return new ScheduleDto(scheduleService.patchSchedule(operator, projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout, null));
  }

  /**
   * 下线一个调度
   *
   * @param operator
   * @param projectName
   * @param workflowName
   */
  @PostMapping("/{workflowName}/schedules/online")
  public void scheduleOnline(@RequestAttribute(value = "session.user") User operator,
                             @PathVariable("projectName") String projectName,
                             @PathVariable("workflowName") String workflowName) throws Exception {
    logger.info("Operator user {}, schedule online, project name: {}, workflow name: {}",
            operator.getName(), projectName, workflowName);

    try {
      scheduleService.postScheduleStatus(operator, projectName, workflowName, ScheduleStatus.ONLINE);
    } catch (Exception e) {
      logger.error("Schedule online error", e);
      throw e;
    }
  }

  /**
   * 下线一个调度
   *
   * @param operator
   * @param projectName
   * @param workflowName
   */
  @PostMapping("/{workflowName}/schedules/offline")
  public void scheduleOffline(@RequestAttribute(value = "session.user") User operator,
                              @PathVariable("projectName") String projectName,
                              @PathVariable("workflowName") String workflowName) throws Exception {
    logger.info("Operator user {}, schedule offline, project name: {}, workflow name: {}",
            operator.getName(), projectName, workflowName);

    try {
      scheduleService.postScheduleStatus(operator, projectName, workflowName, ScheduleStatus.OFFLINE);
    } catch (Exception e) {
      logger.error("Schedule offline error", e);
      throw e;
    }
  }

  /**
   * 查询一个工作流的调度
   *
   * @param operator
   * @param projectName
   * @param workflowName
   * @return
   */
  @GetMapping("/{workflowName}/schedules")
  public ScheduleDto querySchedule(@RequestAttribute(value = "session.user") User operator,
                                   @PathVariable String projectName,
                                   @PathVariable String workflowName) {
    logger.info("Operator user {}, query schedule, project name: {}, workflow name: {}",
            operator.getName(), projectName, workflowName);

    return new ScheduleDto(scheduleService.querySchedule(operator, projectName, workflowName));
  }

  /**
   * 查询一个项目下所有调度
   *
   * @param operator
   * @param projectName
   * @return
   */
  @GetMapping("/schedules")
  public List<ScheduleDto> queryAllSchedule(@RequestAttribute(value = "session.user") User operator,
                                            @PathVariable String projectName) {
    logger.info("Operator user {}, query schedules, project name: {}",
            operator.getName(), projectName);

    List<Schedule> scheduleList = scheduleService.queryAllSchedule(operator, projectName);
    List<ScheduleDto> scheduleDtoList = new ArrayList<>();

    for (Schedule schedule : scheduleList) {
      scheduleDtoList.add(new ScheduleDto(schedule));
    }

    return scheduleDtoList;
  }
}
