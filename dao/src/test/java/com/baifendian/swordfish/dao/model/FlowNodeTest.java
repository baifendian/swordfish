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

import com.baifendian.swordfish.dao.utils.json.JsonUtil;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlowNodeTest {
  @Test
  public void testSetDep() throws IOException {
    FlowNode flowNode = new FlowNode();
    flowNode.setDep("[\"shelljob1\",\"shelljob2\"]");
  }

  @Test
  public void testDeserializer(){
    String nodeStr="{\"name\":\"shelljob1\",\"desc\":\"shell\",\"type\":\"VIRTUAL\",\"parameter\":{\"script\":\"echo shelljob1\"},\"dep\":[],\"extras\":null}";
    FlowNode flowNode = JsonUtil.parseObject(nodeStr, FlowNode.class);
    List<FlowNode> nodes = new ArrayList<>();
    nodes.add(flowNode);
    flowNode.setName("shelljob2");
    nodes.add(flowNode);
    String nodesStr = JsonUtil.toJsonString(nodes);
    System.out.println(nodesStr);
    /*
    String nodesStr="[{\"name\":\"shelljob1\",\"desc\":\"shell\",\"type\":\"VIRTUAL\",\"parameter\":{\"script\":\"echo shelljob1\"},\"dep\":[],\"extras\":null},{\"name\":\"shelljob2\",\"desc\":\"shell\",\"type\":\"VIRTUAL\",\"parameter\":{\"script\":\"echo shelljob2\"},\"dep\":[],\"extras\":null},{\"name\":\"shelljob3\",\"desc\":\"shell\",\"type\":\"SHELL\",\"parameter\":{\"script\":\"echo shelljob3;sleep 5\"},\"dep\":[\"shelljob1\",\"shelljob2\"],\"extras\":null},{\"name\":\"shelljob4\",\"desc\":\"shelljob4\",\"type\":\"SHELL\",\"parameter\":{\"script\":\"echo shelljob4\"},\"dep\":[],\"extras\":null}]\n";
    List<FlowNode> nodes = JsonUtil.parseObjectList(nodeStr, FlowNode.class);
    */
  }

  @Test
  public void testDeserializer2() throws IOException {
    FlowNode flowNode = new FlowNode();
    flowNode.setName("shellJob1");
    flowNode.setType("VIRTUAL");
    flowNode.setParameter("{\"script\":\"echo shelljob1\"}");
    flowNode.setDep("[]");
    String flowNodeStr = JsonUtil.toJsonString(flowNode);
    System.out.println(flowNodeStr);
    FlowNode flowNode1 = JsonUtil.parseObject(flowNodeStr, FlowNode.class);
    System.out.println(flowNode1.getParameter());
  }
}
