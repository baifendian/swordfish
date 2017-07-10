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
package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.job.struct.node.impexp.column.HiveColumn;
import com.baifendian.swordfish.execserver.job.impexp.Args.HqlColumn;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 导入导出工具类诶
 */
public class ImpExpUtil {

  /**
   * 去除一个字符串头尾的反引号
   */
  public static String exceptBackQuota(String str) {
    // 不处理空字符串和长度小于2的字符串
    if (StringUtils.isEmpty(str) || str.length() < 2) {
      return str;
    }
    char first = str.charAt(0);
    if (first == '`') {
      str = str.substring(1);
    }

    char last = str.charAt(str.length() - 1);
    if (last == '`') {
      str = str.substring(0, str.length() - 1);
    }
    return str;
  }

  /**
   * 给字符串增加反引号
   */
  public static String addBackQuota(String str) {
    //不处理空字符串
    if (StringUtils.isEmpty(str)) {
      return str;
    }

    char first = str.charAt(0);
    if (first == '`') {
      str = "`" + str;
    }

    char last = str.charAt(str.length() - 1);
    if (last == '`') {
      str += "`";
    }
    return str;
  }

  /**
   * 判断两个字符串是否相同，排除反引号区别
   */
  public static boolean equalIgnoreCaseWithoutBackQuota(String str1, String str2) {
    return StringUtils.equalsIgnoreCase(exceptBackQuota(str1), exceptBackQuota(str2));
  }
}
