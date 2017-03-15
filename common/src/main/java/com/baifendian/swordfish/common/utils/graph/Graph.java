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

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * author: smile8
 * date:   09/09/2016
 * desc:   这里描述了一个图的结构, 图具有顶点和边, 顶点属性是 VD, 边的属性是 ED, 默认实现是考虑的普通的 "有向图结构"
 */
public class Graph<VD, ED> {
  private static final Logger LOG = LoggerFactory.getLogger(Graph.class);

  /**
   * 顶点, key 为顶点的 ID, value 为顶点的属性信息
   */
  protected volatile Map<Integer, VD> vertices;

  /**
   * 边, key 为起点 ID, value 为终点 ID 到对应的属性信息
   */
  protected volatile Map<Integer, Map<Integer, ED>> edges;

  /**
   * 反向边的关系
   */
  protected volatile Map<Integer, Map<Integer, ED>> reverseEdges;

  public Graph() {
    vertices = new HashMap<>();
    edges = new HashMap<>();
    reverseEdges = new HashMap<>();
  }

  /**
   * 增加顶点信息
   *
   * @param id
   * @param vertex
   */
  public synchronized void addVertex(int id, VD vertex) {
    vertices.put(id, vertex);
  }

  /**
   * 增加顶点, 不存在则插入
   *
   * @param id
   * @param vertex
   */
  public synchronized void addVertexIfAbsent(int id, VD vertex) {
    if (!containsVertex(id)) {
      addVertex(id, vertex);
    }
  }

  /**
   * 删除顶点, 需要将关联的边也进行删除
   *
   * @param id
   */
  public synchronized void removeVertex(int id) {
    List<Map.Entry<Integer, Integer>> pairs = new ArrayList<>();

    // 找到所有后继结点
    for (Integer postId : getPostNode(id)) {
      pairs.add(new AbstractMap.SimpleEntry<>(id, postId));
    }

    // 找到所有前续结点
    for (Integer preId : getPreNode(id)) {
      pairs.add(new AbstractMap.SimpleEntry<>(preId, id));
    }

    // 删除结点
    for (Map.Entry<Integer, Integer> pair : pairs) {
      removeEdge(pair.getKey(), pair.getValue());
    }

    vertices.remove(id);
  }

  /**
   * 属性没有值
   *
   * @param startId
   * @param endId
   * @return
   */
  public boolean addEdge(int startId, int endId) {
    return addEdge(startId, endId, false);
  }

  /**
   * 属性没有值
   *
   * @param startId
   * @param endId
   * @param createVertex
   * @return
   */
  public boolean addEdge(int startId, int endId, boolean createVertex) {
    return addEdge(startId, endId, null, createVertex);
  }

  /**
   * 增加边, 注意, 边的起点和终点必须存在, 否则返回失败
   *
   * @param startId
   * @param endId
   * @param edge
   * @return
   */
  public boolean addEdge(int startId, int endId, ED edge) {
    return addEdge(startId, endId, edge, false);
  }

  /**
   * 增加边, 如果边的起始或终止结点不存在, 可以设置是否创建
   *
   * @param startId
   * @param endId
   * @param edge
   * @param createVertex, true 表示创建, 否则表示不创建
   * @return
   */
  public synchronized boolean addEdge(int startId, int endId, ED edge, boolean createVertex) {
    // 需要是否可以加入 startId, endId (普通的 Graph 不需要判断, DAG 需要判断)
    if (!validIfAdd(startId, endId, createVertex)) {
      LOG.error("Add will be invalid, cause cycle.");
      return false;
    }

    addEdgeNoCheck(startId, endId, edge);

    return true;
  }

  /**
   * 增加边, 不做顶点是否存在的检测, 以及不检测边是否是存在的
   *
   * @param startId
   * @param endId
   * @param edge
   * @return
   */
  private synchronized void addEdgeNoCheck(int startId, int endId, ED edge) {
    addVertexIfAbsent(startId, null);
    addVertexIfAbsent(endId, null);

    addEdge(startId, endId, edge, edges);
    addEdge(endId, startId, edge, reverseEdges);
  }

  /**
   * 增加边到具体的数据结构中
   *
   * @param startId
   * @param endId
   * @param edge
   * @param edges
   */
  private synchronized void addEdge(int startId, int endId, ED edge, Map<Integer, Map<Integer, ED>> edges) {
    edges.putIfAbsent(startId, new HashMap<>());
    Map<Integer, ED> endEdges = edges.get(startId);
    endEdges.put(endId, edge);
  }

