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
package com.baifendian.swordfish.dao;

import com.baifendian.swordfish.dao.enums.FlowRunType;
import com.baifendian.swordfish.dao.model.ExecutionFlow;
import com.baifendian.swordfish.dao.model.ExecutionNode;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : liujin
 * @date : 2017-03-14 9:06
 */
public class FlowDaoTest {
  FlowDao flowDao;

  @Before
  public void before() {
    flowDao = DaoFactory.getDaoInstance(FlowDao.class);
  }

  @Test
  public void testQueryExecutionNodeLastAttempt() {
    ExecutionNode executionNode = flowDao.queryExecutionNodeLastAttempt(411, 6);
    System.out.println(executionNode.getStatus());
  }

  @Test
  public void testQueryAllExecutionFlow() {
    List<ExecutionFlow> executionNodeList = flowDao.queryAllNoFinishFlow();
    System.out.println(executionNodeList.size());
  }

  @Test
  public void testQueryExecutionFlow() {
    ExecutionFlow executionFlow = flowDao.queryExecutionFlow(2549);
    System.out.println(executionFlow);
  }

  @Test
  public void testScheduleFlowToExecution() {
    ExecutionFlow executionFlow = flowDao.scheduleFlowToExecution(1, 2, 1, new Date(), FlowRunType.DISPATCH, 3, 5000);
    System.out.println(executionFlow);
  }
}
