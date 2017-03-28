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
package com.baifendian.swordfish.common.datasource.jdbc;

import com.baifendian.swordfish.dao.enums.DbType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JDBCHandlerTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testIsConnectable() throws Exception {
    String paramStr = "{ \"address\": \"jdbc:mysql://swordfish.dev:3306\", \"database\": \"swordfish\", \"user\": \"swordfish\", \"password\": \"myswordfish\", \"autoReconnect\": true, \"maxReconnect\": 3, \"useUnicode\": true, \"characterEncoding\": \"UTF-8\" } \n";
    JDBCHandler jdbcHandler = new JDBCHandler(DbType.MYSQL, paramStr);
    jdbcHandler.isConnectable();
    paramStr = "{ \"address\": \"jdbc:mysql://swordfish.dev1:3306\", \"database\": \"swordfish\", \"user\": \"swordfish\", \"password\": \"myswordfish\", \"autoReconnect\": true, \"maxReconnect\": 3, \"useUnicode\": true, \"characterEncoding\": \"UTF-8\" } \n";
    jdbcHandler = new JDBCHandler(DbType.MYSQL, paramStr);
    thrown.expect(Exception.class);
    jdbcHandler.isConnectable();
    jdbcHandler = new JDBCHandler(DbType.ORACLE, paramStr);
    jdbcHandler.isConnectable();
  }
}
