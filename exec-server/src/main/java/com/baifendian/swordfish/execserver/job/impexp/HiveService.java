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
package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.HiveColumn;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.ExecThriftServer;
import com.baifendian.swordfish.execserver.job.impexp.Args.HqlColumn;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.MessageFormat;
import java.util.*;

import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.*;

/**
 * hive 服务类
 * 这不是一个线程安全的类,只能在线程中封闭使用。
 */
public class HiveService {

  private static Logger logger = LoggerFactory.getLogger(HiveService.class);

  private Connection con;

  private String url;

  private String metaUrl;

  private String username;

  private HiveMetaStoreClient hiveMetaStoreClient;

  private String password;

  public HiveService(String url, String metaUrl, String username, String password) {
    if (StringUtils.isEmpty(url) || StringUtils.isEmpty(metaUrl)) {
      logger.error("Url must not be null!");
      throw new IllegalArgumentException("Url must not be null!");
    }
    this.url = url;
    this.metaUrl = metaUrl;
    this.username = username;
    this.password = password;
  }

  /**
   * 初始化hive 服务类
   *
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void init() throws ClassNotFoundException, SQLException, MetaException {
    Class.forName(HIVE_DRIVER);
    //防止重复初始化
    if (con == null) {
      con = DriverManager.getConnection(url, username, password);
    }
    if (hiveMetaStoreClient == null) {
      Configuration hiveConf = new Configuration();
      hiveConf.set("hive.metastore.uris", metaUrl);
      hiveMetaStoreClient = new HiveMetaStoreClient(new HiveConf(hiveConf, HiveConf.class));
    }
  }

  /**
   * 获取临时表名称
   *
   * @return
   */
  @Deprecated
  public String getTbaleName(int projectId, int execId, String jobId) {
    String uuidSuffix = UUID.randomUUID().toString().replace('-', '_');
    return MessageFormat.format("impexp_{0}_{1}_{2}_{3}", String.valueOf(projectId), String.valueOf(execId), jobId, uuidSuffix);
  }

  /**
   * 获取一个hive的表结构
   *
   * @param dbName
   * @param tableName
   * @return
   */
  @Deprecated
  public List<HqlColumn> getHiveDesc(String dbName, String tableName) throws SQLException, TException {
    List<HqlColumn> res = new ArrayList<>();
    List<FieldSchema> fieldSchemaList = hiveMetaStoreClient.getFields(dbName, tableName);
    Table table = hiveMetaStoreClient.getTable(dbName, tableName);
    fieldSchemaList.addAll(table.getPartitionKeys());

    for (FieldSchema fieldSchema : fieldSchemaList) {
      res.add(new HqlColumn(fieldSchema.getName(), fieldSchema.getType()));
    }

    return res;

/*
    //构造查询SQL
    String sql = MessageFormat.format("DESC {0}.{1}", dbName, tableName);
    Statement stmt = null;
    try {
      stmt = con.createStatement();
      ResultSet resultSet = stmt.executeQuery(sql);

      List<HqlColumn> res = new ArrayList<>();
      while (resultSet.next()) {
        String colName = resultSet.getString(1);
        String colType = resultSet.getString(2);
        res.add(new HqlColumn(colName, colType));
      }
      resultSet.close();
      return res;
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }

*/
  }

