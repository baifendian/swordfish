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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

public class MailSendUtil {
  private static Logger LOGGER = LoggerFactory.getLogger(MailSendUtil.class.getName());

  private static String mailProtocol;

  private static String mailServerHost;

  private static Integer mailServerPort;

  private static String mailSender;

  private static String mailPasswd;

  static {
    try {
      File dataSourceFile = ResourceUtils.getFile("classpath:common/mail.properties");
      InputStream is = new FileInputStream(dataSourceFile);

      Properties properties = new Properties();
      properties.load(is);

      mailProtocol = properties.getProperty("mail.protocol");
      mailServerHost = properties.getProperty("mail.server.host");
      mailServerPort = Integer.parseInt(properties.getProperty("mail.server.port"));
      mailSender = properties.getProperty("mail.sender");
      mailPasswd = properties.getProperty("mail.passwd");
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  /**
   * 发送邮件给具体的邮件接收人
   *
   * @param receivers
   * @param title
   * @param content
   * @return
   */
  public static boolean sendMails(Collection<String> receivers, String title, String content) {
    if (receivers == null) {
      LOGGER.error("Mail receivers is null.");
      return false;
    }

    receivers.removeIf((from) -> (StringUtils.isEmpty(from)));

    if (receivers.isEmpty()) {
      LOGGER.error("Mail receivers is empty.");
      return false;
    }

    // 发送 email
    HtmlEmail email = new HtmlEmail();

    try {
      // 这里是 SMTP 发送服务器的名字, 163 的如下："smtp.163.com"
      email.setHostName(mailServerHost);

      // 字符编码集的设置
      email.setCharset("UTF-8");
      // 收件人的邮箱
      for (String receiver : receivers) {
        email.addTo(receiver);
      }
      // 发送人的邮箱
      email.setFrom(mailSender, mailSender);
      // 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码
      email.setAuthentication(mailSender, mailPasswd);
      // 要发送的邮件主题
      email.setSubject(title);
      // 要发送的信息，由于使用了 HtmlEmail，可以在邮件内容中使用 HTML 标签
      email.setMsg(content);
      // 发送
      email.send();

      return true;
    } catch (Throwable e) {
      LOGGER.error("Send email to {} failed", StringUtils.join(",", receivers), e);
    }

    return false;
  }
}