  /**
   * 删除边
   *
   * @param startId
   * @param endId
   */
  public synchronized void removeEdge(int startId, int endId) {
    removeEdge(startId, endId, edges);
    removeEdge(endId, startId, reverseEdges);
  }

  /**
   * 删除边
   *
   * @param startId
   * @param endId
   * @param edges
   */
  private synchronized void removeEdge(int startId, int endId, Map<Integer, Map<Integer, ED>> edges) {
    Map<Integer, ED> endEdges = edges.get(startId);

    if (endEdges != null) {
      endEdges.remove(endId);

      // 如果删除之后, 为空, 则直接一起删除
      if (endEdges.isEmpty()) {
        edges.remove(startId);
      }
    }
  }

  /**
   * 强制刷新 endId 的前续结点, 该操作会删除所有以 endId 为终结点的边, 并增加 addStartIds.
   * 我们不支持刷新后继结点信息, 是由于这个算法并不高效.
   *
   * @param addStartIds
   * @param endId
   * @return
   */
  public boolean forceRefreshPreEdges(Collection<Integer> addStartIds,
                                      int endId) {
    return forceRefreshPreEdges(addStartIds, endId, false);
  }

  /**
   * @param addStartIds
   * @param endId
   * @param createVertex
   * @return
   */
  public synchronized boolean forceRefreshPreEdges(Collection<Integer> addStartIds,
                                                   int endId,
                                                   boolean createVertex) {
    List<Integer> l = new ArrayList<>();
    l.addAll(getPreNode(endId));

    return addRemoveMultiEdges(l, addStartIds, endId, createVertex);
  }

  /**
   * 一次性增加, 删除多条边, 顶点不存在则失败
   *
   * @param removeStartIds
   * @param addStartIds
   * @param endId
   * @return
   */
  public boolean addRemoveMultiEdges(Collection<Integer> removeStartIds,
                                     Collection<Integer> addStartIds,
                                     int endId) {
    return addRemoveMultiEdges(removeStartIds, addStartIds, endId, false);
  }

  /**
   * 一次性增加, 删除多条边, 顶点不存在可以选择创建
   *
   * @param removeStartIds
   * @param addStartIds
   * @param endId
   * @param createVertex
   * @return
   */
  public synchronized boolean addRemoveMultiEdges(Collection<Integer> removeStartIds,
                                                  Collection<Integer> addStartIds,
                                                  int endId,
                                                  boolean createVertex) {
    for (Integer startId : addStartIds) {
      /**
       * 如果删除和增加操作导致环存在, 则不会进行任何操作, 返回 false.
       * 这里的操作有其特殊性存在, 我们的增加和删除操作的目标结点都是一个, 即 endId, 也就是所有操作都指向了一个目标结点.
       * 我们可以证明, 如果一个图没有环, 那么:
       * 1) 删除边不可能导致环;
       * 2) 如果单独新增 startId{i} -> endId 没有环, 同时新增 startId{i} 必然没有环;
       * 3) 如果单独新增 startId{i} -> endId 导致了环, 那么必然不是由于其它的 startId{i} -> endId 导致的.
       */
      if (!validIfAdd(startId, endId, createVertex)) {
        return false;
      }
    }

    // 删除边
    for (Integer startId : removeStartIds) {
      removeEdge(startId, endId);
    }

    // 新增边
    for (Integer startId : addStartIds) {
      // 这里必定会成功
      addEdgeNoCheck(startId, endId, null);
    }

    return true;
  }

  /**
   * 清空图
   */
  public synchronized void clear() {
    vertices.clear();
    edges.clear();
    reverseEdges.clear();
  }

  /**
   * 是否包含顶点
   *
   * @param id
   * @return
   */
  public synchronized boolean containsVertex(int id) {
    return vertices.containsKey(id);
  }

  /**
   * 获取顶点属性
   *
   * @param id
   * @return
   */
  public synchronized VD getVertex(int id) {
    return vertices.get(id);
  }

  /**
   * 返回所有顶点
   *
   * @return
   */
  public Map<Integer, VD> getVertices() {
    return vertices;
  }

  /**
   * 是否包含边
   *
   * @param startId
   * @param endId
   * @return
   */
  public synchronized boolean containsEdge(int startId, int endId) {
    Map<Integer, ED> endEdges = edges.get(startId);
    if (endEdges == null) {
      return false;
    }

    return endEdges.containsKey(endId);
  }

  /**
   * 返回所有边
   *
   * @return
   */
  public Map<Integer, Map<Integer, ED>> getEdges() {
    return edges;
  }

