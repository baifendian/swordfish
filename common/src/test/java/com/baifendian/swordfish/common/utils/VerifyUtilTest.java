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

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class VerifyUtilTest {
  @Test
  public void testMatcheUserName() {
    {
      String test = "bfd_test";
      assertTrue(VerifyUtil.matcheUserName(test));
    }
  }

  @Test
  public void testMatcheProjectName() {
    {
      String test = "bfd_test";
      assertTrue(VerifyUtil.matcheProjectName(test));
    }
  }

  @Test
  public void testMatcheResName() {
    {
      assertTrue(VerifyUtil.matcheResName("aa.jar"));

      assertTrue(VerifyUtil.matcheResName("spark-examples-1.0-SNAPSHOT-hadoop2.6.0.jar"));
    }
  }
}
