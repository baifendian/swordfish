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
package com.baifendian.swordfish.execserver.adhoc;

import com.baifendian.swordfish.common.job.BaseParam;
import com.baifendian.swordfish.execserver.job.hive.UdfsInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * sql 节点参数 <p>
 */
public class AdHocParam {

  /**
   * LOGGER
   */
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  /**
   * 原始 sql 语句（多条，内部可能包含换行等符号，执行时需要处理）
   */
  private String stmt;

  private List<UdfsInfo> udfs;

  private Integer limit;

  public String getStmt() {
    return stmt;
  }

  public void setStmt(String stmt) {
    this.stmt = stmt;
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
