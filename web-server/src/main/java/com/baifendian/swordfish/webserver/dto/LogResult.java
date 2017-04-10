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
package com.baifendian.swordfish.webserver.dto;

import java.util.Collections;
import java.util.List;

public class LogResult {
  /**
   * 总的大小
   */
  private long total;

  /**
   * 当前长度
   */
  private long length;

  /**
   * 当前日志偏移
   */
  private long offset;

  /**
   * 搜索和解析时间, 毫秒
   */
  private long took;

  /**
   * 日志信息
   */
  private List<String> content = Collections.emptyList();

  public LogResult() {

  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public long getLength() {
    return length;
  }

  public void setLength(long length) {
    this.length = length;
  }

  public List<String> getContent() {
    return content;
  }

  public void setContent(List<String> content) {
    this.content = content;
  }

  public long getOffset() {
    return offset;
  }

  public void setOffset(long offset) {
    this.offset = offset;
  }

  public long getTook() {
    return took;
  }

  public void setTook(long took) {
    this.took = took;
  }
}