  /**
   * 检测一个 HiveColumn 是否合法，如果合法就返回hql
   *
   * @return
   */
  @Deprecated
  public List<HqlColumn> checkHiveColumn(List<HiveColumn> srcColumn, List<HqlColumn> destColumn) throws Exception {
    List<HqlColumn> hqlColumnList = new ArrayList<>();
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
        //如果没有找到匹配的抛出异常
        String msg = "Write hive column {0} not found";
        throw new Exception(MessageFormat.format(msg, srcCol.getName()));
      }
    }
    return hqlColumnList;
  }

  /**
   * 组装一个临时表
   *
   * @param
   * @return
   */
  public void createHiveTmpTable(String dbName, String tableName, List<HqlColumn> hqlColumnList, String localtion, String fieldDelimiter, String fileCode) throws SQLException {

    List<String> fieldList = new ArrayList<>();

    for (HqlColumn hqlColumn : hqlColumnList) {
      fieldList.add(MessageFormat.format("{0} {1}", hqlColumn.getName(), hqlColumn.getType()));
    }

    String sql = "CREATE TEMPORARY EXTERNAL TABLE {0}.{1}({2}) ROW FORMAT SERDE \"org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe\" WITH SERDEPROPERTIES(\"field.delim\"=\"{3}\",\"serialization.encoding\"=\"{4}\") STORED AS {5} LOCATION \"{6}\"";

    sql = MessageFormat.format(sql, dbName, tableName, String.join(",", fieldList), fieldDelimiter, fileCode, DEFAULT_FILE_TYPE.getType(), localtion);

    logger.info("Create temp hive table sql: {}", sql);

    Statement stmt = null;
    try {
      logger.info("Start create temp hive table ...");
      stmt = con.createStatement();
      stmt.execute(sql);
      logger.info("Finish create temp hive table!");
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }
  }

  /**
   * 把数据插入表中
   */
  public void insertTable(String srcDbNmae, String srcTableName, String destDbName, String destTableName, List<HqlColumn> srcHqlColumnList, List<HqlColumn> destHqlColumnList, WriteMode writeMode) throws SQLException, TException {
    String insertSql = "INSERT {0} TABLE {1}.{2} {3} SELECT {4} FROM {5}.{6}";
    String partFieldSql = "";

    // 所有的分区都是必传字段先整理出分区字段

    Table destTable = hiveMetaStoreClient.getTable(destDbName, destTableName);
    List<FieldSchema> partFieldList = destTable.getPartitionKeys();

    if (CollectionUtils.isNotEmpty(partFieldList)) {
      List<String> partNameList = new ArrayList<>();
      for (FieldSchema fieldSchema : partFieldList) {
        partNameList.add(fieldSchema.getName());
      }

      partFieldSql = MessageFormat.format("PARTITION({0})", String.join(",", partNameList));
    }


    List<String> fieldList = new ArrayList<>();

    //预处理字段，如果字段为空就加上NULL
    for (HqlColumn destHqlColumn : destHqlColumnList) {
      boolean found = false;
      for (HqlColumn srcHqlColumn : srcHqlColumnList) {
        if (StringUtils.containsIgnoreCase(srcHqlColumn.getName(), destHqlColumn.getName())) {
          fieldList.add(MessageFormat.format("`{0}`", destHqlColumn.getName()));
          found = true;
          break;
        }
      }
      if (!found) {
        fieldList.add(MessageFormat.format("null as `{0}`", destHqlColumn.getName()));
      }
    }

    insertSql = MessageFormat.format(insertSql, writeMode.gethiveSql(), destDbName, destTableName, partFieldSql, String.join(",", fieldList), srcDbNmae, srcTableName);
    logger.info("Insert table sql: {}", insertSql);

    Statement stmt = null;
    try {
      logger.info("Start insert hive table ...");
      stmt = con.createStatement();
      stmt.execute("SET hive.exec.dynamic.partition.mode=nonstrict");
      stmt.execute(insertSql);
      logger.info("Finish insert hive table!");
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }

  }

  public Connection getCon() {
    return con;
  }

  @Deprecated
  public HiveMetaStoreClient getHiveMetaStoreClient() {
    return hiveMetaStoreClient;
  }

  /**
   * 获取一个表的分区字段
   *
   * @param dbName
   * @param table
   * @return
   */
  @Deprecated
  public List<FieldSchema> getPartionField(String dbName, String table) throws TException {
    Table destTable = hiveMetaStoreClient.getTable(dbName, table);
    return destTable.getPartitionKeys();
  }

  /**
   * 获取一个表的普通字段
   *
   * @param dbName
   * @param table
   * @return
   * @throws TException
   */
  @Deprecated
  public List<FieldSchema> getGeneralField(String dbName, String table) throws TException {
    return hiveMetaStoreClient.getFields(dbName, table);
  }

  /**
   * 执行一些sql
   */
  public void execSql(String[] sqlList) throws SQLException {
    Statement stmt = null;
    try {
      logger.info("Start execsql sql list ...");
      stmt = con.createStatement();
      for (String sql : sqlList) {
        stmt.execute(sql);
      }
      logger.info("Finish exec sql!");
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }
  }

  /**
   * 使用完毕关闭连接
   */
  public void close() {
    if (con != null) {
      try {
        con.close();
      } catch (SQLException e) {
        logger.error("Hive con close error", e);
      }
    }
    if (hiveMetaStoreClient != null) {
      hiveMetaStoreClient.close();
    }
  }
}
