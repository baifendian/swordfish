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

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 计算表达式工具类 <p>
 */
public class CalculateUtil {

  /**
   * 计算整数表达式的值
   *
   * @param expression
   * @return
   */
  public static Integer calc(String expression) {
    expression = StringUtils.trim(expression);
    expression = transform(expression);
    List<String> result = getStringList(expression); // String转换为List
    result = getPostOrder(result); // 中缀变后缀

    return calculate(result); // 计算
  }

  /**
   * 将表达式中正负号改为 P（正） S（负）
   *
   * @param expression 例如 "-2+-1*(+3)-(-1) 被转为 S2+S1*(P3)-(S1)"
   * @return
   */
  private static String transform(String expression) {
    char[] arr = expression.toCharArray();
    for (int i = 0; i < arr.length; i++) {
      if (arr[i] == '-') {
        if (i == 0) {
          arr[i] = 'S';
        } else {
          char c = arr[i - 1];
          if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(') {
            arr[i] = 'S';
          }
        }
      } else if (arr[i] == '+') {
        if (i == 0) {
          arr[i] = 'P';
        } else {
          char c = arr[i - 1];
          if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(') {
            arr[i] = 'P';
          }
        }
      }
    }
    return new String(arr);
  }

  /**
   * 将字符串转化成 List
   *
   * @param expression
   * @return list
   */
  private static List<String> getStringList(String expression) {
    List<String> result = new ArrayList<String>();
    String num = "";
    for (int i = 0; i < expression.length(); i++) {
      if (Character.isDigit(expression.charAt(i))) {
        num = num + expression.charAt(i);
      } else {
        if (num != "") {
          result.add(num);
        }
        result.add(expression.charAt(i) + "");
        num = "";
      }
    }
    if (num != "") {
      result.add(num);
    }
    return result;
  }

  /**
   * 将中缀表达式转化为后缀表达式
   *
   * @param inOrderList
   * @return 后缀表达式
   */
  private static List<String> getPostOrder(List<String> inOrderList) {
    List<String> result = new ArrayList<String>();
    Stack<String> stack = new Stack<String>();
    for (int i = 0; i < inOrderList.size(); i++) {
      if (Character.isDigit(inOrderList.get(i).charAt(0))) {
        result.add(inOrderList.get(i));
      } else {
        switch (inOrderList.get(i).charAt(0)) {
          case '(':
            stack.push(inOrderList.get(i));
            break;
          case ')':
            while (!stack.peek().equals("(")) {
              result.add(stack.pop());
            }
            stack.pop();
            break;
          default:
            while (!stack.isEmpty() && compare(stack.peek(), inOrderList.get(i))) {
              result.add(stack.pop());
            }
            stack.push(inOrderList.get(i));
            break;
        }
      }
    }

    while (!stack.isEmpty()) {
      result.add(stack.pop());
    }

    return result;
  }

  /**
   * 计算后缀表达式
   *
   * @param postOrder
   * @return 整数
   */
  private static Integer calculate(List<String> postOrder) {
    Stack<Integer> stack = new Stack<>();
    for (int i = 0; i < postOrder.size(); i++) {
      if (Character.isDigit(postOrder.get(i).charAt(0))) {
        stack.push(Integer.parseInt(postOrder.get(i)));
      } else {
        Integer back = stack.pop();
        Integer front = 0;
        char op = postOrder.get(i).charAt(0);
        if (!(op == 'P' || op == 'S')) { // 操作符不是 "正负号" 的情况
          front = stack.pop();
        }

        Integer res = 0;
        switch (postOrder.get(i).charAt(0)) {
          case 'P':
            res = front + back;
            break;
          case 'S':
            res = front - back;
            break;
          case '+':
            res = front + back;
            break;
          case '-':
            res = front - back;
            break;
          case '*':
            res = front * back;
            break;
          case '/':
            res = front / back;
            break;
        }
        stack.push(res);
      }
    }
    return stack.pop();
  }

  /**
   * 比较运算符等级
   *
   * @param peek
   * @param cur
   * @return true or false
   */
  private static boolean compare(String peek, String cur) {
    if ("*".equals(peek) && ("/".equals(cur) || "*".equals(cur) || "+".equals(cur) || "-".equals(cur))) {
      return true;
    } else if ("/".equals(peek) && ("/".equals(cur) || "*".equals(cur) || "+".equals(cur) || "-".equals(cur))) {
      return true;
    } else if ("+".equals(peek) && ("+".equals(cur) || "-".equals(cur))) {
      return true;
    } else if ("-".equals(peek) && ("+".equals(cur) || "-".equals(cur))) {
      return true;
    }

    return false;
  }

  public static void main(String[] args) {
    String s = "24*60*(-12/24/60)";
    System.out.println(calc(s));
  }
}