  /**
   * 获取边的属性
   *
   * @param startId
   * @param endId
   * @return
   */
  public synchronized ED getEdge(int startId, int endId) {
    Map<Integer, ED> endEdges = edges.get(startId);

    if (endEdges == null) {
      return null;
    }

    return endEdges.get(endId);
  }

  /**
   * 获取边的个数
   *
   * @return
   */
  public synchronized int getEdgeNumber() {
    int c = 0;

    for (Map.Entry<Integer, Map<Integer, ED>> entry : edges.entrySet()) {
      c += entry.getValue().size();
    }

    return c;
  }

  /**
   * 获取顶点个数
   *
   * @return
   */
  public int getVertexNumber() {
    return vertices.size();
  }

  /**
   * 获取起始结点
   *
   * @return
   */
  public synchronized Collection<Integer> getStartVertex() {
    return CollectionUtils.subtract(vertices.keySet(), reverseEdges.keySet());
  }

  /**
   * 获取终止结点
   *
   * @return
   */
  public synchronized Collection<Integer> getEndVertex() {
    return CollectionUtils.subtract(vertices.keySet(), edges.keySet());
  }

  /**
   * 获取前置结点
   *
   * @param id
   * @return
   */
  public Set<Integer> getPreNode(int id) {
    return getNeighborNode(id, reverseEdges);
  }

  /**
   * 获取前续结点及其属性
   *
   * @param id
   * @return
   */
  public Map<Integer, ED> getPreNodeAttr(int id) {
    return getNeighborNodeAttr(id, reverseEdges);
  }

  /**
   * 获取后续结点
   *
   * @param id
   * @return
   */
  public Set<Integer> getPostNode(int id) {
    return getNeighborNode(id, edges);
  }

  /**
   * 获取后续结点及其属性
   *
   * @param id
   * @return
   */
  public Map<Integer, ED> getPostNodeAttr(int id) {
    return getNeighborNodeAttr(id, edges);
  }

  /**
   * 获取邻居结点
   *
   * @param id
   * @param edges
   * @return
   */
  private Set<Integer> getNeighborNode(int id, final Map<Integer, Map<Integer, ED>> edges) {
    return getNeighborNodeAttr(id, edges).keySet();
  }

  /**
   * 获取邻居结点及其属性
   *
   * @param id
   * @param edges
   * @return
   */
  private synchronized Map<Integer, ED> getNeighborNodeAttr(int id, final Map<Integer, Map<Integer, ED>> edges) {
    final Map<Integer, ED> neighborEdges = edges.get(id);

    if (neighborEdges == null) {
      return Collections.EMPTY_MAP;
    }

    return neighborEdges;
  }

  /**
   * 获取 id 的入度
   *
   * @param id
   * @return
   */
  public synchronized int getIndegree(int id) {
    Collection<Integer> getNeighborNode = getPreNode(id);
    if (getNeighborNode == null) {
      return 0;
    }

    return getNeighborNode.size();
  }

  /**
   * 返回 id 的出度
   *
   * @param id
   * @return
   */
  public synchronized int getOutdegree(int id) {
    Collection<Integer> getNeighborNode = getPostNode(id);
    if (getNeighborNode == null) {
      return 0;
    }

    return getNeighborNode.size();
  }

  /**
   * 判断增加 startId -> endId 是否合法
   *
   * @param startId
   * @param endId
   * @return
   */
  protected synchronized boolean validIfAdd(int startId, int endId, boolean createVertex) {
    // 不允许自己加到自己
    if (startId == endId) {
      LOG.error("Edge start id can't equals to end id.");
      return false;
    }

    if (!createVertex) {
      return containsVertex(startId) && containsVertex(endId);
    }

    return true;
  }

  /**
   * 判断图是否有环
   *
   * @return true if has cycle, else return false.
   */
  public boolean hasCycle() {
    return !topologicalSortImpl().getKey();
  }

  /**
   * 广度优先遍历, 在无法遍历完成的时候, 是会抛出异常的
   *
   * @return
   */
  public List<Integer> broadFirstSearch() throws Exception {
    List<Integer> visit = new ArrayList<>();
    Queue<Integer> q = new LinkedList<>();
    Set<Integer> hasVisited = new HashSet<>();

    synchronized (this) {
      // 将起始结点加入到队列中
      for (int id : getStartVertex()) {
        q.add(id);
        hasVisited.add(id);
        visit.add(id);
      }

      while (!q.isEmpty()) {
        int id = q.poll();

        // 后续结点加到队列中
        for (int postId : getPostNode(id)) {
          if (!hasVisited.contains(postId)) {
            q.add(postId);
            hasVisited.add(postId);
            visit.add(postId);
          }
        }
      }

      // 遍历完所有结点
      if (visit.size() != getVertexNumber()) {
        throw new Exception("Broad first search can't search complete.");
      }
    }

    return visit;
  }

