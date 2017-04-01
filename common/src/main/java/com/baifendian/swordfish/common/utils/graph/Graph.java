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
 * 这里描述了一个图的结构, 图具有顶点和边, 顶点属性是 VD, 边的属性是 ED, 默认实现是考虑的普通的
 * "有向图结构"
 */
public class Graph<VK, VD, ED> {
  private static final Logger LOG = LoggerFactory.getLogger(Graph.class);

  /**
   * 顶点, key 为顶点, value 为顶点的属性信息
   */
  protected volatile Map<VK, VD> vertices;

  /**
   * 边, key 为起点 , value 为终点到对应的属性信息
   */
  protected volatile Map<VK, Map<VK, ED>> edges;

  /**
   * 反向边的关系
   */
  protected volatile Map<VK, Map<VK, ED>> reverseEdges;

  public Graph() {
    vertices = new HashMap<>();
    edges = new HashMap<>();
    reverseEdges = new HashMap<>();
  }

  /**
   * 增加顶点信息
   *
   * @param key    顶点的 id
   * @param vertex 顶点的属性
   */
  public synchronized void addVertex(VK key, VD vertex) {
    vertices.put(key, vertex);
  }

  /**
   * 增加顶点, 不存在则插入
   *
   * @param key    顶点的 id
   * @param vertex 顶点的属性
   */
  public synchronized void addVertexIfAbsent(VK key, VD vertex) {
    if (!containsVertex(key)) {
      addVertex(key, vertex);
    }
  }

  /**
   * 删除顶点, 需要将关联的边也进行删除
   *
   * @param key 顶点的 id
   */
  public synchronized void removeVertex(VK key) {
    List<Map.Entry<VK, VK>> pairs = new ArrayList<>();

    // 找到所有后继结点
    for (VK postKey : getPostNode(key)) {
      pairs.add(new AbstractMap.SimpleEntry<>(key, postKey));
    }

    // 找到所有前续结点
    for (VK preKey : getPreNode(key)) {
      pairs.add(new AbstractMap.SimpleEntry<>(preKey, key));
    }

    // 删除结点
    for (Map.Entry<VK, VK> pair : pairs) {
      removeEdge(pair.getKey(), pair.getValue());
    }

    vertices.remove(key);
  }

  /**
   * 属性没有值
   *
   * @param start 边的起点
   * @param end   边的终点
   * @return
   */
  public boolean addEdge(VK start, VK end) {
    return addEdge(start, end, false);
  }

  /**
   * @param start        边的起点
   * @param end          边的终点
   * @param createVertex 顶点不存在是否创建
   * @return
   */
  public boolean addEdge(VK start, VK end, boolean createVertex) {
    return addEdge(start, end, null, createVertex);
  }

  /**
   * 增加边, 注意, 边的起点和终点必须存在, 否则返回失败
   *
   * @param start 边的起点
   * @param end   边的终点
   * @param edge  边的属性
   * @return
   */
  public boolean addEdge(VK start, VK end, ED edge) {
    return addEdge(start, end, edge, false);
  }

  /**
   * 增加边, 如果边的起始或终止结点不存在, 可以设置是否创建
   *
   * @param start        边的起点
   * @param end          边的终点
   * @param edge         边的属性
   * @param createVertex 顶点不存在是否创建
   * @return
   */
  public synchronized boolean addEdge(VK start, VK end, ED edge, boolean createVertex) {
    // 需要是否可以加入 startId, endId (普通的 Graph 不需要判断, DAG 需要判断)
    if (!validIfAdd(start, end, createVertex)) {
      LOG.error("Add will be invalid, cause cycle.");
      return false;
    }

    addEdgeNoCheck(start, end, edge);

    return true;
  }

  /**
   * 增加边, 不做顶点是否存在的检测, 以及不检测边是否是存在的
   *
   * @param start 边的起点
   * @param end   边的终点
   * @param edge  边的属性
   */
  private synchronized void addEdgeNoCheck(VK start, VK end, ED edge) {
    addVertexIfAbsent(start, null);
    addVertexIfAbsent(end, null);

    addEdge(start, end, edge, edges);
    addEdge(end, start, edge, reverseEdges);
  }

  /**
   * 增加边到具体的数据结构中
   *
   * @param start 起点
   * @param end   终点
   * @param edge  边的属性
   * @param edges 集合
   */
  private synchronized void addEdge(VK start, VK end, ED edge, Map<VK, Map<VK, ED>> edges) {
    edges.putIfAbsent(start, new HashMap<>());
    Map<VK, ED> endEdges = edges.get(start);
    endEdges.put(end, edge);
  }

