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
package com.baifendian.swordfish.dao.utils;

import com.baifendian.swordfish.dao.model.FlowNode;
import com.baifendian.swordfish.dao.model.FlowNodeRelation;
import com.baifendian.swordfish.dao.model.flow.FlowDag;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class DagHelperTest {

  @Test
  public void testFindNodeDepDag() {
    FlowDag flowDag = new FlowDag();

    List<FlowNodeRelation> relas = new ArrayList<>();

    relas.add(new FlowNodeRelation("shell1", "shell2"));
    relas.add(new FlowNodeRelation("shell1", "shell3"));
    relas.add(new FlowNodeRelation("shell2", "shell4"));
    relas.add(new FlowNodeRelation("shell4", "shell5"));

    flowDag.setEdges(relas);

    List<FlowNode> nodes = new ArrayList<>();

    nodes.add(JsonUtil.parseObject("{\"name\":\"shell1\"}", FlowNode.class));
    nodes.add(JsonUtil.parseObject("{\"name\":\"shell2\"}", FlowNode.class));
    nodes.add(JsonUtil.parseObject("{\"name\":\"shell3\"}", FlowNode.class));
    nodes.add(JsonUtil.parseObject("{\"name\":\"shell4\"}", FlowNode.class));
    nodes.add(JsonUtil.parseObject("{\"name\":\"shell5\"}", FlowNode.class));

    flowDag.setNodes(nodes);

    FlowNode node = JsonUtil.parseObject("{\"name\":\"shell2\"}", FlowNode.class);
    FlowDag flowDag1 = DagHelper.findNodeDepDag(flowDag, node, true);

    assertEquals(3, flowDag1.getNodes().size());
    assertEquals("shell2:shell4,shell4:shell5", flowDag1.getEdges().stream().map(rela -> rela.getStartNode() + ":" + rela.getEndNode()).collect(Collectors.joining(",")));

    FlowDag flowDagPre = DagHelper.findNodeDepDag(flowDag, node, false);

    assertEquals("shell1,shell2", flowDagPre.getNodes().stream().map(n -> n.getName()).sorted().collect(Collectors.joining(",")));
    assertEquals("shell1:shell2", flowDagPre.getEdges().stream().map(rela -> rela.getStartNode() + ":" + rela.getEndNode()).collect(Collectors.joining(",")));
  }
}
