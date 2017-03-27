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
package com.baifendian.swordfish.common.datasource;

import com.baifendian.swordfish.common.datasource.jdbc.JDBCHandler;
import com.baifendian.swordfish.dao.enums.DbType;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertTrue;

public class DataSourceManagerTest {

  @Test
  public void testGetHandler() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    String paramStr = "{ \"address\": \"jdbc:mysql://swordfish.dev:3306\", \"database\": \"swordfish\", \"user\": \"swordfish\", \"password\": \"myswordfish\", \"autoReconnect\": true, \"maxReconnect\": 3, \"useUnicode\": true, \"characterEncoding\": \"UTF-8\" } \n";
    DataSourceHandler dataSourceHandler = DataSourceManager.getHandler(DbType.MYSQL, paramStr);
    assertTrue(JDBCHandler.class.isInstance(dataSourceHandler));
  }
}