  /**
   * 删除边
   *
   * @param start 边的起点
   * @param end   边的终点
   */
  public synchronized void removeEdge(VK start, VK end) {
    removeEdge(start, end, edges);
    removeEdge(end, start, reverseEdges);
  }

  /**
   * 删除边
   *
   * @param start 边的起点
   * @param end   边的终点
   * @param edges 边的属性
   */
  private synchronized void removeEdge(VK start, VK end, Map<VK, Map<VK, ED>> edges) {
    Map<VK, ED> endEdges = edges.get(start);

    if (endEdges != null) {
      endEdges.remove(end);

      // 如果删除之后, 为空, 则直接一起删除
      if (endEdges.isEmpty()) {
        edges.remove(start);
      }
    }
  }

  /**
   * 强制刷新 endId 的前续结点, 该操作会删除所有以 endId 为终结点的边, 并增加 addStartIds. 我们不支持刷新后继结点信息, 是由于这个算法并不高效.
   */
  public boolean forceRefreshPreEdges(Collection<VK> addStartKeys,
                                      VK endKey) {
    return forceRefreshPreEdges(addStartKeys, endKey, false);
  }

  /**
   * @param addStartKeys
   * @param endKey
   * @param createVertex
   * @return
   */
  public synchronized boolean forceRefreshPreEdges(Collection<VK> addStartKeys,
                                                   VK endKey,
                                                   boolean createVertex) {
    List<VK> l = new ArrayList<>();
    l.addAll(getPreNode(endKey));

    return addRemoveMultiEdges(l, addStartKeys, endKey, createVertex);
  }

  /**
   * 一次性增加, 删除多条边, 顶点不存在则失败
   *
   * @param removeStartKeys 删除的起始顶点
   * @param addStartKeys    增加的起始顶点
   * @param endKey          终点
   * @return
   */
  public boolean addRemoveMultiEdges(Collection<VK> removeStartKeys,
                                     Collection<VK> addStartKeys,
                                     VK endKey) {
    return addRemoveMultiEdges(removeStartKeys, addStartKeys, endKey, false);
  }

