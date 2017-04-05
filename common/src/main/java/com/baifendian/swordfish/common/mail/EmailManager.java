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
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.enums.FlowRunType;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ExecutionNode;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 邮件内容管理 <p>
 */
public class EmailManager {

  private static final Logger logger = LoggerFactory.getLogger(EmailManager.class);

  /**
   * {@link MailSendService}
   */
  private static MailSendService mailSendService;

  static {
    mailSendService = DaoFactory.getDaoInstance(MailSendService.class);
  }

  /**
   * 邮件标题格式
   */
  private static final String TITLE_FORMAT = "[Schedule system] [{0} [{1}]";

  /**
   * 获取邮件任务
   */
  private static final String CONTENT_FORMAT = "<b>{0}</b><hr/>Project：{1}<br/>workflow name：{2}<br/>schedule time：{3}<br/>execution time：{4}<br/><br/><I>Note：execution detail see [maintain center] - [schedule logs]</I>";


  private static final String CONTENT_NODE_FORMAT = "<br>Long job Node:{0} RUN ERROR";

  /**
   * 补数据内容头部
   */
  private static final String ADD_DATA_HEAD_FORMAT = "<b>{0}</b><hr/>Project：{1}<br/>workflow name：{2}<br/><br/><b>Add data detail</b>";

  /**
   * 补数据的每个元素内容
   */
  private static final String ADD_DATA_ITEM_FORMAT = "<hr style=\"border:1px dotted #036\" />Schedule time：{0}<br/>Execution result：{1}";

  /**
   * 补数据的结尾
   */
  private static final String ADD_DATA_TAIL_FORMAT = "<br/><br/><I>Note：execution detail see [Maintain center】- [Schedule log]</I>";

  /**
   * 发送 EMAIL(调度)
   *
   * @param executionFlow
   * @param schedule
   */
  public static void sendEmail(ExecutionFlow executionFlow, Schedule schedule) {
    String title = genTitle(executionFlow.getType(), executionFlow.getStatus());
    String content = genContent(executionFlow.getType(), executionFlow.getProjectName(), executionFlow.getFlowName(),
        executionFlow.getScheduleTime(), executionFlow.getStatus());

    mailSendService.sendToFlowMails(executionFlow.getFlowId(), title, content, true, schedule);
  }

  /**
   * 长任务，如果 node 报错，就发邮件通知
   *
   * @param executionFlow
   * @param executionNode
   */
  public static void sendEmail(ExecutionFlow executionFlow, ExecutionNode executionNode) {
    try {
      String title = genTitle(executionFlow.getType(), executionNode.getStatus());
      String content = genContent(executionFlow.getType(), executionFlow.getProjectName(), executionFlow.getFlowName(),
          executionFlow.getScheduleTime(), executionNode.getStatus());

      content += MessageFormat.format(CONTENT_NODE_FORMAT, executionNode.getName());
      mailSendService.sendToFlowUserMails(executionFlow.getFlowId(), title, content);
    } catch (Exception e) {
      logger.error("send mail error", e);
    }
  }

  /**
   * 发送 EMAIL(补数据)
   *
   * @param projectFlow
   * @param isSuccess
   * @param resultList
   */
  public static void sendAddDataEmail(ProjectFlow projectFlow, boolean isSuccess, List<Map.Entry<Date, Boolean>> resultList) {
    String title = MessageFormat.format(TITLE_FORMAT, "Add data", isSuccess ? "Success" : "Failed");
    StringBuilder builder = new StringBuilder();
    String head = MessageFormat.format(ADD_DATA_HEAD_FORMAT, "Add data", projectFlow.getProjectName(), projectFlow.getName());
    builder.append(head);

    for (Map.Entry<Date, Boolean> entry : resultList) {
      String item = MessageFormat.format(ADD_DATA_ITEM_FORMAT, DateUtils.defaultFormat(entry.getKey()), getResultStatus(entry.getValue()));
      builder.append(item);
    }

    builder.append(ADD_DATA_TAIL_FORMAT);
    mailSendService.sendToFlowMails(projectFlow.getProjectId(), title, builder.toString(), true, null);
  }

  /**
   * 获取结果状态字符串
   *
   * @param isSuccess
   * @return
   */
  private static String getResultStatus(Boolean isSuccess) {
    if (isSuccess == null) {
      return "Not started";
    }

    return isSuccess ? "<font color=\"green\">Success</font>" : "<font color=\"red\">Failed</font>";
  }

  /**
   * 获取邮件标题
   *
   * @param runType
   * @param flowStatus
   * @return
   */
  public static String genTitle(FlowRunType runType, FlowStatus flowStatus) {
    return MessageFormat.format(TITLE_FORMAT, getRunTypeCnName(runType), getFlowStatusCnName(flowStatus));
  }

  /**
   * 生成邮件内容
   *
   * @param runType
   * @param projectName
   * @param flowName
   * @param scheduleDate
   * @param flowStatus
   * @return
   */
  public static String genContent(FlowRunType runType, String projectName, String flowName, Date scheduleDate, FlowStatus flowStatus) {
    return MessageFormat.format(CONTENT_FORMAT, getRunTypeCnName(runType), projectName, flowName,
        DateUtils.defaultFormat(scheduleDate), getFlowStatusCnNameH5(flowStatus));
  }

  /**
   * 获取执行类型的描述 <p>
   *
   * @param runType
   * @return
   */
  private static String getRunTypeCnName(FlowRunType runType) {
    String cnName;

    switch (runType) {
      case ADD_DATA:
        cnName = "Add data";
        break;

      case DIRECT_RUN:
        cnName = "Direct run";
        break;

      case DISPATCH:
        cnName = "Schedule";
        break;

      case STREAMING:
        cnName = "Streaming task";
        break;

      default:
        cnName = "Unknown";
    }

    return cnName;
  }

  /**
   * 获取执行状态的描述 <p>
   *
   * @param status
   * @return
   */
  private static String getFlowStatusCnName(FlowStatus status) {
    if (status.typeIsFailure()) {
      return "Failed";
    }

    return "Success";
  }

  /**
   * 获取执行状态的描述 <p>
   *
   * @param status
   * @return
   */
  private static String getFlowStatusCnNameH5(FlowStatus status) {
    if (status.typeIsFailure()) {
      return "<font color=\"red\">Failed</font>";
    }

    return "<font color=\"green\">Success</font>";
  }
}
