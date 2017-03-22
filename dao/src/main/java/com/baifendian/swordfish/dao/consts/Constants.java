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
package com.baifendian.swordfish.dao.consts;

import java.util.Locale;

/**
 * 常用的常量 <p>
 */
public class Constants {
  /**
   * "utf-8"
   */
  public static final String UTF_8 = "utf-8";

  /**
   * "yyyy-MM-dd HH:mm:ss"
   */
  public static final String BASE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static final String BASE_TIME_FORMAT = "HH:mm:ss";

  public static final String BASE_DATE_FORMAT = "yyyy-MM-dd";

  /**
   * "GMT+8"
   */
  public static final String DEFAULT_TIME_ZONE = "GMT+8";

    /*
     * public static final String DATA_SOURCE_DB_TYPE_MYSQL = "MYSQL"; public
     * static final String DATA_SOURCE_DB_TYPE_MONGO = "MONGO"; public static
     * final String DATA_SOURCE_PARAMS_SOURCE_ID = "sourceId"; public static
     * final String DATA_SOURCE_PARAMS_ACTION = "action"; public static final
     * String DATA_SOURCE_PARAMS_SESSION_ID = "sessionId"; public static final
     * String DATA_SOURCE_PARAMS_PROJECT_ID = "projectId"; public static final
     * String DATA_SOURCE_PARAMS_TABLE_NAME = "tableName"; public static final
     * String DATA_SOURCE_PARAMS_FIELD_NAME = "fieldName"; public static final
     * String DATA_SOURCE_PARAMS_OWNER_ID = "ownerId"; public static final
     * String DATA_SOURCE_PARAMS_NAME = "name"; public static final String
     * DATA_SOURCE_PARAMS_DESC = "desc"; public static final String
     * DATA_SOURCE_PARAMS_TYPE = "type"; public static final String
     * DATA_SOURCE_PARAMS_PARAMS = "params";
     */

  /**
   * private constructor
   */
  private Constants() {
  }

  public static final String SESSION_ID_NAME = "dw.browser.session.id";

  public static final String SESSION_LANGUAGE = "language";

  public static final Locale DEFAULT_LANGUAGE = new Locale("zh", "CN");

  /**
   * 判断邮箱正则表达式
   **/
  public static final String REGEX_MATCHES_EMAIL = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";

  /**
   * 判断用户名最小2位,最大32位
   **/
  public static final String REGEX_MATCHES_USER_NAME = "^[a-zA-Z]\\S{1,31}$";

  /**
   * 判断手机号码
   **/
  public static final String REGEX_MATCHES_MOBILE = "\\d{11,11}$";

  /**
   * 判断密码是否规范
   **/
  public static final String REGEX_MATCHES_PWD = "^(?![a-z]+$)(?![A-Z]+$)(?!\\d+$)(?![\\W_]+$)\\S+$";

  /**
   * 判断组织code是否合法 字母开头 数字字母 下划线
   **/
  public static final String REGEX_MATCHES_ORG_NAME = "^[a-zA-Z]\\w{1,31}$";
  public static final String REGEX_MATCHES_MAIL_GROUPS = "^[a-zA-Z0-9_.]+@[a-zA-Z0-9_.]+(?:;[a-zA-Z0-9_.]+@[a-zA-Z0-9_.]+)*$";

  /**
   * 常用的名称的正则表达式（字母开头，后续为字母、数字、下划线, 最大32位）
   **/
  public static final String REGEX_MATCHES_COMMON_NAME = "^[a-zA-Z]\\w{0,31}$";

  /**
   * 常用的名称的正则表达式（字母开头，后续为字母、数字、下划线, 最大64位）
   **/
  public static final String REGEX_MATCHES_NODE_NAME = "^[a-zA-Z]\\w{0,63}$";

  /**
   * 资源名称的正则表达式（字母开头，后续为字母、数字、下划线、中划线、.，最小2位,最大42位）
   **/
  public static final String REGEX_MATCHES_RESOURCE_NAME = "^[a-zA-Z][\\w\\.\\-]{1,41}$";

  /**
   * 从字符串中获取ip:port的正则表达式
   */
  public static final String REGEX_MATCHES_IP_PORT = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))):[1-9]\\d*";

  /**
   * 从字符串中抽取资源名称
   */
  public static final String RESOURCE_RULE_MATCHES = "# --@resource_reference\\{([^{}]+)\\}";
}
