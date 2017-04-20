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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstantsTest {

  @Test
  public void testRegex() {

    // 用户名正则表达式
    assertFalse(Constants.REGEX_USER_NAME.matcher("aaaaaaaaaaaaaaaaaaaaa").matches());
    assertTrue(Constants.REGEX_USER_NAME.matcher("aaaaaaaaaaaaaaaaaaaa").matches());

    assertFalse(Constants.REGEX_USER_NAME.matcher("").matches());
    assertFalse(Constants.REGEX_USER_NAME.matcher("ab").matches());

    assertFalse(Constants.REGEX_USER_NAME.matcher("_abc").matches());
    assertFalse(Constants.REGEX_USER_NAME.matcher("123").matches());

    // 邮箱正则表达式
    assertTrue(Constants.REGEX_MAIL_NAME.matcher("qifengdai-----@aaa.sss").matches());
    assertFalse(Constants.REGEX_MAIL_NAME.matcher("qifeng.daibaifendian.com").matches());
    assertTrue(Constants.REGEX_MAIL_NAME.matcher("-abc@ifendian.com").matches());
    assertFalse(Constants.REGEX_MAIL_NAME.matcher("-abc@ifendian").matches());
  }
}