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
package com.baifendian.swordfish.common.datasource.mongodb;

import com.baifendian.swordfish.dao.enums.DbType;
import org.junit.Test;

public class MongoDBHandlerTest {

  @Test
  public void testIsConnectable() {
    String paramStr = "{ \"address\": \"mongodb://bgsbtsp0006-dqf:27017\", \"database\": \"test\" }\n";
    MongoDBHandler handler = new MongoDBHandler(DbType.MONGODB, paramStr);
    handler.isConnectable();
  }
}
