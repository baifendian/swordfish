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
package com.baifendian.swordfish.common.job.struct.node.adhoc;

import com.baifendian.swordfish.common.job.struct.node.common.UdfsInfo;
import java.util.List;

/**
 * 即席查询节点参数
 */
public class AdHocParam {
  /**
   * 原始 sql 语句（多条，内部可能包含换行等符号，执行时需要处理）
   */
  private String stms;

  /**
   * udfs 函数
   */
  private List<UdfsInfo> udfs;

  /**
   * 限制返回结果条目
   */
  private Integer limit;

  public String getStms() {
    return stms;
  }

  public void setStms(String stms) {
    this.stms = stms;
  }

  public List<UdfsInfo> getUdfs() {
    return udfs;
  }

  public void setUdfs(List<UdfsInfo> udfs) {
    this.udfs = udfs;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }
}
