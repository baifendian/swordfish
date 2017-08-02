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
package com.baifendian.swordfish.execserver.engine.hive;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveMetaExecTest {

  @Test
  public void testGetTables() {
    try {
      Logger logger = LoggerFactory.getLogger(HiveMetaExec.class);

      HiveMetaExec hiveMetaExec = new HiveMetaExec(logger);

      int times = 100;

      long start = System.currentTimeMillis();

      for (int i = 0; i < times; ++i) {
        hiveMetaExec.getTables("dw");
      }

      long end = System.currentTimeMillis();

      System.out.println("get tables time(ms): " + (end - start) / times);

      start = System.currentTimeMillis();

      for (int i = 0; i < times; ++i) {
        hiveMetaExec.getTableObjectsByName("dw");
      }

      end = System.currentTimeMillis();

      System.out.println("get tables objects time(ms): " + (end - start) / times);
    } catch (Exception e) {
    }
  }
}
