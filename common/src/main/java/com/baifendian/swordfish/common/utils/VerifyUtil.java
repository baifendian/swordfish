/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.common.utils;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.consts.Constants;
import org.apache.commons.lang3.StringUtils;
import java.util.regex.Pattern;

/**
 * 校验工具类
 */
public class VerifyUtil {

  /**
   * 正则匹配
   *
   * @param str
   * @param pattern
   * @return
   */
  public static boolean regexMatches(String str, Pattern pattern) {
    if (StringUtils.isEmpty(str)) {
      return false;
    }

    return pattern.matcher(str).matches();
  }

  /**
   * 项目正则是否匹配
   *
   * @param str
   * @return
   */
  public static boolean matcheProjectName(String str) {
    return regexMatches(str, Constants.REGEX_PROJECT_NAME);
  }

  /**
   * 用户名是否匹配
   *
   * @param str
   * @return
   */
  public static boolean matcheUserName(String str) {
    return regexMatches(str, Constants.REGEX_USER_NAME);
  }

  /**
   * 邮箱是否匹配
   *
   * @param str
   * @return
   */
  public static boolean matchEmail(String str) {
    return regexMatches(str, Constants.REGEX_MAIL_NAME) && str.length() <= 64;
  }

  /**
   * 资源名称是否匹配
   *
   * @param str
   * @return
   */
  public static boolean matcheResName(String str) {
    return regexMatches(str, Constants.REGEX_RES_NAME);
  }

  /**
   * 数据源名称是否匹配
   *
   * @param str
   * @return
   */
  public static boolean matcheDatasourceName(String str) {
    return regexMatches(str, Constants.REGEX_WORKFLOW_NAME);
  }

  /**
   * 匹配工作流名称
   *
   * @param str
   * @return
   */
  public static boolean matchWorkflowName(String str) {
    return regexMatches(str, Constants.REGEX_DATASOURCE_NAME);
  }

  /**
   * 检测代理用户是合法
   * @param str
   * @return
   */
  public static boolean matchProxyUser(String str) {
    return BaseConfig.isProhibitUser(str);
  }
}
