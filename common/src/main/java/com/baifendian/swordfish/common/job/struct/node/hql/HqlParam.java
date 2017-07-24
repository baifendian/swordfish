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
package com.baifendian.swordfish.common.job.struct.node.hql;

import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.common.UdfsInfo;
import com.baifendian.swordfish.dao.enums.SqlEngineType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * sql 节点参数 <p>
 */
public class HqlParam extends BaseParam {

  /**
   * 原始 sql 语句（多条，内部可能包含换行等符号，执行时需要处理）
   */
  private String sql;

  /**
   * 执行类型
   */
  private SqlEngineType type = SqlEngineType.HIVE;

  /**
   * udfs 函数列表
   */
  private List<UdfsInfo> udfs;

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public SqlEngineType getType() {
    return type;
  }

  public void setType(SqlEngineType type) {
    this.type = type;
  }

  public List<UdfsInfo> getUdfs() {
    return udfs;
  }

  public void setUdfs(List<UdfsInfo> udfs) {
    this.udfs = udfs;
  }

  @Override
  public boolean checkValid() {
    return StringUtils.isNotEmpty(sql) && type != null;
  }

  @Override
  public List<String> getProjectResourceFiles() {
    if (CollectionUtils.isNotEmpty(udfs)) {
      List<String> resFiles = new ArrayList<>();

      for (UdfsInfo udfsInfo : udfs) {
        addProjectResourceFiles(udfsInfo.getLibJars(), resFiles);
      }

      return resFiles;
    }

    return null;
  }
}
