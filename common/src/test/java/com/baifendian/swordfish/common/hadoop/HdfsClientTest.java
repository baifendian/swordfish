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
package com.baifendian.swordfish.common.hadoop;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.*;

public class HdfsClientTest {

  private static HdfsClient hdfsClient;

  private static String tmpFilename;

  @BeforeClass
  public static void runOnceBeforeClass() {
    HdfsClient.init(ConfigurationUtil.getConfiguration());

    hdfsClient = HdfsClient.getInstance();

    String uuid = "a286e22a-4dc8-c093-a1db-f69af4f3b8ce";
    tmpFilename = "/tmp/" + uuid + ".txt";
  }

  @AfterClass
  public static void runOnceAfterClass() {
  }

  @Test
  public void testMkdir() {
    try {
      hdfsClient.mkdir("/tmp/test-001/2/3/");
      assertEquals(hdfsClient.exists("/tmp/test-001/2/3/"), true);
    } catch (IOException e) {
      assertTrue(false);
    }

    hdfsClient.delete("/tmp/test-001", true);

    try {
      assertEquals(hdfsClient.exists("/tmp/test-001"), false);
    } catch (IOException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testCopy() throws IOException {
    PrintWriter writer = new PrintWriter(tmpFilename, "UTF-8");
    writer.println("The first line");
    writer.println("The second line");
    writer.close();

    try {
      assertTrue(hdfsClient.copyLocalToHdfs(tmpFilename, "/tmp", false, true));
      assertTrue(hdfsClient.delete(tmpFilename, false));
    } catch (HdfsException e) {
      assertFalse(true);
    }
  }
}
