/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.baifendian.swordfish.com.baifendian.swordfish.common.mail;

import com.baifendian.swordfish.common.mail.MailSendUtil;
import org.junit.Test;

import java.util.Arrays;

/**
 * author: smile8
 * date:   2017/2/28
 * desc:
 */
public class MailSendUtilTest {
  @Test
  public void testSendMails() {
    String[] mails = new String[]{"qifeng.dai@baifendian.com"};
    String title = "test from swordfish";
    String content = "test";

    MailSendUtil.sendMails(Arrays.asList(mails), title, content);
  }
}