  /**
   * 一次性增加, 删除多条边, 顶点不存在可以选择创建
   *
   * @param removeStartKeys 删除的起始顶点
   * @param addStartKeys    增加的起始顶点
   * @param endKey          终点
   * @param createVertex    顶点不存在的时候, 是否创建
   * @return
   */
  public synchronized boolean addRemoveMultiEdges(Collection<VK> removeStartKeys,
                                                  Collection<VK> addStartKeys,
                                                  VK endKey,
                                                  boolean createVertex) {
    for (VK startKey : addStartKeys) {
      /**
       * 如果删除和增加操作导致环存在, 则不会进行任何操作, 返回 false.
       * 这里的操作有其特殊性存在, 我们的增加和删除操作的目标结点都是一个, 即 endKey, 也就是所有操作都指向了一个目标结点.
       * 我们可以证明, 如果一个图没有环, 那么:
       * 1) 删除边不可能导致环;
       * 2) 如果单独新增 startKey{i} -> endKey 没有环, 同时新增 startKey{i} 必然没有环;
       * 3) 如果单独新增 startKey{i} -> endKey 导致了环, 那么必然不是由于其它的 startKey{i} -> endKey 导致的.
       */
      if (!validIfAdd(startKey, endKey, createVertex)) {
        return false;
      }
    }

    // 删除边
    for (VK startKey : removeStartKeys) {
      removeEdge(startKey, endKey);
    }

    // 新增边
    for (VK startKey : addStartKeys) {
      // 这里必定会成功
      addEdgeNoCheck(startKey, endKey, null);
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
   * @param key 待检测顶点 id
   * @return
   */
  public synchronized boolean containsVertex(VK key) {
    return vertices.containsKey(key);
  }

  /**
   * 获取顶点属性
   *
   * @param key 待查询顶点 id
   * @return
   */
  public synchronized VD getVertex(VK key) {
    return vertices.get(key);
  }

  /**
   * 返回所有顶点
   *
   * @return
   */
  public Map<VK, VD> getVertices() {
    return vertices;
  }

  /**
   * 是否包含边
   *
   * @param startKey 起始顶点
   * @param endKey   终止顶点
   * @return
   */
  public synchronized boolean containsEdge(VK startKey, VK endKey) {
    Map<VK, ED> endEdges = edges.get(startKey);
    if (endEdges == null) {
      return false;
    }

    return endEdges.containsKey(endKey);
  }

  /**
   * 返回所有边
   *
   * @return
   */
  public Map<VK, Map<VK, ED>> getEdges() {
    return edges;
  }

  /**
   * 获取边的属性
   *
   * @param startKey 起始顶点
   * @param endKey   终止顶点
   * @return
   */
  public synchronized ED getEdge(VK startKey, VK endKey) {
    Map<VK, ED> endEdges = edges.get(startKey);

    if (endEdges == null) {
      return null;
    }

    return endEdges.get(endKey);
  }

  /**
   * 获取边的个数
   *
   * @return
   */
  public synchronized int getEdgeNumber() {
    int c = 0;

    for (Map.Entry<VK, Map<VK, ED>> entry : edges.entrySet()) {
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
  public synchronized Collection<VK> getStartVertex() {
    return CollectionUtils.subtract(vertices.keySet(), reverseEdges.keySet());
  }

  /**
   * 获取终止结点
   *
   * @return
   */
  public synchronized Collection<VK> getEndVertex() {
    return CollectionUtils.subtract(vertices.keySet(), edges.keySet());
  }

  /**
   * 获取前置结点
   *
   * @param key 待计算顶点 id
   * @return
   */
  public Set<VK> getPreNode(VK key) {
    return getNeighborNode(key, reverseEdges);
  }

  /**
   * 获取前续结点及其属性
   *
   * @param key 待计算顶点 id
   * @return
   */
  public Map<VK, ED> getPreNodeAttr(VK key) {
    return getNeighborNodeAttr(key, reverseEdges);
  }

  /**
   * 获取后续结点
   *
   * @param key 待计算顶点 id
   * @return
   */
  public Set<VK> getPostNode(VK key) {
    return getNeighborNode(key, edges);
  }

  /**
   * 获取后续结点及其属性
   *
   * @param key 待计算顶点 id
   * @return
   */
  public Map<VK, ED> getPostNodeAttr(VK key) {
    return getNeighborNodeAttr(key, edges);
  }

  /**
   * 获取邻居结点
   *
   * @param key   待计算顶点 id
   * @param edges 邻边信息
   * @return
   */
  private Set<VK> getNeighborNode(VK key, final Map<VK, Map<VK, ED>> edges) {
    return getNeighborNodeAttr(key, edges).keySet();
  }

  /**
   * 获取邻居结点及其属性
   *
   * @param key   待计算的 id
   * @param edges 连接的顶点
   * @return
   */
  private synchronized Map<VK, ED> getNeighborNodeAttr(VK key, final Map<VK, Map<VK, ED>> edges) {
    final Map<VK, ED> neighborEdges = edges.get(key);

    if (neighborEdges == null) {
      return Collections.EMPTY_MAP;
    }

    return neighborEdges;
  }

  /**
   * 获取 id 的入度
   *
   * @param key 待计算的 id
   * @return
   */
  public synchronized int getIndegree(VK key) {
    Collection<VK> getNeighborNode = getPreNode(key);
    if (getNeighborNode == null) {
      return 0;
    }

    return getNeighborNode.size();
  }

  /**
   * 返回 id 的出度
   *
   * @param key 待计算的 id
   * @return
   */
  public synchronized int getOutdegree(VK key) {
    Collection<VK> getNeighborNode = getPostNode(key);
    if (getNeighborNode == null) {
      return 0;
    }

    return getNeighborNode.size();
  }

  /**
   * 判断增加 startId -> endId 是否合法, 不会执行真正的添加操作
   *
   * @param startKey     起点
   * @param endKey       终点
   * @param createVertex 是否创建顶点
   * @return
   */
  protected synchronized boolean validIfAdd(VK startKey, VK endKey, boolean createVertex) {
    // 不允许自己加到自己
    if (startKey.equals(endKey)) {
      LOG.error("Edge start can't equals to end .");
      return false;
    }

    if (!createVertex) {
      return containsVertex(startKey) && containsVertex(endKey);
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
   * @throws Exception
   */
  public List<VK> broadFirstSearch() throws Exception {
    List<VK> visit = new ArrayList<>();
    Queue<VK> q = new LinkedList<>();
    Set<VK> hasVisited = new HashSet<>();

    synchronized (this) {
      // 将起始结点加入到队列中
      for (VK key : getStartVertex()) {
        q.add(key);
        hasVisited.add(key);
        visit.add(key);
      }

      while (!q.isEmpty()) {
        VK key = q.poll();

        // 后续结点加到队列中
        for (VK postKey : getPostNode(key)) {
          if (!hasVisited.contains(postKey)) {
            q.add(postKey);
            hasVisited.add(postKey);
            visit.add(postKey);
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
   * @throws Exception
   */
  public List<VK> depthFirstSearch() throws Exception {
    List<VK> visit = new ArrayList<>();
    Set<VK> hasVisited = new HashSet<>();

    synchronized (this) {
      for (VK key : getStartVertex()) {
        depthFirstSearch(key, visit, hasVisited);
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
   * @param key        遍历的起始 id
   * @param visit      访问列表
   * @param hasVisited 已经访问的集合
   */
  private void depthFirstSearch(VK key, Collection<VK> visit, Set<VK> hasVisited) {
    visit.add(key);
    hasVisited.add(key);

    for (VK postKey : getPostNode(key)) {
      if (!hasVisited.contains(postKey)) {
        depthFirstSearch(postKey, visit, hasVisited);
      }
    }
  }

  /**
   * 返回 topological 排序, 如果有环, 会抛出异常
   *
   * @return
   * @throws Exception
   */
  public List<VK> topologicalSort() throws Exception {
    Map.Entry<Boolean, List<VK>> entry = topologicalSortImpl();

    if (entry.getKey()) {
      return entry.getValue();
    }

    throw new Exception("Graph has a cycle, can't compute topological sort.");
  }

  /**
   * 返回 topological 排序
   *
   * @return key 返回的是状态, 如果成功为 true, 失败(如有环)为 false, value 为 topology sort
   */
  private Map.Entry<Boolean, List<VK>> topologicalSortImpl() {
    List<VK> sort = new ArrayList<>();
    Queue<VK> zeroVertex = new LinkedList<>();
    Map<VK, Integer> indegrees = new HashMap<>();

    synchronized (this) {
      // 首先计算每个 vertex 的度, 保存起来
      for (Map.Entry<VK, VD> id2Vertex : vertices.entrySet()) {
        VK key = id2Vertex.getKey();
        int inDegree = getIndegree(key);

        if (inDegree == 0) {
          sort.add(key);
          zeroVertex.add(key);
        } else {
          indegrees.put(key, inDegree);
        }
      }

      // 采用 topology 算法, 删除入度为 0 的顶点, 然后把关联边删除
      while (!zeroVertex.isEmpty()) {
        VK key = zeroVertex.poll();
        Collection<VK> postNodes = getPostNode(key);

        for (VK postKey : postNodes) {
          int d = indegrees.getOrDefault(postKey, 0);

          if (d <= 1) {
            sort.add(postKey);
            indegrees.remove(postKey);
            zeroVertex.add(postKey);
          } else {
            indegrees.put(postKey, d - 1);
          }
        }
      }
    }

    // indegrees 如果为空, 表示没有环, 否则表示有环
    return new AbstractMap.SimpleEntry(indegrees.isEmpty(), sort);
  }

  /**
   * 判断图是否是联通的, 判断连通是当做无向图来看待的, 算法描述如下:
   * 1) 以一个点为起点, 采用广度优先遍历的方式对图进行遍历
   * 2) 如果遍历结束后, 能覆盖所有顶点, 则认为是联通的
   *
   * @return true 表示 连通, false 表示不连通
   */
  public synchronized boolean isConnected() {
    Queue<VK> q = new LinkedList<>();
    Set<VK> hasVisited = new HashSet<>();

    // 随机选择一个顶点
    Iterator<Map.Entry<VK, VD>> iter = vertices.entrySet().iterator();

    // 如果没有下一个结点, 返回 true
    if (!iter.hasNext()) {
      return true;
    }

    // 随机选取一个节点开始遍历
    Map.Entry<VK, VD> entry = iter.next();

    VK startKey = entry.getKey();

    q.add(startKey);
    hasVisited.add(startKey);

    while (!q.isEmpty()) {
      VK key = q.poll();

      for (VK postKey : getPostNode(key)) {
        if (!hasVisited.contains(postKey)) {
          q.add(postKey);
          hasVisited.add(postKey);
        }
      }

      for (VK preKey : getPreNode(key)) {
        if (!hasVisited.contains(preKey)) {
          q.add(preKey);
          hasVisited.add(preKey);
        }
      }
    }

    return hasVisited.size() == getVertexNumber();
  }
}
