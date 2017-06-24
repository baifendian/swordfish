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
package com.baifendian.swordfish.common.mail;

import com.baifendian.swordfish.common.utils.DateUtils;
import com.baifendian.swordfish.dao.enums.ExecType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.enums.NotifyType;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.StreamingResult;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * 邮件内容管理 <p>
 */
public class EmailManager {

  /**
   * 邮件标题格式
   */
  private static final String TITLE_FORMAT = "[{0}]Swordfish system notify, {1}";

  /**
   * 获取邮件任务
   */
  private static final String CONTENT_FORMAT = "<b>{0}</b>" +
      "<hr/>Project name：{1}<br/>" +
      "Job name：{2}<br/>" +
      "Proxy user：{3}<br/>" +
      "Queue：{4}<br/>" +
      "Schedule time: {5}<br/>" +
      "Start execution time：{6}<br/>" +
      "End execution time：{7}<br/>" +
      "Final status：{8}<br/>";

  /**
   * 发送 EMAIL(调度)
   */
  public static void sendEmail(String type,
      FlowStatus status,
      String projectName,
      String jobName,
      String proxyUser,
      String queue,
      Date scheduleTime,
      Date startTime,
      Date endTime,
      List<String> receivers) {
    // 得到标题
    String title = MessageFormat.format(TITLE_FORMAT, getFlowStatusCnName(status), type);

    String content = MessageFormat.format(CONTENT_FORMAT, type, projectName, jobName,
        proxyUser, queue, DateUtils.defaultFormat(scheduleTime), DateUtils.defaultFormat(startTime),
        DateUtils.defaultFormat(endTime), getFlowStatusCnNameH5(status));

    MailSendUtil.sendMails(receivers, title, content);
  }

  /**
   * 获取执行类型的描述 <p>
   */
  private static String getRunTypeCnName(ExecType runType) {

    switch (runType) {
      case COMPLEMENT_DATA:
        return "add data workflow job";

      case DIRECT:
        return "direct run workflow job";

      case SCHEDULER:
        return "schedule workflow job";

      default:
    }

    return "unknown job";
  }

  /**
   * 获取执行状态的描述 <p>
   */
  private static String getFlowStatusCnName(FlowStatus status) {
    if (status.typeIsFailure()) {
      return "Failed";
    }

    if (status.typeIsSuccess()) {
      return "Success";
    }

    return "Not finish";
  }

  /**
   * 获取执行状态的描述 <p>
   */
  private static String getFlowStatusCnNameH5(FlowStatus status) {
    if (status.typeIsFailure()) {
      return "<font color=\"red\">Failed</font>";
    }

    if (status.typeIsSuccess()) {
      return "<font color=\"green\">Success</font>";
    }

    return "<font color=\"red\">Not finish</font>";
  }


  /**
   * 发送工作流的 mail 信息
   */
  public static void sendMessageOfExecutionFlow(ExecutionFlow executionFlow) {
    NotifyType notifyType = executionFlow.getNotifyType();

    boolean sendMail = false;

    switch (notifyType) {
      case SUCCESS:
        if (executionFlow.getStatus().typeIsSuccess()) {
          sendMail = true;
        }
      case FAILURE:
        if (executionFlow.getStatus().typeIsFailure()) {
          sendMail = true;
        }
      case ALL:
        if (executionFlow.getStatus().typeIsFinished()) {
          sendMail = true;
        }
      default:
    }

    if (sendMail) {
      sendEmail(getRunTypeCnName(executionFlow.getType()),
          executionFlow.getStatus(),
          executionFlow.getProjectName(),
          executionFlow.getWorkflowName(),
          executionFlow.getProxyUser(),
          executionFlow.getQueue(),
          executionFlow.getScheduleTime(),
          executionFlow.getStartTime(),
          executionFlow.getEndTime(),
          executionFlow.getNotifyMailList());
    }
  }

  /**
   * 发送流任务的报警
   */
  public static void sendMessageOfStreamingJob(StreamingResult streamingResult) {
    NotifyType notifyType = streamingResult.getNotifyType();

    boolean sendMail = false;

    switch (notifyType) {
      case SUCCESS:
        if (streamingResult.getStatus().typeIsSuccess()) {
          sendMail = true;
        }
      case FAILURE:
        if (streamingResult.getStatus().typeIsFailure()) {
          sendMail = true;
        }
      case ALL:
        if (streamingResult.getStatus().typeIsFailure()) {
          sendMail = true;
        }
      default:
    }

    if (sendMail) {
      sendEmail("streaming job",
          streamingResult.getStatus(),
          streamingResult.getProjectName(),
          streamingResult.getName(),
          streamingResult.getProxyUser(),
          streamingResult.getQueue(),
          streamingResult.getScheduleTime(),
          streamingResult.getStartTime(),
          streamingResult.getEndTime(),
          streamingResult.getNotifyMailList());
    }
  }
}