  /**
   * 深度优先遍历, 在无法遍历完成的时候, 是会抛出异常的
   *
   * @return
   */
  public List<Integer> depthFirstSearch() throws Exception {
    List<Integer> visit = new ArrayList<>();
    Set<Integer> hasVisited = new HashSet<>();

    synchronized (this) {
      for (Integer id : getStartVertex()) {
        depthFirstSearch(id, visit, hasVisited);
      }

      // 遍历完所有结点
      if (visit.size() != getVertexNumber()) {
        throw new Exception("Depth first search can't search complete.");
      }
    }

    return visit;
  }

  /**
   * 深度优先遍历递归
   *
   * @param visit
   * @param hasVisited
   */
  private void depthFirstSearch(int id, Collection<Integer> visit, Set<Integer> hasVisited) {
    visit.add(id);
    hasVisited.add(id);

    for (Integer postId : getPostNode(id)) {
      if (!hasVisited.contains(postId)) {
        depthFirstSearch(postId, visit, hasVisited);
      }
    }
  }

  /**
   * 返回 topological 排序, 如果有环, 会抛出异常
   *
   * @return
   */
  public List<Integer> topologicalSort() throws Exception {
    Map.Entry<Boolean, List<Integer>> entry = topologicalSortImpl();

    if (entry.getKey()) {
      return entry.getValue();
    }

    throw new Exception("Graph has a cycle, can't compute topological sort.");
  }

  /**
   * 返回 topological 排序
   * key 返回的是状态, 如果成功为 true, 失败(如有环)为 false, value 为 topology sort
   *
   * @return
   */
  private Map.Entry<Boolean, List<Integer>> topologicalSortImpl() {
    List<Integer> sort = new ArrayList<>();
    Queue<Integer> zeroVertex = new LinkedList<>();
    Map<Integer, Integer> indegrees = new HashMap<>();

    synchronized (this) {
      // 首先计算每个 vertex 的度, 保存起来
      for (Map.Entry<Integer, VD> id2Vertex : vertices.entrySet()) {
        int id = id2Vertex.getKey();
        int inDegree = getIndegree(id);

        if (inDegree == 0) {
          sort.add(id);
          zeroVertex.add(id);
        } else {
          indegrees.put(id, inDegree);
        }
      }

      // 采用 topology 算法, 删除入度为 0 的顶点, 然后把关联边删除
      while (!zeroVertex.isEmpty()) {
        int id = zeroVertex.poll();
        Collection<Integer> postNodes = getPostNode(id);

        for (int postId : postNodes) {
          int d = indegrees.getOrDefault(postId, 0);

          if (d <= 1) {
            sort.add(postId);
            indegrees.remove(postId);
            zeroVertex.add(postId);
          } else {
            indegrees.put(postId, d - 1);
          }
        }
      }
    }

    // indegrees 如果为空, 表示没有环, 否则表示有环
    return new AbstractMap.SimpleEntry(indegrees.isEmpty(), sort);
  }

  /**
   * 判断图是否是联通的, 判断连通是当做无向图来看待的, 算法描述如下:
   * 1) 以一个点为起点, 采用广度优先遍历的方式对图进行遍历;
   * 2) 如果遍历结束后, 能覆盖所有顶点, 则认为是联通的
   *
   * @return
   */
  public synchronized boolean isConnected() {
    Queue<Integer> q = new LinkedList<>();
    Set<Integer> hasVisited = new HashSet<>();

    // 随机选择一个顶点
    Iterator<Map.Entry<Integer, VD>> iter = vertices.entrySet().iterator();

    // 如果没有下一个结点, 返回 true
    if (!iter.hasNext()) {
      return true;
    }

    // 随机选取一个节点开始遍历
    Map.Entry<Integer, VD> entry = iter.next();

    int startId = entry.getKey();

    q.add(startId);
    hasVisited.add(startId);

    while (!q.isEmpty()) {
      int id = q.poll();

      for (int postId : getPostNode(id)) {
        if (!hasVisited.contains(postId)) {
          q.add(postId);
          hasVisited.add(postId);
        }
      }

      for (int preId : getPreNode(id)) {
        if (!hasVisited.contains(preId)) {
          q.add(preId);
          hasVisited.add(preId);
        }
      }
    }

    return hasVisited.size() == getVertexNumber();
  }
}
