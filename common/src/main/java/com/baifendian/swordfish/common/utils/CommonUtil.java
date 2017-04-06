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
package com.baifendian.swordfish.common.utils;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class CommonUtil {
  /**
   * 状态机描述
   */
  private enum Status {
    START, BLANK, END, QUOTE, SINGLE_QUOTE, BACKSLASH_FOR_QUOTE, BACKSLASH_FOR_SINGLE_QUOTE, COMMENT
  }

  /**
   * 关键词, 不需要列举全
   */
  private final static Set<String> keywords = Sets.newHashSet("SELECT", "ALL", "DISTINCT", "FROM", "WHERE", "BY",
      "WITH", "LIMIT", "JOIN", "UNION", "OVER", "IN", "IF", "AND", "OR", "PARTITION");

  /**
   * 识别文件后缀名称
   *
   * @param filename
   * @return
   */
  public static String fileSuffix(String filename) {
    if (StringUtils.isEmpty(filename)) {
      return StringUtils.EMPTY;
    }

    int index = filename.lastIndexOf(".");
    if (index < 0) {
      return StringUtils.EMPTY;
    }

    return filename.substring(index + 1);
  }

  /**
   * sql 字符串解析, 将一个语句进行正常切割为多个, 语句的切割符号为 ";"
   *
   * @param sql
   * @return
   */
  public static List<String> sqlSplit(String sql) {
    if (StringUtils.isEmpty(sql)) {
      return Collections.EMPTY_LIST;
    }

    List<String> r = new ArrayList<>();

    Status status = Status.START;
    StringBuffer buffer = new StringBuffer();

    for (int i = 0; i < sql.length(); ++i) {
      char c = sql.charAt(i);
      char nextChar = ((i + 1) < sql.length()) ? sql.charAt(i + 1) : ' '; // add at 2017/1/6

      boolean skip = false;

      switch (status) {
        case START: {
          if (c == ';') {
            status = Status.END;
            skip = true; // 去掉 ;
          } else if (c == '\'') {
            status = Status.SINGLE_QUOTE;
          } else if (c == '"') {
            status = Status.QUOTE;
          } else if (c == '-' && nextChar == '-') { // add at 2017/1/6
            status = Status.COMMENT;
            ++i; // add at 2017/1/6
            skip = true;
          } else if (Character.isWhitespace(c)) {
            status = Status.BLANK;
          }
        }
        break;
        case BLANK: {
          if (c == ';') {
            status = Status.END;
            skip = true; // 去掉 ;
          } else if (c == '"') {
            status = Status.QUOTE;
          } else if (c == '\'') {
            status = Status.SINGLE_QUOTE;
          } else if (c == '-' && nextChar == '-') { // add at 2017/1/6)
            status = Status.COMMENT;
            ++i; // add at 2017/1/6
            skip = true;
          } else if (!Character.isWhitespace(c)) {
            status = Status.START;
          } else {
            skip = true;
          }
        }
        break;
        case END: {
          if (c == '"') {
            status = Status.QUOTE;
          } else if (c == '\'') {
            status = Status.SINGLE_QUOTE;
          } else if (Character.isWhitespace(c)) {
            status = Status.BLANK;
          } else if (c == '-' && nextChar == '-') { // add at 2017/1/6)
            status = Status.COMMENT;
            ++i; // add at 2017/1/6
            skip = true;
          } else if (c != ';') {
            status = Status.START;
          } else {
            skip = true;
          }
        }
        break;
        case QUOTE: {
          if (c == '"') {
            status = Status.START;
          } else if (c == '\\') {
            status = Status.BACKSLASH_FOR_QUOTE;
          }
        }
        break;
        case SINGLE_QUOTE: {
          if (c == '\'') {
            status = Status.START;
          } else if (c == '\\') {
            status = Status.BACKSLASH_FOR_SINGLE_QUOTE;
          }
        }
        break;
        case BACKSLASH_FOR_QUOTE: {
          status = Status.QUOTE;
        }
        break;
        case BACKSLASH_FOR_SINGLE_QUOTE: {
          status = Status.SINGLE_QUOTE;
        }
        break;
        case COMMENT: {
          if (c != '\r' && c != '\n') {
            status = Status.COMMENT;
            skip = true;
          } else {
            status = Status.START;
          }
        }
        break;
      }

      if (!skip) {
        // 凡是 white space 当做空格处理
        if (Character.isWhitespace(c)) {
          buffer.append(' ');
        } else {
          buffer.append(c);
        }
      }

      if (status == Status.END) {
        String sub = buffer.toString();
        if (!StringUtils.isWhitespace(sub)) {
          r.add(sub.trim());
        }
        buffer = new StringBuffer();
      }
    }

    String sub = buffer.toString();
    if (!StringUtils.isWhitespace(sub)) {
      r.add(sub.trim());
    }

    return r;
  }

  public static void main(String[] args) {
    System.out.println("abc'\\t'def");

    System.out.println("split complex clause...");
    Collection<String> r = sqlSplit("by '\\t' by '\\n' e;LOAD DATA INPATH 'hdfs:///tmp/dw//testtxt_1479882703178_4322' INTO TABLE testtxt_1479882703178_4322;INSERT INTO TABLE baifendian_e_commerce.fact_daily_order PARTITION(l_date='aa') SELECT col_0,null,null,null,null from testtxt_1479882703178_4322;DROP TABLE IF EXISTS testtxt_1479882703178_4322;");
    for (String sr : r) {
      System.out.println(sr);
    }

    System.out.println(fileSuffix("abc.txt"));
    System.out.println(fileSuffix("abc.jar"));
    System.out.println(fileSuffix("abc.zip"));
    System.out.println(fileSuffix("abc"));
  }
}
