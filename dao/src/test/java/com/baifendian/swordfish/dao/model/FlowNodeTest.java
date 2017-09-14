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
package com.baifendian.swordfish.dao.model;

import static org.junit.Assert.assertEquals;

import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class FlowNodeTest {
  @Test
  public void testSetDep() throws IOException {
  }

  @Test
  public void testDeserializer() {
    String nodeStr = "{\"name\":\"shelljob1\",\"desc\":\"shell\",\"type\":\"VIRTUAL\",\"parameter\":{\"script\":\"echo shelljob1\"},\"dep\":[],\"extras\":null}";

    FlowNode flowNode = JsonUtil.parseObject(nodeStr, FlowNode.class);

    List<FlowNode> nodes = new ArrayList<>();
    nodes.add(flowNode);
    flowNode.setName("shelljob2");
    nodes.add(flowNode);

    String nodesStr = JsonUtil.toJsonString(nodes);
  }

  @Test
  public void testDeserializer2() throws IOException {
    FlowNode flowNode = new FlowNode();

    flowNode.setName("shellJob1");
    flowNode.setType("VIRTUAL");
    flowNode.setParameter("{\"script\":\"echo shelljob1\"}");
    flowNode.setDep("[]");

    String flowNodeStr = JsonUtil.toJsonString(flowNode);

    FlowNode flowNode1 = JsonUtil.parseObject(flowNodeStr, FlowNode.class);

    assertEquals(flowNode.getName(), flowNode1.getName());
    assertEquals(flowNode.getParameter(), flowNode1.getParameter());
  }
}
