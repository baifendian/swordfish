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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

/**
 * author: smile8
 * date:   12/09/2016
 * desc:   有向无环图, 增加边需要判断是否符合无环的约束, 否则增加失败
 */
public class DAGGraph<VD, ED> extends Graph<VD, ED> {
  private static final Logger LOG = LoggerFactory.getLogger(DAGGraph.class);

  public DAGGraph() {
    super();
  }

  /**
   * 判断增加 startId -> endId 会否导致环存在, 这个算法就是判断从 endId 到 startId 是不是可达的
   *
   * @param startId
   * @param endId
   * @return
   */
  protected synchronized boolean validIfAdd(int startId, int endId, boolean createVertex) {
    if (!super.validIfAdd(startId, endId, createVertex)) {
      return false;
    }

    // 具体算法是, 看 endId 到 startId 是否可达, 如果 endId 本来就不存在, 效率也是非常之高
    int times = getVertexNumber();

    Queue<Integer> q = new LinkedList<>();

    q.add(endId);

    // 循环最多 times - 1 次, 如果没有找到 startId, 表示是不可达的
    while (!q.isEmpty() && (--times > 0)) {
      int id = q.poll();

      for (Integer postId : getPostNode(id)) {
        if (postId == startId) {
          return false;
        }

        q.add(postId);
      }
    }

    return true;
  }

  /**
   * 判断图是否有环, 无向图肯定是没有环的
   *
   * @return true if has cycle, else return false.
   */
  public boolean hasCycle() {
    return false;
  }
}
