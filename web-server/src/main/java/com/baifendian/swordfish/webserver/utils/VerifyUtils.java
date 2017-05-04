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
package com.baifendian.swordfish.webserver.utils;

import com.baifendian.swordfish.common.utils.VerifyUtil;
import com.baifendian.swordfish.webserver.exception.ParameterException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * server校验工具
 */
public class VerifyUtils extends VerifyUtil {


  /**
   * 校验项目名并抛出异常
   *
   * @param projectName
   */
  public static <T extends RuntimeException> void verifyProjectName(String projectName, T e) {
    if (!matcheProjectName(projectName)) {
      throw e;
    }
  }

  /**
   * 校验用户名并抛出异常
   *
   * @param userName
   */
  public static <T extends RuntimeException> void verifyUserName(String userName, T e) {
    if (!matcheUserName(userName)) {
      throw e;
    }
  }

  /**
   * 校验工作流名称
   *
   * @param workflowName
   */
  public static <T extends RuntimeException> void verifyWorkflowName(String workflowName, T e) {
    if (!matchWorkflowName(workflowName)) {
      throw e;
    }
  }

  /**
   * 校验邮箱
   *
   * @param email
   */
  public static <T extends RuntimeException> void verifyEmail(String email, T e) {
    if (!matchEmail(email)) {
      throw e;
    }
  }

  /**
   * 校验资源名称
   *
   * @param resourceName
   */
  public static <T extends RuntimeException> void verifyResName(String resourceName, T e) {
    if (!matcheResName(resourceName)) {
      throw e;
    }
  }

  /**
   * 校验数据源名称
   *
   * @param datasourceName
   */
  public static <T extends RuntimeException> void verifyDatasourceName(String datasourceName, T e) {
    if (!matcheDatasourceName(datasourceName)) {
      throw e;
    }
  }

  /**
   * 校验描述是否符合规范
   *
   * @param desc
   */
  public static <T extends RuntimeException> void verifyDesc(String desc, T e) {
    if (StringUtils.isNotEmpty(desc) && desc.length() > 256) {
      throw e;
    }
  }

  /**
   * 校验代理用户列表是否符合规范
   *
   * @param proxyUserList
   */
  public static <T extends RuntimeException> void verifyProxyUser(List<String> proxyUserList, T e) {
    for (String proxyUser : proxyUserList) {
      verifyProxyUser(proxyUser, e);
    }
  }

  /**
   * 指定代理用户是否符合规范
   *
   * @param proxyUser
   * @param e
   * @param <T>
   */
  public static <T extends RuntimeException> void verifyProxyUser(String proxyUser, T e) {
    if (!matchProxyUser(proxyUser)) {
      throw e;
    }
  }

}
