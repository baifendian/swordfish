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

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.BaseParamFactory;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.webserver.exception.BadRequestException;
import com.baifendian.swordfish.webserver.exception.NotFoundException;
import com.baifendian.swordfish.webserver.exception.ParameterException;
import com.baifendian.swordfish.webserver.exception.ServerErrorException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

import static com.baifendian.swordfish.common.utils.VerifyUtil.*;

/**
 * server校验工具
 */
public class ParamVerify {

  /**
   * 校验一个对象是否存在并抛出异常
   *
   * @param object
   * @param msg
   * @param parameter
   * @param <T>
   * @return
   */
  public static <T> void notNull(T object, String msg, Object... parameter) {
    if (object == null) {
      throw new NotFoundException(msg, parameter);
    }
  }

  /**
   * 校验项目名并抛出异常
   *
   * @param projectName
   */
  public static void verifyProjectName(String projectName) {
    if (!matcheProjectName(projectName)) {
      throw new ParameterException("Project name \"{0}\" not valid", projectName);
    }
  }

  /**
   * 校验用户名并抛出异常
   *
   * @param userName
   */
  public static void verifyUserName(String userName) {
    if (!matcheUserName(userName)) {
      throw new ParameterException("User name \"{0}\" not valid", userName);
    }
  }

  /**
   * 校验工作流名称
   *
   * @param workflowName
   */
  public static void verifyWorkflowName(String workflowName) {
    if (!matchWorkflowName(workflowName)) {
      throw new ParameterException("Workflow name \"{0}\" not valid", workflowName);
    }
  }

  /**
   * 校验流任务名称
   *
   * @param streamingName
   */
  public static void verifyStreamingName(String streamingName) {
    // 这里和工作流名称一致
    if (!matchWorkflowName(streamingName)) {
      throw new ParameterException("StreamingJob name \"{0}\" not valid", streamingName);
    }
  }

  /**
   * 校验邮箱
   *
   * @param email
   */
  public static void verifyEmail(String email) {
    if (!matchEmail(email)) {
      throw new ParameterException("Email name \"{0}\" not valid", email);
    }
  }

  /**
   * 检测 email 列表
   *
   * @param emails
   */
  public static void verifyEmails(String emails, boolean mustExist) {
    try {
      List<String> emailList = JsonUtil.parseObjectList(emails, String.class);
      if (mustExist && emailList == null) {
        throw new ParameterException("Extra parameter \"{0}\" not valid, emails must set correct", emails);
      }
    } catch (Exception e) {
      throw new ParameterException("Extra parameter \"{0}\" not valid", emails);
    }
  }

  /**
   * 校验资源名称
   *
   * @param resourceName
   */
  public static void verifyResName(String resourceName) {
    if (!matcheResName(resourceName)) {
      throw new ParameterException("Resource name \"{0}\" not valid", resourceName);
    }
  }

  /**
   * 校验数据源名称
   *
   * @param datasourceName
   */
  public static void verifyDatasourceName(String datasourceName) {
    if (!matcheDatasourceName(datasourceName)) {
      throw new ParameterException("Datasource name \"{0}\" not valid", datasourceName);
    }
  }

  /**
   * 校验描述是否符合规范
   *
   * @param desc
   */
  public static void verifyDesc(String desc) {
    if (StringUtils.isNotEmpty(desc) && desc.length() > 256) {
      throw new ParameterException("Desc name \"{0}\" not valid", desc);
    }
  }

  /**
   * 校验额外参数是否符合规范
   *
   * @param extras
   */
  public static void verifyExtras(String extras) {
    if (extras != null && !JsonUtil.isJsonNode(extras)) {
      throw new ParameterException("Extra parameter \"{0}\" not valid", extras);
    }
  }

  /**
   * 校验密码是否符合规范
   *
   * @param password
   */
  public static void verifyPassword(String password) {
    if (StringUtils.isEmpty(password) || password.length() < 2 || password.length() > 20) {
      throw new ParameterException("Password \"{0}\" not valid", password);
    }
  }

  /**
   * 校验电话号码是否符合规范
   *
   * @param phone
   */
  public static void verifyPhone(String phone) {
    if (StringUtils.isNotEmpty(phone) && phone.length() > 18) {
      throw new ParameterException("Phone number \"{0}\" not valid", phone);
    }
  }

  /**
   * 判断代理用户是否合法
   *
   * @param proxyUserList
   * @param proxyUser
   */
  public static void verifyProxyUser(List<String> proxyUserList, String proxyUser) {
    if (StringUtils.isEmpty(proxyUser)) {
      throw new BadRequestException("Proxy user \"{0}\" is empty", proxyUser);
    }

    if (CollectionUtils.isEmpty(proxyUserList)) {
      throw new ServerErrorException("Proxy user list of the operator is empty.");
    }

    if (BaseConfig.isProhibitUser(proxyUser)) {
      throw new BadRequestException("Proxy user \"{0}\" not allowed", proxyUser);
    }

    // 如果不是代理所有用户, 且不包含代理的用户
    if (!proxyUserList.get(0).equals("*") && !proxyUserList.contains(proxyUser)) {
      throw new BadRequestException("Proxy user \"{0}\" not allowed", proxyUser);
    }
  }

  /**
   * 检测 node parameter 格式是否正常
   *
   * @param parameter
   * @param type
   * @return
   */
  public static boolean flowNodeParamCheck(String parameter, String type) {
    BaseParam baseParam = BaseParamFactory.getBaseParam(type, parameter);

    if (baseParam == null) {
      return false;
    }

    return baseParam.checkValid();
  }
}
