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
package com.baifendian.swordfish.dao.mapper;

import com.baifendian.swordfish.dao.datasource.ConnectionFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;

public class FlowNodeMapperTest {
  FlowNodeMapper flowNodeMapper;

  @Before
  public void before() {
    flowNodeMapper = ConnectionFactory.getSqlSession().getMapper(FlowNodeMapper.class);
  }

  @Test
  public void testSelectByFlowId() {
  }

  @Test
  public void testSelectByFlowIds() {
  }

  @Test
  public void testInsert() throws JsonProcessingException {
//    FlowNode flowNode = new FlowNode();
//
//    flowNode.setName("shelljob4");
//    flowNode.setFlowId(2);
//    flowNode.setDesc("shelljob4");
//    flowNode.setType("SHELL");
//    flowNode.setParameter("{\"script\":\"echo shelljob4\"}");
//    flowNode.setDepList(new ArrayList<String>());
//
//    flowNodeMapper.insert(flowNode);
  }
}
