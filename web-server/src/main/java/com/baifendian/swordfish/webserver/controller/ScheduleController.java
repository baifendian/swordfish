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

import com.baifendian.swordfish.dao.enums.*;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.dao.enums.DepPolicyType;
import com.baifendian.swordfish.dao.enums.FailurePolicyType;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.model.User;
import com.baifendian.swordfish.webserver.dto.ScheduleDto;
import com.baifendian.swordfish.webserver.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

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
   * @param response
   * @return
   */
  @PostMapping("/{workflowName}/schedules")
  public ScheduleDto createSchedule(@RequestAttribute(value = "session.user") User operator,
                                    @PathVariable String projectName,
                                    @PathVariable String workflowName,
                                    @RequestParam(value = "schedule") String schedule,
                                    @RequestParam(value = "notifyType",required=false,defaultValue = "None") NotifyType notifyType,
                                    @RequestParam(value = "notifyMails",required=false) String notifyMails,
                                    @RequestParam(value = "maxTryTimes",required=false,defaultValue = "1") int maxTryTimes,
                                    @RequestParam(value = "failurePolicy",required=false,defaultValue = "END") FailurePolicyType failurePolicy,
                                    @RequestParam(value = "depWorkflows",required=false) String  depWorkflows,
                                    @RequestParam(value = "depPolicy",required=false,defaultValue = "NO_DEP_PRE") DepPolicyType depPolicyType,
                                    @RequestParam(value = "timeout",required=false,defaultValue = "18000") int timeout,
                                    HttpServletResponse response){
    logger.info("Operator user {}, exec workflow, project name: {}, workflow name: {}, schedule: {}, notify type: {}, notify mails: {}, max try times: {}," +
                    "failure policy: {}, dep workflows: {}, dep policy: {}, timeout: {}",
            operator.getName(), projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout);
    return new ScheduleDto(scheduleService.createSchedule(operator,projectName,workflowName,schedule,notifyType,notifyMails,maxTryTimes,failurePolicy,depWorkflows,depPolicyType,timeout));
  }

  /**
   * 新增或修改一个调度
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
   * @param response
   * @return
   */
  @PutMapping("/{workflowName}/schedules")
  public ScheduleDto createAndModifySchedule(@RequestAttribute(value = "session.user") User operator,
                                             @PathVariable String projectName,
                                             @PathVariable String workflowName,
                                             @RequestParam(value = "schedule") String schedule,
                                             @RequestParam(value = "notifyType",required=false,defaultValue = "None") NotifyType notifyType,
                                             @RequestParam(value = "notifyMails",required=false) String notifyMails,
                                             @RequestParam(value = "maxTryTimes",required=false,defaultValue = "1") int maxTryTimes,
                                             @RequestParam(value = "failurePolicy",required=false,defaultValue = "END") FailurePolicyType failurePolicy,
                                             @RequestParam(value = "depWorkflows",required=false) String  depWorkflows,
                                             @RequestParam(value = "depPolicy",required=false,defaultValue = "NO_DEP_PRE") DepPolicyType depPolicyType,
                                             @RequestParam(value = "timeout",required=false,defaultValue = "18000") int timeout,
                                             HttpServletResponse response){
    logger.info("Operator user {}, exec workflow, project name: {}, workflow name: {}, schedule: {}, notify type: {}, notify mails: {}, max try times: {}," +
                    "failure policy: {}, dep workflows: {}, dep policy: {}, timeout: {}",
            operator.getName(), projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout);
    return new ScheduleDto(scheduleService.putSchedule(operator,projectName,workflowName,schedule,notifyType,notifyMails,maxTryTimes,failurePolicy,depWorkflows,depPolicyType,timeout));
  }

  /**
   * 修改一个调度
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
   * @param response
   * @return
   */
  @PatchMapping("/{workflowName}/schedules")
  public ScheduleDto patchSchedule(@RequestAttribute(value = "session.user") User operator,
                                   @PathVariable String projectName,
                                   @PathVariable String workflowName,
                                   @RequestParam(value = "schedule") String schedule,
                                   @RequestParam(value = "notifyType",required=false,defaultValue = "None") NotifyType notifyType,
                                   @RequestParam(value = "notifyMails",required=false) String notifyMails,
                                   @RequestParam(value = "maxTryTimes",required=false,defaultValue = "1") int maxTryTimes,
                                   @RequestParam(value = "failurePolicy",required=false,defaultValue = "END") FailurePolicyType failurePolicy,
                                   @RequestParam(value = "depWorkflows",required=false) String  depWorkflows,
                                   @RequestParam(value = "depPolicy",required=false,defaultValue = "NO_DEP_PRE") DepPolicyType depPolicyType,
                                   @RequestParam(value = "timeout",required=false,defaultValue = "18000") int timeout,
                                   HttpServletResponse response){
    logger.info("Operator user {}, exec workflow, project name: {}, workflow name: {}, schedule: {}, notify type: {}, notify mails: {}, max try times: {}," +
                    "failure policy: {}, dep workflows: {}, dep policy: {}, timeout: {}",
            operator.getName(), projectName, workflowName, schedule, notifyType, notifyMails, maxTryTimes, failurePolicy, depWorkflows, depPolicyType, timeout);
    return new ScheduleDto(scheduleService.patchSchedule(operator,projectName,workflowName,schedule,notifyType,notifyMails,maxTryTimes,failurePolicy,depWorkflows,depPolicyType,timeout,null));
  }

  /**
   * 设置一个调度的状态
   * @param operator
   * @param projectName
   * @param workflowName
   * @param response
   */
  @PostMapping("/{workflowName}/schedules/{scheduleStatus}")
  public void postScheduleStatus(@RequestAttribute(value = "session.user") User operator,
                                 @PathVariable String projectName,
                                 @PathVariable String workflowName,
                                 @PathVariable String scheduleStatus,
                                 HttpServletResponse response){
    try {
      scheduleService.postScheduleStatus(operator, projectName, workflowName, scheduleStatus);
    }catch (Exception e){
      logger.error("Post schedule status error",e);
    }
  }

  /**
   * 查询一个工作流的调度
   * @param operator
   * @param projectName
   * @param workflowName
   * @param response
   * @return
   */
  @GetMapping("/{workflowName}/schedules")
  public ScheduleDto querySchedule(@RequestAttribute(value = "session.user") User operator,
                                   @PathVariable String projectName,
                                   @PathVariable String workflowName,
                                   HttpServletResponse response){
    return new ScheduleDto(scheduleService.querySchedule(operator,projectName,workflowName));
  }

  /**
   * 查询一个项目下所有调度
   * @param operator
   * @param projectName
   * @param response
   * @return
   */
  @GetMapping("/schedules")
  public List<ScheduleDto> queryAllSchedule(@RequestAttribute(value = "session.user") User operator,
                                            @PathVariable String projectName,
                                            HttpServletResponse response){
    List<Schedule> scheduleList = scheduleService.queryAllSchedule(operator,projectName);
    List<ScheduleDto> scheduleDtoList = new ArrayList<>();
    for (Schedule schedule:scheduleList){
      scheduleDtoList.add(new ScheduleDto(schedule));
    }

    return scheduleDtoList;
  }
}