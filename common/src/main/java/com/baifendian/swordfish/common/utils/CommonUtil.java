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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

import static com.baifendian.swordfish.common.utils.CommonUtil.TokenType.BRACKET;

public class CommonUtil {
  /**
   * 状态机描述
   */
  private enum Status {
    START, LETTER, BLANK, END, QUOTE, SINGLE_QUOTE, BACKSLASH_FOR_QUOTE, BACKSLASH_FOR_SINGLE_QUOTE, COMMENT
  }

  /**
   * token 的类型描述
   */
  public enum TokenType {
    WORD, BRACKET, BRACKET2, BLANK, OTHERS
  }

  /**
   * 关键词, 不需要列举全
   */
  private final static Set<String> keywords = Sets.newHashSet("SELECT", "ALL", "DISTINCT", "FROM", "WHERE", "BY",
          "WITH", "LIMIT", "JOIN", "UNION", "OVER", "IN", "IF", "AND", "OR", "PARTITION");

  private static MessageDigest md = null;

  static {
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * 计算指定字符串的16位md5值
   *
   * @param str 指定字符串
   * @return 计算后的md5值
   */
  public static String getMD5(String str) {

    md.update(str.getBytes());
    return new BigInteger(1, md.digest()).toString(16);
  }

  /**
   * 增加日期中某类型的某数值。如增加日期
   *
   * @param date     日期
   * @param dateType 类型
   * @param amount   数值
   * @return 计算后日期
   */
  private static Date addInteger(Date date, int dateType, int amount) {
    Date resDate = null;
    if (date != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.add(dateType, amount);
      resDate = calendar.getTime();
    }
    return resDate;
  }

  /**
   * 增加日期的小时。失败返回null。
   *
   * @param date       日期
   * @param hourAmount 增加数量。可为负数
   * @return 增加小时后的日期
   */
  public static Date addHour(Date date, int hourAmount) {
    return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
  }

  /**
   * 增加日期的天数。失败返回null。
   *
   * @param date      日期
   * @param dayAmount 增加数量。可为负数
   * @return 增加天数后的日期
   */
  public static Date addDay(Date date, int dayAmount) {
    return addInteger(date, Calendar.DATE, dayAmount);
  }

  /**
   * 解析整型 <p>
   *
   * @return 整型
   */
  public static Integer parseInteger(String integerStr) {
    if (StringUtils.isEmpty(integerStr)) {
      return null;
    }
    return Integer.valueOf(integerStr);
  }

  /**
   * sql 字符串解析, 将一个语句进行正常切割, 为多个. 能处理 ', ", 回车换行问题.
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

  /**
   * 返回 token 以及类型, 对于单词, 采用 value, 其它的则没有
   */
  public static List<Map.Entry<TokenType, String>> getToken(String sql) {
    if (StringUtils.isEmpty(sql)) {
      return Collections.EMPTY_LIST;
    }

    StringBuffer buffer = new StringBuffer();

    List<Map.Entry<TokenType, String>> list = new ArrayList<>();
    Status status = Status.START;

    for (int i = 0; i < sql.length(); ++i) {
      char c = sql.charAt(i);
      char nextChar = ((i + 1) < sql.length()) ? sql.charAt(i + 1) : ' '; // add at 2017/1/6

      boolean singleChar = false;

      switch (status) {
        case START: {
          if (c == '_' || Character.isLetter(c)) {
            status = Status.LETTER;
            // 如果是单词, 就记录之
            buffer.append(c);
          } else if (c == '\'') {
            status = Status.SINGLE_QUOTE;
          } else if (c == '"') {
            status = Status.QUOTE;
          } else if (c == '-' && nextChar == '-') { // add at 2017/1/6)
            status = Status.COMMENT;
            ++i;
            continue;
          } else {
            singleChar = true;
          }
        }
        break;
        case COMMENT: {
          if (c != '\r' && c != '\n') {
            status = Status.COMMENT;
            continue;
          } else {
            status = Status.START;
            singleChar = true;
          }
        }
        case LETTER: {
          if (c == '_' || Character.isLetter(c) || Character.isDigit(c)) {
            status = Status.LETTER;
            // 如果是单词, 就记录之
            buffer.append(c);
          } else if (c == '"') {
            status = Status.QUOTE;
          } else if (c == '\'') {
            status = Status.SINGLE_QUOTE;
          } else if (c == '-' && nextChar == '-') { // add at 2017/1/6)
            status = Status.COMMENT;
            ++i;
            continue;
          } else {
            list.add(new AbstractMap.SimpleEntry(TokenType.WORD, buffer.toString()));
            buffer = new StringBuffer();
            // 是一个终结符号
            status = Status.START;
            singleChar = true;
          }
        }
        break;
        case QUOTE: {
          if (c == '"') {
            // 结束了
            list.add(new AbstractMap.SimpleEntry(TokenType.OTHERS, null));
            status = Status.START;
          } else if (c == '\\') {
            status = Status.BACKSLASH_FOR_QUOTE;
          }
        }
        break;
        case SINGLE_QUOTE: {
          if (c == '\'') {
            // 结束了
            list.add(new AbstractMap.SimpleEntry(TokenType.OTHERS, null));
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
      }

      // 这里起点即是终点
      if (singleChar) {            // 是一个终结符号
        if (c == '(') {
          list.add(new AbstractMap.SimpleEntry(BRACKET, null));
        } else if (c == ')') {
          list.add(new AbstractMap.SimpleEntry(TokenType.BRACKET2, null));
        } else if (Character.isWhitespace(c)) {
          list.add(new AbstractMap.SimpleEntry(TokenType.BLANK, null));
        } else {
          list.add(new AbstractMap.SimpleEntry(TokenType.OTHERS, null));
        }
      }
    }

    switch (status) {
      case QUOTE:
      case BACKSLASH_FOR_QUOTE:
      case BACKSLASH_FOR_SINGLE_QUOTE:
      case SINGLE_QUOTE: {
        list.add(new AbstractMap.SimpleEntry(TokenType.OTHERS, null));
      }
      break;
      case LETTER: {
        list.add(new AbstractMap.SimpleEntry(TokenType.WORD, buffer.toString()));
      }
      break;
      default:
        break;
    }

    return list;
  }

  /**
   * 识别一个 sql 中的自定义 function, 这个算法是采用栈的方式来实现: 1) 首先将 sql 处理, 去除掉 ""; 2) 然后获取一个个的 Token, 对于 Token
   * 有2种情况, 单词, 非单词(都是一个个字符); 3) 单词就符合单词的规则, 以 _ 或者字母开头, 后面都是数字; 4) 如果是 单词, 后面接上了 (, 考虑将 单词 作为
   * function name, 否则抛弃; <p> 注意: 有可能多找关键词, 比如某些 hive 关键词后面跟上了 (), 会误认为是关键词, 不过这个不影响我们的正常处理.
   */
  public static Set<String> sqlFunction(String sql) {
    Collection<Map.Entry<TokenType, String>> tokens = getToken(sql);
    if (tokens.isEmpty()) {
      return Collections.EMPTY_SET;
    }

    Stack<Map.Entry<TokenType, String>> stack = new Stack<>();

    Set<String> funcs = new HashSet<>();

    // 遍历 token
    for (Map.Entry<TokenType, String> token : tokens) {
      // 1) 如果是遇到了 BLANK, 则忽略;
      // 2) 栈顶如果是 WORD, 如果遇到了 WORD, 弹出并压入新的 WORD, 遇到了 BRACKET, 则压入, 遇到了 BRACKET2, 一直弹出遇到了 BRACKET;
      // 3) 栈顶如果是 BRACKET, 遇到了 WORD 则压入, 遇到了 OTHERS 则丢弃, 如果遇到了 BRACKET2 则栈弹出, 一直到弹出了 WORD, 记为 function;
      // 4) 栈顶如果是空的, 遇到了 WORD 则压入, 其它都丢弃;
      TokenType type = token.getKey();

      // 如果遇到了 BLANK, 则丢弃
      if (type == TokenType.BLANK) {
        continue;
      }

      // 查看栈顶
      if (stack.isEmpty()) { // 空的
        if (type == TokenType.WORD) {
          stack.push(token);
        }
        continue;
      }

      // 非空的
      Map.Entry<TokenType, String> top = stack.peek();

      switch (top.getKey()) {
        case WORD: {
          switch (token.getKey()) {
            case BRACKET: { // 遇到了 (
              stack.push(token);
            }
            break;
            case BRACKET2: { // 遇到了 )
              while (!stack.isEmpty()) {
                TokenType pop = stack.pop().getKey();
                // 如果遇到了 (
                if (pop == TokenType.BRACKET) {
                  Map.Entry<TokenType, String> word = stack.pop();
                  if (!keywords.contains(word.getValue().toUpperCase())) {
                    funcs.add(word.getValue());
                  }
                  break;
                }
              }
            }
            break;
            case WORD: { // 遇到了 WORD
              stack.pop();
              stack.push(token);
            }
            break;
            default: {
              stack.pop();
            }
            break;
          }
        }
        break;
        case BRACKET: {
          switch (token.getKey()) {
            case WORD: { // 遇到了 WORD
              stack.push(token);
            }
            break;
            case BRACKET2: { // 遇到了 )
              stack.pop();
              Map.Entry<TokenType, String> word = stack.pop();
              if (!keywords.contains(word.getValue().toUpperCase())) {
                funcs.add(word.getValue());
              }
            }
            break;
            default:
              break;
          }
        }
        break;
        default:
          break;
      }
    }

    return funcs;
  }

  public static void main(String[] args) {
    System.out.println("abc'\\t'def");

    System.out.println("split complex clause...");
    Collection<String> r = CommonUtil.sqlSplit("by '\\t' by '\\n' e;LOAD DATA INPATH 'hdfs:///tmp/dw//testtxt_1479882703178_4322' INTO TABLE testtxt_1479882703178_4322;INSERT INTO TABLE baifendian_e_commerce.fact_daily_order PARTITION(l_date='aa') SELECT col_0,null,null,null,null from testtxt_1479882703178_4322;DROP TABLE IF EXISTS testtxt_1479882703178_4322;");
    for (String sr : r) {
      System.out.println(sr);
    }
  }
}
