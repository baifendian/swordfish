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
package com.baifendian.swordfish.common.utils.graph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GraphTest {
  private Graph<Integer, String, String> graph;

  @Before
  public void setup() {
    graph = new Graph<>();
  }

  @After
  public void tearDown() {
    graph.clear();
  }

  private void clearAndTest() {
    graph.clear();

    assertEquals(graph.getVertexNumber(), 0);
    assertEquals(graph.getEdgeNumber(), 0);
  }

  private void construtGraph() {
    clearAndTest();

    // 构造一个 1->2,3,4
    //         2->5
    //         3->5
    //         5->7
    //         10->4
    //         4->6
    //         6->7
    //         8
    //         9

    // 构造顶点
    for (int i = 1; i <= 10; ++i) {
      graph.addVertex(i, "v_" + i);
    }

    // 构造边
    assertTrue(graph.addEdge(1, 2, null));
    assertTrue(graph.addEdge(1, 3, null));
    assertTrue(graph.addEdge(1, 4, null));

    assertTrue(graph.addEdge(2, 5, null));

    assertTrue(graph.addEdge(3, 5, null));

    assertTrue(graph.addEdge(5, 7, null));

    assertTrue(graph.addEdge(10, 4, null));

    assertTrue(graph.addEdge(4, 6, null));

    assertTrue(graph.addEdge(6, 7, null));

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 9);
  }

  /**
   * 测试增加顶点, 对顶点数以及返回的顶点个数进行判断
   */
  @Test
  public void testAddVertex() {
    clearAndTest();

    graph.addVertex(1, "abc");
    graph.addVertex(2, null);
    graph.addVertex(3, "def");

    assertEquals(graph.getVertexNumber(), 3);

    assertEquals(graph.getVertex(1), "abc");
    assertTrue(graph.containsVertex(1));
    assertTrue(graph.getVertices().containsKey(1));

    assertEquals(graph.getVertex(2), null);
    assertTrue(graph.containsVertex(2));
    assertTrue(graph.getVertices().containsKey(2));

    assertEquals(graph.getVertex(3), "def");
    assertTrue(graph.containsVertex(3));
    assertTrue(graph.getVertices().containsKey(3));

    assertEquals(graph.getVertex(4), null);
    assertFalse(graph.containsVertex(4));
    assertFalse(graph.getVertices().containsKey(4));

    graph.addVertex(1, "def");
    graph.addVertex(102, "css");

    assertEquals(graph.getVertexNumber(), 4);

    assertEquals(graph.getVertex(1), "def");

    assertEquals(graph.getVertex(102), "css");
    assertTrue(graph.containsVertex(102));
    assertTrue(graph.getVertices().containsKey(102));
  }

  @Test
  public void testAddEdge() {
    clearAndTest();

    assertFalse(graph.addEdge(1, 2, "attr1"));

    graph.addVertex(1, "v1");

    assertFalse(graph.addEdge(1, 2, "attr1"));

    graph.addVertex(2, "v2");

    // 添加自己到自己
    assertFalse(graph.addEdge(1, 1, "wrong"));

    assertTrue(graph.addEdge(1, 2, "attr_12"));

    // 查看边的情况
    assertFalse(graph.containsEdge(1, 3));

    assertTrue(graph.containsEdge(1, 2));
    assertEquals(graph.getEdgeNumber(), 1);
    assertEquals(graph.getEdge(1, 2), "attr_12");
    assertEquals(graph.getEdges().size(), 1);
    assertTrue(graph.getEdges().containsKey(1));

    // 再添加点信息
    graph.addVertex(3, "v3");
    graph.addVertex(4, "v4");

    assertTrue(graph.addEdge(3, 4, "attr_34"));
    assertTrue(graph.addEdge(1, 4, "attr_14"));

    // 查看边的情况
    assertTrue(graph.containsEdge(3, 4));
    assertTrue(graph.containsEdge(1, 4));

    assertEquals(graph.getEdgeNumber(), 3);
    assertEquals(graph.getEdge(3, 4), "attr_34");
    assertEquals(graph.getEdge(1, 4), "attr_14");

    assertEquals(graph.getEdges().size(), 2);
    assertTrue(graph.getEdges().containsKey(1));
    assertTrue(graph.getEdges().containsKey(3));

    assertEquals(graph.getEdges().get(1).size(), 2);
    assertEquals(graph.getEdges().get(3).size(), 1);

    // 测试强制增加结点
    clearAndTest();

    assertTrue(graph.addEdge(1, 2, "attr1", true));

    assertTrue(graph.addEdge(1, 3, "attr2", true));

    assertFalse(graph.addEdge(1, 4, "attr2", false));

    assertEquals(graph.getVertexNumber(), 3);
    assertEquals(graph.getEdgeNumber(), 2);

    assertTrue(graph.containsEdge(1, 2));
    assertTrue(graph.containsEdge(1, 3));

    assertTrue(graph.containsVertex(1));
    assertTrue(graph.containsVertex(2));
    assertTrue(graph.containsVertex(3));
  }

  /**
   *
   */
  @Test
  public void testRemoveVertex() {
    construtGraph();

    graph.removeVertex(4);

    assertEquals(graph.getVertexNumber(), 9);
    assertEquals(graph.getEdgeNumber(), 6);

    graph.removeVertex(6);

    assertEquals(graph.getVertexNumber(), 8);
    assertEquals(graph.getEdgeNumber(), 5);

    graph.removeVertex(8);

    assertEquals(graph.getVertexNumber(), 7);
    assertEquals(graph.getEdgeNumber(), 5);

    graph.removeVertex(9);

    assertEquals(graph.getVertexNumber(), 6);
    assertEquals(graph.getEdgeNumber(), 5);

    graph.removeVertex(1);

    assertEquals(graph.getVertexNumber(), 5);
    assertEquals(graph.getEdgeNumber(), 3);

    graph.removeVertex(3);

    assertEquals(graph.getVertexNumber(), 4);
    assertEquals(graph.getEdgeNumber(), 2);

    graph.removeVertex(2);

    assertEquals(graph.getVertexNumber(), 3);
    assertEquals(graph.getEdgeNumber(), 1);

    graph.removeVertex(7);

    assertEquals(graph.getVertexNumber(), 2);
    assertEquals(graph.getEdgeNumber(), 0);

    graph.removeVertex(5);

    assertEquals(graph.getVertexNumber(), 1);
    assertEquals(graph.getEdgeNumber(), 0);

    graph.removeVertex(10);

    assertEquals(graph.getVertexNumber(), 0);
    assertEquals(graph.getEdgeNumber(), 0);
  }

  /**
   *
   */
  @Test
  public void testRemoveEdge() {
    construtGraph();

    // 构造一个 1->2,3,4
    //         2->5
    //         3->5
    //         5->7
    //         10->4
    //         4->6
    //         6->7
    //         8
    //         9
    graph.removeEdge(1, 2);

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 8);
    assertEquals(graph.getEdges().size(), 7);

    graph.removeEdge(1, 5);

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 8);
    assertEquals(graph.getEdges().size(), 7);

    graph.removeEdge(1, 3);

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 7);
    assertEquals(graph.getEdges().size(), 7);

    graph.removeEdge(1, 4);

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 6);
    assertEquals(graph.getEdges().size(), 6);

    graph.removeEdge(2, 5);

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 5);
    assertEquals(graph.getEdges().size(), 5);

    graph.removeEdge(3, 5);

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 4);
    assertEquals(graph.getEdges().size(), 4);

    graph.removeEdge(5, 7);

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 3);
    assertEquals(graph.getEdges().size(), 3);

    graph.removeEdge(10, 4);

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 2);
    assertEquals(graph.getEdges().size(), 2);

    graph.removeEdge(4, 6);

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 1);
    assertEquals(graph.getEdges().size(), 1);

    graph.removeEdge(6, 7);

    assertEquals(graph.getVertexNumber(), 10);
    assertEquals(graph.getEdgeNumber(), 0);
    assertTrue(graph.getEdges().isEmpty());
  }

  /**
   * 测试后继
   */
  @Test
  public void testPost() {
    construtGraph();

    // id & attr
    assertEquals(graph.getPostNode(1).size(), 3);
    assertTrue(graph.getPostNode(1).contains(2));
    assertTrue(graph.getPostNode(1).contains(3));
    assertTrue(graph.getPostNode(1).contains(4));

    assertEquals(graph.getPostNodeAttr(1).size(), 3);
    assertTrue(graph.getPostNodeAttr(1).containsKey(2));
    assertTrue(graph.getPostNodeAttr(1).containsKey(3));
    assertTrue(graph.getPostNodeAttr(1).containsKey(4));

    // id & attr
    assertEquals(graph.getPostNode(2).size(), 1);
    assertTrue(graph.getPostNode(2).contains(5));

    assertEquals(graph.getPostNodeAttr(2).size(), 1);
    assertTrue(graph.getPostNodeAttr(2).containsKey(5));

    // id & attr
    assertEquals(graph.getPostNode(3).size(), 1);
    assertTrue(graph.getPostNode(3).contains(5));

    assertEquals(graph.getPostNodeAttr(3).size(), 1);
    assertTrue(graph.getPostNodeAttr(3).containsKey(5));

    // id & attr
    assertEquals(graph.getPostNode(5).size(), 1);
    assertTrue(graph.getPostNode(5).contains(7));

    assertEquals(graph.getPostNodeAttr(5).size(), 1);
    assertTrue(graph.getPostNodeAttr(5).containsKey(7));

    // id & attr
    assertTrue(graph.getPostNode(7).isEmpty());

    assertTrue(graph.getPostNodeAttr(7).isEmpty());

    // id & attr
    assertEquals(graph.getPostNode(10).size(), 1);
    assertTrue(graph.getPostNode(10).contains(4));

    assertEquals(graph.getPostNodeAttr(10).size(), 1);
    assertTrue(graph.getPostNodeAttr(10).containsKey(4));

    // id & attr
    assertEquals(graph.getPostNode(4).size(), 1);
    assertTrue(graph.getPostNode(4).contains(6));

    assertEquals(graph.getPostNodeAttr(4).size(), 1);
    assertTrue(graph.getPostNodeAttr(4).containsKey(6));

    // id & attr
    assertEquals(graph.getPostNode(6).size(), 1);
    assertTrue(graph.getPostNode(6).contains(7));

    assertEquals(graph.getPostNodeAttr(6).size(), 1);
    assertTrue(graph.getPostNodeAttr(6).containsKey(7));

    // id & attr
    assertTrue(graph.getPostNode(8).isEmpty());

    assertTrue(graph.getPostNodeAttr(8).isEmpty());

    // id & attr
    assertTrue(graph.getPostNode(9).isEmpty());

    assertTrue(graph.getPostNodeAttr(9).isEmpty());
  }

  /**
   * 测试批量删除和增加接口
   */
  @Test
  public void testMulti() {
    clearAndTest();

    for (int i = 0; i < 10; ++i) {
      graph.addVertex(i, null);
    }

    List<Integer> startIds = new ArrayList<>();

    startIds.add(1);
    startIds.add(2);
    startIds.add(3);
    startIds.add(4);

    assertTrue(graph.addRemoveMultiEdges(Collections.EMPTY_LIST, startIds, 0));

    assertEquals(graph.getEdgeNumber(), 4);

    assertTrue(graph.addRemoveMultiEdges(startIds, Collections.EMPTY_LIST, 0));

    assertEquals(graph.getEdgeNumber(), 0);

    // 测试强制更新前继结点的操作
    clearAndTest();

    for (int i = 0; i < 10; ++i) {
      graph.addVertex(i, null);
    }

    for (int i = 1; i <= 4; ++i) {
      startIds.add(i);
    }

    assertTrue(graph.addEdge(5, 0));
    assertTrue(graph.addEdge(7, 0));

    assertEquals(graph.getPreNode(0).size(), 2);
    assertTrue(graph.getPreNode(0).contains(5));
    assertTrue(graph.getPreNode(0).contains(7));

    assertTrue(graph.forceRefreshPreEdges(startIds, 0));

    assertEquals(graph.getPreNode(0).size(), 4);

    for (int i = 1; i <= 4; ++i) {
      assertTrue(graph.getPreNode(0).contains(i));
    }

    // startIds 清空
    startIds.clear();

    for (int i = 5; i <= 8; ++i) {
      startIds.add(i);
    }

    assertTrue(graph.forceRefreshPreEdges(startIds, 0, true));

    assertEquals(graph.getPreNode(0).size(), 4);

    for (int i = 5; i <= 8; ++i) {
      assertTrue(graph.getPreNode(0).contains(i));
    }

    // 测试顶点不存在的情况
    clearAndTest();

    for (int i = 0; i < 10; ++i) {
      graph.addVertex(i, null);
    }

    assertTrue(graph.addEdge(2, 5));
    assertTrue(graph.addEdge(3, 5));

    startIds.clear();

    startIds.add(7);
    startIds.add(8);
    startIds.add(10);

    assertFalse(graph.forceRefreshPreEdges(startIds, 5));

    assertEquals(graph.getPreNode(5).size(), 2);

    assertTrue(graph.getPreNode(5).contains(2));
    assertTrue(graph.getPreNode(5).contains(3));

    assertTrue(graph.forceRefreshPreEdges(startIds, 5, true));

    assertEquals(graph.getPreNode(5).size(), 3);

    assertTrue(graph.getPreNode(5).contains(7));
    assertTrue(graph.getPreNode(5).contains(8));
    assertTrue(graph.getPreNode(5).contains(10));
  }

  /**
   * 测试前续
   */
  @Test
  public void testPre() {
    construtGraph();

    // id & attr
    assertEquals(graph.getPreNode(7).size(), 2);
    assertTrue(graph.getPreNode(7).contains(5));
    assertTrue(graph.getPreNode(7).contains(6));

    assertEquals(graph.getPreNodeAttr(7).size(), 2);
    assertTrue(graph.getPreNodeAttr(7).containsKey(5));
    assertTrue(graph.getPreNodeAttr(7).containsKey(6));

    // id & attr
    assertEquals(graph.getPreNode(5).size(), 2);
    assertTrue(graph.getPreNode(5).contains(2));
    assertTrue(graph.getPreNode(5).contains(3));

    assertEquals(graph.getPreNodeAttr(5).size(), 2);
    assertTrue(graph.getPreNodeAttr(5).containsKey(2));
    assertTrue(graph.getPreNodeAttr(5).containsKey(3));

    // id & attr
    assertEquals(graph.getPreNode(2).size(), 1);
    assertTrue(graph.getPreNode(2).contains(1));

    assertEquals(graph.getPreNodeAttr(2).size(), 1);
    assertTrue(graph.getPreNodeAttr(2).containsKey(1));

    // id & attr
    assertEquals(graph.getPreNode(3).size(), 1);
    assertTrue(graph.getPreNode(3).contains(1));

    assertEquals(graph.getPreNodeAttr(3).size(), 1);
    assertTrue(graph.getPreNodeAttr(3).containsKey(1));

    // id & attr
    assertTrue(graph.getPreNode(1).isEmpty());

    assertTrue(graph.getPreNodeAttr(1).isEmpty());

    // id & attr
    assertEquals(graph.getPreNode(6).size(), 1);
    assertTrue(graph.getPreNode(6).contains(4));

    assertEquals(graph.getPreNodeAttr(6).size(), 1);
    assertTrue(graph.getPreNodeAttr(6).containsKey(4));

    // id & attr
    assertEquals(graph.getPreNode(4).size(), 2);
    assertTrue(graph.getPreNode(4).contains(1));
    assertTrue(graph.getPreNode(4).contains(10));

    assertEquals(graph.getPreNodeAttr(4).size(), 2);
    assertTrue(graph.getPreNodeAttr(4).containsKey(1));
    assertTrue(graph.getPreNodeAttr(4).containsKey(10));

    // id & attr
    assertTrue(graph.getPreNode(10).isEmpty());

    assertTrue(graph.getPreNodeAttr(10).isEmpty());

    // id & attr
    assertTrue(graph.getPreNode(8).isEmpty());

    assertTrue(graph.getPreNodeAttr(8).isEmpty());

    // id & attr
    assertTrue(graph.getPreNode(9).isEmpty());

    assertTrue(graph.getPreNodeAttr(9).isEmpty());
  }

  /**
   * 测试出度
   */
  @Test
  public void testOutdegree() {
    construtGraph();

    assertEquals(graph.getOutdegree(1), 3);
    assertEquals(graph.getOutdegree(2), 1);
    assertEquals(graph.getOutdegree(3), 1);
    assertEquals(graph.getOutdegree(4), 1);
    assertEquals(graph.getOutdegree(5), 1);
    assertEquals(graph.getOutdegree(6), 1);
    assertEquals(graph.getOutdegree(7), 0);
    assertEquals(graph.getOutdegree(8), 0);
    assertEquals(graph.getOutdegree(9), 0);
    assertEquals(graph.getOutdegree(10), 1);
  }

  /**
   * 测试入度
   */
  @Test
  public void testIndegree() {
    construtGraph();

    assertEquals(graph.getIndegree(1), 0);
    assertEquals(graph.getIndegree(2), 1);
    assertEquals(graph.getIndegree(3), 1);
    assertEquals(graph.getIndegree(4), 2);
    assertEquals(graph.getIndegree(5), 2);
    assertEquals(graph.getIndegree(6), 1);
    assertEquals(graph.getIndegree(7), 2);
    assertEquals(graph.getIndegree(8), 0);
    assertEquals(graph.getIndegree(9), 0);
    assertEquals(graph.getIndegree(10), 0);
  }

  /**
   * 测试起点
   */
  @Test
  public void testStart() {
    construtGraph();

    assertEquals(graph.getStartVertex().size(), 4);

    assertTrue(graph.getStartVertex().contains(1));
    assertTrue(graph.getStartVertex().contains(10));
    assertTrue(graph.getStartVertex().contains(8));
    assertTrue(graph.getStartVertex().contains(9));
  }

  /**
   * 测试终点
   */
  @Test
  public void testEnd() {
    construtGraph();

    assertEquals(graph.getEndVertex().size(), 3);

    assertTrue(graph.getEndVertex().contains(7));
    assertTrue(graph.getEndVertex().contains(8));
    assertTrue(graph.getEndVertex().contains(9));
  }

  /**
   * 测试环
   */
  @Test
  public void testCycle() {
    clearAndTest();

    // 构造顶点
    for (int i = 1; i <= 10; ++i) {
      graph.addVertex(i, "v_" + i);
    }

    // 构造边, 1->2, 2->3, 3->4
    try {
      graph.addEdge(1, 2, null);
      graph.addEdge(2, 3, null);
      graph.addEdge(3, 4, null);

      assertFalse(graph.hasCycle());
    } catch (Exception e) {
      e.printStackTrace();
      assertTrue(false);
    }

    // 构造 4->1
    try {
      graph.addEdge(4, 1, null);

      assertTrue(graph.hasCycle());

      graph.addEdge(5, 1, null);

      assertTrue(graph.hasCycle());
    } catch (Exception e) {
      assertTrue(false);
    }

    // 重新清空
    clearAndTest();

    // 构造顶点
    for (int i = 1; i <= 10; ++i) {
      graph.addVertex(i, "v_" + i);
    }

    // 构造边, 1->2, 2->3, 3->4
    try {
      graph.addEdge(1, 2, null);
      graph.addEdge(2, 3, null);
      graph.addEdge(3, 4, null);
      graph.addEdge(4, 5, null);
      graph.addEdge(5, 2, null);

      assertTrue(graph.hasCycle());
    } catch (Exception e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }

  @Test
  public void testBroadFirstSearch() {
    clearAndTest();

    // 构造一个 1->2
    //         2->3
    //         3->4
    //         4->5
    //         1->6

    graph.addEdge(1, 2, null, true);
    graph.addEdge(2, 3, null, true);
    graph.addEdge(3, 4, null, true);
    graph.addEdge(4, 5, null, true);
    graph.addEdge(1, 6, null, true);

    List<Integer> visit = null;

    try {
      visit = graph.broadFirstSearch();

      assertEquals(visit.size(), 6);

      assertTrue(visit.get(0) == 1);
      assertTrue(visit.get(1) == 2);
      assertTrue(visit.get(2) == 6);
      assertTrue(visit.get(3) == 3);
      assertTrue(visit.get(4) == 4);
      assertTrue(visit.get(5) == 5);
    } catch (Exception e) {
      assertTrue(false);
    }

    graph.addEdge(7, 8, null, true);

    try {
      visit = graph.broadFirstSearch();

      assertEquals(visit.size(), 8);

      assertTrue(visit.get(0) == 1);
      assertTrue(visit.get(1) == 7);
      assertTrue(visit.get(2) == 2);
      assertTrue(visit.get(3) == 6);
      assertTrue(visit.get(4) == 8);
      assertTrue(visit.get(5) == 3);
      assertTrue(visit.get(6) == 4);
      assertTrue(visit.get(7) == 5);
    } catch (Exception e) {
      assertTrue(false);
    }

    graph.addEdge(8, 7, null, true);

    try {
      visit = graph.broadFirstSearch();

      assertTrue(false);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testDepthFirstSearch() {

    graph.addEdge(1, 2, null, true);
    graph.addEdge(2, 3, null, true);
    graph.addEdge(3, 4, null, true);
    graph.addEdge(4, 5, null, true);
    graph.addEdge(1, 6, null, true);

    List<Integer> visit = null;

    try {
      visit = graph.depthFirstSearch();

      assertEquals(visit.size(), 6);

      assertTrue(visit.get(0) == 1);
      assertTrue(visit.get(1) == 2);
      assertTrue(visit.get(2) == 3);
      assertTrue(visit.get(3) == 4);
      assertTrue(visit.get(4) == 5);
      assertTrue(visit.get(5) == 6);
    } catch (Exception e) {
      assertTrue(false);
    }

    graph.addEdge(2, 7, null, true);

    try {
      visit = graph.depthFirstSearch();

      assertEquals(visit.size(), 7);

      assertTrue(visit.get(0) == 1);
      assertTrue(visit.get(1) == 2);
      assertTrue(visit.get(2) == 3);
      assertTrue(visit.get(3) == 4);
      assertTrue(visit.get(4) == 5);
      assertTrue(visit.get(5) == 7);
      assertTrue(visit.get(6) == 6);
    } catch (Exception e) {
      assertTrue(false);
    }

    graph.addEdge(6, 1, null, true);

    try {
      visit = graph.depthFirstSearch();

      assertTrue(false);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testTopologicalSort() {
    clearAndTest();

    // 构造一个有环图
    graph.addEdge(1, 2, null, true);
    graph.addEdge(1, 6, null, true);
    graph.addEdge(2, 3, null, true);
    graph.addEdge(3, 4, null, true);
    graph.addEdge(3, 6, null, true);
    graph.addEdge(4, 5, null, true);
    graph.addEdge(5, 3, null, true);
    graph.addEdge(5, 6, null, true);
    graph.addEdge(7, 6, null, true);

    try {
      graph.topologicalSort();

      assertTrue(false);
    } catch (Exception e) {
      assertTrue(true);
    }

    // 构造一个好的图
    graph.removeEdge(3, 4);

    try {
      List<Integer> entry = graph.topologicalSort();

      List l = entry.subList(0, 3);
      assertTrue(l.contains(1) && l.contains(7) && l.contains(4));

      l = entry.subList(3, 5);
      assertTrue(l.contains(2) && l.contains(5));

      assertTrue(entry.get(5) == 3);

      assertTrue(entry.get(6) == 6);
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testIsConnected() {
    clearAndTest();

    assertTrue(graph.isConnected());

    graph.addVertex(1, null);

    assertTrue(graph.isConnected());

    graph.addEdge(1, 2, null, true);
    graph.addEdge(2, 3, null, true);
    graph.addEdge(3, 4, null, true);

    assertTrue(graph.isConnected());

    graph.addEdge(5, 6, null, true);

    assertFalse(graph.isConnected());
  }

  /**
   * 测试性能
   */
  @Test
  public void testPerf() {
    clearAndTest();

    Random r = new Random();

    long start = System.currentTimeMillis();

    /** 构造 10W 的边大概需要 3~4 秒, 后面的循环判断等等操作, 几乎不要时间; 50~100 以下的, 在 10ms 以内; 50 以内的, 在 3ms 以内. */
    int nodes = 10000;
    int edgePerNode = 20;

    for (int i = 0; i < nodes; ++i) {
      for (int j = 0; j < edgePerNode; ++j) {
        graph.addEdge(i, r.nextInt(nodes) + nodes, null, true);
      }
    }

    long end = System.currentTimeMillis();

    System.out.println("Add edge cost time: " + (end - start) / 1000.0);

    start = System.currentTimeMillis();

    try {
      graph.topologicalSort();
      assert (true);
    } catch (Exception e) {
      assertTrue(false);
    }

    end = System.currentTimeMillis();

    System.out.println("TopologicalSort cost time: " + (end - start) / 1000.0);

    start = System.currentTimeMillis();

    graph.hasCycle();

    end = System.currentTimeMillis();

    System.out.println("Judge cycle cost time: " + (end - start) / 1000.0 + ", has cycle: " + graph.hasCycle());

    start = System.currentTimeMillis();

    for (int i = 0; i < nodes; ++i) {
      Collection<Integer> pre = graph.getPreNode(i);
      Collection<Integer> post = graph.getPostNode(i);
    }

    end = System.currentTimeMillis();

    System.out.println("Get pre and post cost time: " + (end - start) / 1000.0);

    start = System.currentTimeMillis();

    try {
      graph.broadFirstSearch();
      assert (true);
    } catch (Exception e) {
      assertTrue(false);
    }

    try {
      graph.depthFirstSearch();
      assert (true);
    } catch (Exception e) {
      assertTrue(false);
    }

    end = System.currentTimeMillis();

    System.out.println("Get broad and depth first search cost time: " + (end - start) / 1000.0);

    start = System.currentTimeMillis();

    assertTrue(graph.isConnected());

    end = System.currentTimeMillis();

    System.out.println("Test connect cost time: " + (end - start) / 1000.0);

    start = System.currentTimeMillis();

    for (int i = 0; i < 100; ++i) {
      for (int j = 0; j < edgePerNode; ++j) {
        graph.addEdge(i, r.nextInt(10), null, true);
      }
    }

    end = System.currentTimeMillis();

    System.out.println("Add edge cost time: " + (end - start) / 1000.0);
  }
}
