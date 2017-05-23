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

import java.util.*;

import static org.junit.Assert.*;

public class DAGGraphTest {
  private Graph<Integer, String, String> graph;

  @Before
  public void setup() {
    graph = new DAGGraph<>();
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

  // 这里主要测试 "增加边" 的相关操作, 其它 method 的测试在 GraphTest 中都已经覆盖了
  @Test
  public void testAddEdge() {
    clearAndTest();

    assertTrue(graph.addEdge(1, 2, null, true));
    assertTrue(graph.addEdge(2, 3, null, true));

    assertTrue(graph.addEdge(1, 2, null, true));
    assertTrue(graph.addEdge(2, 3, null, true));

    assertTrue(graph.addEdge(3, 4, null, true));
    assertTrue(graph.addEdge(4, 5, null, true));

    graph.clear();

    for (int i = 0; i < 1000; ++i) {
      assertTrue(graph.addEdge(i, i + 1, null, true));
    }

    assertFalse(graph.addEdge(1000, 0, null, true));
    assertTrue(graph.addEdge(1000, 1001, null, true));
    assertFalse(graph.addEdge(1000, 1000, null, true));
  }

  @Test
  public void testMulti() {
    clearAndTest();

    List<Integer> l = new LinkedList<>();
    for (int i = 0; i < 10; ++i) {
      l.add(i);
    }

    assertTrue(graph.addRemoveMultiEdges(Collections.EMPTY_LIST, l, 10, true));

    l.clear();
    l.add(10);

    assertFalse(graph.addRemoveMultiEdges(Collections.EMPTY_LIST, l, 1, true));

    List<Integer> l2 = new LinkedList<>();
    l2.add(1);

    // delete 1 -> 10
    assertTrue(graph.addRemoveMultiEdges(l2, Collections.EMPTY_LIST, 10, true));

    // add 10 -> 1
    assertTrue(graph.addRemoveMultiEdges(Collections.EMPTY_LIST, l, 1, true));

    // 测试环的情况
    clearAndTest();

    List<Integer> startIds = new ArrayList<>();

    for (int i = 0; i < 10; ++i) {
      graph.addVertex(i, null);
    }

    assertTrue(graph.addEdge(5, 3));
    assertTrue(graph.addEdge(3, 2));

    startIds.clear();

    startIds.add(7);
    startIds.add(8);
    startIds.add(10);

    assertTrue(graph.forceRefreshPreEdges(startIds, 5, true));

    // 清空
    startIds.clear();
    assertTrue(graph.forceRefreshPreEdges(startIds, 5, true));

    assertTrue(graph.addEdge(5, 3));
    assertTrue(graph.addEdge(3, 2));

    startIds.add(7);
    startIds.add(8);
    startIds.add(10);
    startIds.add(2);

    assertFalse(graph.forceRefreshPreEdges(startIds, 5, true));
  }

  @Test
  public void testPerf() {
    clearAndTest();

    Random r = new Random();

    long start = System.currentTimeMillis();

    /** 构造 10W 的边大概需要 7~8 秒, 后面的循环判断等等操作, 几乎不要时间; 50~100 以下的, 在 20ms 以内; 50 以内的, 在 10ms 以内. */
    int nodes = 10000;
    int edgePerNode = 20;

    for (int i = 0; i < nodes; ++i) {
      List<Integer> l = new LinkedList<>();

      for (int j = 0; j < edgePerNode; ++j) {
        l.add(r.nextInt() + nodes);
      }

      graph.addRemoveMultiEdges(Collections.EMPTY_LIST, l, i, true);
    }

    long end = System.currentTimeMillis();

    System.out.println("Add edge cost time: " + (end - start) / 1000.0);

    start = System.currentTimeMillis();

    end = System.currentTimeMillis();

    System.out.println("Judge cycle cost time: " + (end - start) / 1000.0);

    start = System.currentTimeMillis();

    for (int i = 0; i < nodes; ++i) {
      Collection<Integer> pre = graph.getPreNode(i);
      Collection<Integer> post = graph.getPostNode(i);
    }

    end = System.currentTimeMillis();

    System.out.println("Get pre and post cost time: " + (end - start) / 1000.0);

    start = System.currentTimeMillis();

    graph.isConnected();

    end = System.currentTimeMillis();

    System.out.println("Test connect cost time: " + (end - start) / 1000.0);

    start = System.currentTimeMillis();

    for (int i = 0; i < 100; ++i) {
      List<Integer> l = new LinkedList<>();

      for (int j = 0; j < 10; ++j) {
        l.add(r.nextInt());
      }

      graph.addRemoveMultiEdges(Collections.EMPTY_LIST, l, i, true);
    }

    end = System.currentTimeMillis();

    System.out.println("Add edge cost time: " + (end - start) / 1000.0);
  }
}
