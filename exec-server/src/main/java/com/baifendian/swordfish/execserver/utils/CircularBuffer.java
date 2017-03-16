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

package com.baifendian.swordfish.execserver.utils;

import com.google.common.collect.Iterators;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 循环 buffer <p>
 *
 * @author : dsfan
 * @date : 2016年10月27日
 */
public class CircularBuffer<T> implements Iterable<T> {

  /**
   * 行列表
   */
  private final List<T> lines;

  /**
   * 最大行数
   */
  private final int size;

  /**
   * 起始位置
   */
  private int start;

  /**
   * @param size
   */
  public CircularBuffer(int size) {
    this.lines = new CopyOnWriteArrayList<T>();
    this.size = size;
    this.start = 0;
  }

  /**
   * 追加（非线程安全） <p>
   */
  public void append(T line) {
    if (lines.size() < size) {
      lines.add(line);
    } else {
      lines.set(start, line);
      start = (start + 1) % size;
    }
  }

  @Override
  public String toString() {
    return "CircularBuffer [lines=" + lines + ", size=" + size + ", start=" + start + "]";
  }

  @Override
  public Iterator<T> iterator() {
    if (start == 0)
      return lines.iterator();
    else
      return Iterators.concat(lines.subList(start, lines.size()).iterator(), lines.subList(0, start).iterator());
  }

  /**
   * getter method
   *
   * @return the lines
   * @see CircularBuffer#lines
   */
  public List<T> getLines() {
    return lines;
  }

  /**
   * getter method
   *
   * @return the size
   * @see CircularBuffer#size
   */
  public int getSize() {
    return lines.size();
  }

}
