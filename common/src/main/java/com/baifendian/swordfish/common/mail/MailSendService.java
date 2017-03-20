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

import com.baifendian.swordfish.dao.BaseDao;
import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.baifendian.swordfish.dao.mapper.ProjectFlowMapper;
import com.baifendian.swordfish.dao.mapper.ProjectMapper;
import com.baifendian.swordfish.dao.mapper.ProjectUserMapper;
import com.baifendian.swordfish.dao.mapper.UserMapper;
import com.baifendian.swordfish.dao.model.ProjectFlow;
import com.baifendian.swordfish.dao.model.Schedule;
import com.baifendian.swordfish.dao.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class MailSendService extends BaseDao {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private ProjectFlowMapper projectFlowMapper;

  @Autowired
  private ProjectUserMapper projectUserMapper;

  @Autowired
  private ProjectMapper projectMapper;

  @Override
  protected void init() {
    userMapper = ConnectionFactory.getSqlSession().getMapper(UserMapper.class);
    projectUserMapper = ConnectionFactory.getSqlSession().getMapper(ProjectUserMapper.class);
    projectFlowMapper = ConnectionFactory.getSqlSession().getMapper(ProjectFlowMapper.class);
    projectMapper = ConnectionFactory.getSqlSession().getMapper(ProjectMapper.class);
  }

  /**
   * 发送邮件给个人
   *
   * @param receiverUserId 收件人的id
   * @param title          邮件主题
   * @param content        邮件内容(支持HTML)
   * @return
   */
  /*
  public boolean sendToUser(int receiverUserId, String title, String content) {
    User user = userMapper.queryById(receiverUserId);
    if (user == null) {
      LOGGER.error("Not find user: {}", receiverUserId);
      return false;
    }

    List receivers = new ArrayList<>();

    receivers.add(user.getEmail());

    return MailSendUtil.sendMails(receivers, title, content);
  }
  */

  /**
   * 发送邮件给项目所有成员
   *
   * @param projectId 项目的id
   * @param title     邮件主题
   * @param content   邮件内容(支持HTML)
   */
  public boolean sendToProjectUsers(int projectId, String title, String content) {
    List<User> users = projectUserMapper.queryForUser(projectId);

    if (users == null) {
      LOGGER.error("Not find project: {}", projectId);
      return false;
    }

    List receivers = new ArrayList<>();

    for (User user : users) {
      receivers.add(user.getEmail());
    }

    return MailSendUtil.sendMails(receivers, title, content);
  }

  /**
   * 若workflow无邮件组，且指定sendToUserIfMailsEmpty为true时，则发送邮件给项目组成员
   *
   * @param flowId                 flowid
   * @param title                  邮件主题
   * @param content                邮件内容
   * @param sendToUserIfMailsEmpty 标志位，若项目无邮件组发送邮件给项目成员
   */
  public boolean sendToFlowMails(int flowId, String title, String content, boolean sendToUserIfMailsEmpty, Schedule schedule) {
    String mailsStr = schedule.getNotifyEmails();
    if (mailsStr == null || mailsStr == "") {
      System.out.println(sendToUserIfMailsEmpty);
      if (sendToUserIfMailsEmpty) {
        return sendToProjectUsers(flowId, title, content);
      } else {
        return false;
      }
    }
    String[] mailsArr = mailsStr.split(";");
    Collection<String> mails = Arrays.asList(mailsArr);

    return MailSendUtil.sendMails(mails, title, content);
  }
}
