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
package com.baifendian.swordfish.dao.datasource;

import com.baifendian.swordfish.dao.mapper.SessionMapper;
import com.baifendian.swordfish.dao.model.Session;
import org.apache.ibatis.session.SqlSession;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * author: smile8 date:   2017/3/16 desc:
 */
public class DatabaseConfigurationTest {
  private static SqlSession sqlSession;

  @BeforeClass
  public static void runOnceBeforeClass() {
    sqlSession = ConnectionFactory.getSqlSession();
  }

  @Test
  public void testSession() {
    SessionMapper mapper = sqlSession.getMapper(SessionMapper.class);

    Session userSession = new Session();

    String uuid = UUID.randomUUID().toString();

    userSession.setId(uuid);
    userSession.setIp("localhost");
    userSession.setUserId(0);
    userSession.setLastLoginTime(new Date());

    mapper.insert(userSession);

    Session userSession2 = mapper.queryById(uuid);

    assertEquals(userSession2.getId(), userSession.getId());

    System.out.println(userSession2.toString());
  }
}