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
package com.baifendian.swordfish.execserver.engine.hive;

import com.baifendian.swordfish.common.hive.metastore.HiveMetaPoolClient;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.HiveColumn;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.execserver.job.impexp.Args.HqlColumn;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.slf4j.Logger;

public class HiveMetaExec {

  /**
   * {@link HiveUtil}
   */
  private final HiveUtil hiveUtil;

  /**
   * 记录日志的实例
   */
  private Logger logger;

  /**
   * hive meta 的连接池
   */
  private HiveMetaPoolClient hiveMetaPoolClient;

  public HiveMetaExec(Logger logger) {
    this.logger = logger;

    this.hiveUtil = DaoFactory.getDaoInstance(HiveUtil.class);

    // 初始化一个连接
    this.hiveMetaPoolClient = hiveUtil.getHiveMetaPoolClient();
  }

  /**
   * 获取一个 hive 的表结构
   */
  public List<HqlColumn> getHiveDesc(String dbName, String tableName)
      throws Exception {
    HiveMetaStoreClient hiveMetaStoreClient = hiveMetaPoolClient.borrowClient();
    List<HqlColumn> res = new ArrayList<>();

    try {
      List<FieldSchema> fieldSchemaList = hiveMetaStoreClient.getFields(dbName, tableName);
      Table table = hiveMetaStoreClient.getTable(dbName, tableName);
      fieldSchemaList.addAll(table.getPartitionKeys());

      for (FieldSchema fieldSchema : fieldSchemaList) {
        res.add(new HqlColumn(fieldSchema.getName(), fieldSchema.getType()));
      }
    } finally {
      if (hiveMetaStoreClient != null) {
        hiveMetaPoolClient.returnClient(hiveMetaStoreClient);
      }
    }

    return res;
  }

  /**
   * 检测一个 HiveColumn 是否合法，如果合法就返回hql
   */
  public List<HqlColumn> checkHiveColumn(List<HiveColumn> srcColumn, List<HqlColumn> destColumn)
      throws Exception {
    HiveMetaStoreClient hiveMetaStoreClient = hiveMetaPoolClient.borrowClient();

    List<HqlColumn> hqlColumnList = new ArrayList<>();

    try {
      for (HiveColumn srcCol : srcColumn) {
        boolean found = false;

        for (HqlColumn destCol : destColumn) {
          if (StringUtils.equalsIgnoreCase(srcCol.getName(), destCol.getName())) {
            hqlColumnList.add(destCol);
            found = true;
            break;
          }
        }

        if (!found) {
          // 如果没有找到匹配的抛出异常
          throw new Exception(
              MessageFormat.format("Write hive column {0} not found", srcCol.getName()));
        }
      }
    } finally {
      if (hiveMetaStoreClient != null) {
        hiveMetaPoolClient.returnClient(hiveMetaStoreClient);
      }
    }

    return hqlColumnList;
  }

  /**
   * 获取一个表的分区字段信息
   *
   * @param dbName db 名称
   * @param table 表名称
   */
  public List<FieldSchema> getPartionField(String dbName, String table) throws Exception {
    HiveMetaStoreClient hiveMetaStoreClient = hiveMetaPoolClient.borrowClient();

    try {
      Table destTable = hiveMetaStoreClient.getTable(dbName, table);

      return destTable.getPartitionKeys();
    } finally {
      if (hiveMetaStoreClient != null) {
        hiveMetaPoolClient.returnClient(hiveMetaStoreClient);
      }
    }
  }

  /**
   * 获取一个表的普通字段
   *
   * @param dbName db 名称
   * @param table 表名称
   */
  public List<FieldSchema> getGeneralField(String dbName, String table) throws Exception {
    HiveMetaStoreClient hiveMetaStoreClient = hiveMetaPoolClient.borrowClient();

    try {
      return hiveMetaStoreClient.getFields(dbName, table);
    } finally {
      if (hiveMetaStoreClient != null) {
        hiveMetaPoolClient.returnClient(hiveMetaStoreClient);
      }
    }
  }
}
