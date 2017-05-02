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
package com.baifendian.swordfish.common.consts;

import java.util.regex.Pattern;

/**
 * 常用的常量 <p>
 */
public class Constants {
  /**
   * "yyyy-MM-dd HH:mm:ss"
   */
  public static final String BASE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * 判断项目正则表达式
   **/
  public static final Pattern REGEX_PROJECT_NAME = Pattern.compile("[a-zA-Z]\\w{1,63}");

  /**
   * 用户名称正则表达式
   */
  public static final Pattern REGEX_USER_NAME = Pattern.compile("[a-zA-Z]\\w{5,19}");

  /**
   * 邮箱正则表达式
   */
  public static final Pattern REGEX_MAIL_NAME = Pattern.compile("[\\w\\.-]+@[\\w-]+(\\.[\\w-]+)+");

  /**
   * 资源名称正则表达式
   */
  public static final Pattern REGEX_RES_NAME = Pattern.compile("[a-zA-Z]\\w{1,63}");

  /**
   * 数据源名称正则表达式
   */
  public static final Pattern REGEX_DATASOURCE_NAME = Pattern.compile("[a-zA-Z]\\w{1,63}");

  /**
   * 工作流名称正则表达式
   */
  public static final Pattern REGEX_WORKFLOW_NAME = Pattern.compile("[a-zA-Z]\\w{1,63}");

  /**
   * 项目用户写权限
   */
  public static final int PROJECT_USER_PERM_WRITE = 0x04;

  /**
   * 项目用户读权限
   */
  public static final int PROJECT_USER_PERM_READ = 0x02;

  /**
   * 项目用户执行权限
   */
  public static final int PROJECT_USER_PERM_EXEC = 0x01;

  /**
   * 所有权限
   */
  public static final int PROJECT_USER_PERM_ALL = PROJECT_USER_PERM_WRITE | PROJECT_USER_PERM_READ | PROJECT_USER_PERM_EXEC;

}
