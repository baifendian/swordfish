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

/**
 * server校验工具
 */
public class VerifyUtils extends VerifyUtil {
  /**
   * 校验项目名并抛出异常
   *
   * @param projectName
   */
  public static void verifyProjectName(String projectName) {
    if (!matcheProjectName(projectName)) {
      throw new ParameterException("project name");
    }
  }

  /**
   * 校验用户名并抛出异常
   *
   * @param userName
   */
  public static void verifyUserName(String userName) {
    if (!matcheUserName(userName)) {
      throw new ParameterException("user name");
    }
  }

  /**
   * 校验工作流名称
   *
   * @param workflowName
   */
  public static void verifyWorkflowName(String workflowName) {
    if (!matchWorkflowName(workflowName)) {
      throw new ParameterException("workflow name");
    }
  }

  /**
   * 校验邮箱
   *
   * @param email
   */
  public static void verifyEmail(String email) {
    if (!matchEmail(email)) {
      throw new ParameterException("email");
    }
  }

  /**
   * 校验资源名称
   *
   * @param resourceName
   */
  public static void verifyResName(String resourceName) {
    if (!matcheResName(resourceName)) {
      throw new ParameterException("resource name");
    }
  }

  /**
   * 校验数据源名称
   *
   * @param datasourceName
   */
  public static void verifyDatasourceName(String datasourceName) {
    if (!matcheDatasourceName(datasourceName)) {
      throw new ParameterException("datasource name");
    }
  }

  /**
   * 校验描述是否付汇规范
   *
   * @param desc
   */
  public static void verifyDesc(String desc) {
    if (StringUtils.isNotEmpty(desc) && desc.length() > 256) {
      throw new ParameterException("desc");
    }
  }
}
