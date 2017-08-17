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

import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.DEFAULT_FILE_TYPE;

import com.baifendian.swordfish.common.hive.metastore.HiveMetaPoolClient;
import com.baifendian.swordfish.common.hive.service2.HiveService2Client;
import com.baifendian.swordfish.common.hive.service2.HiveService2ConnectionInfo;
import com.baifendian.swordfish.dao.BaseDao;
import com.baifendian.swordfish.execserver.job.impexp.Args.HqlColumn;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HiveUtil extends BaseDao {

  private static Logger logger = LoggerFactory.getLogger(HiveUtil.class);

  public static final int DEFAULT_QUERY_PROGRESS_THREAD_TIMEOUT = 10 * 1000;

  // 配置信息
  @Autowired
  HiveConfig hiveConfig;

  // hive service2 的客户端连接
  @Autowired
  HiveService2Client hiveService2Client;

  // hive meta 的客户端连接
  @Autowired
  HiveMetaPoolClient hiveMetaPoolClient;

  /**
   * 采用非注解方式的时候, 需要自己获取这些实例
   */
  @Override
  public void init() {
    hiveConfig = MyHiveFactoryUtil.getInstance();
    hiveService2Client = hiveConfig.hiveService2Client();
    hiveMetaPoolClient = hiveConfig.hiveMetaPoolClient();

    logger.info("Hive config, thrift uri:{}, meta uri:{}", hiveConfig.getThriftUris(),
        hiveConfig.getMetastoreUris());
  }

  /**
   * 获取临时表名称
   *
   * @param projectId 项目 id
   * @param execId 执行 id
   */
  public static String getTmpTableName(int projectId, int execId) {
    String uuidSuffix = UUID.randomUUID().toString().replace('-', '_');

    return MessageFormat
        .format("impexp_{0}_{1}_{2}", String.valueOf(projectId), String.valueOf(execId),
            uuidSuffix);
  }


  /**
   * 组装一个ORC存储的临时表
   */
  public static String getORCTmpTableDDL(String dbName, String tableName,
      List<HqlColumn> hqlColumnList, String localtion) {
    List<String> fieldList = new ArrayList<>();

    for (HqlColumn hqlColumn : hqlColumnList) {
      fieldList.add(MessageFormat.format("{0} {1}", hqlColumn.getName(), hqlColumn.getType()));
    }

    String sql = "CREATE TEMPORARY EXTERNAL TABLE {0}.{1}({2}) STORED AS orc LOCATION \"{3}\"";

    sql = MessageFormat
        .format(sql, dbName, tableName, String.join(",", fieldList), localtion);

    return sql;
  }

  /**
   * 组装一个临时外部表
   */
  public static String getTmpTableDDL(String dbName, String tableName,
      List<HqlColumn> hqlColumnList, String localtion, String fieldDelimiter, String fileCode)
      throws SQLException {

    List<String> fieldList = new ArrayList<>();

    for (HqlColumn hqlColumn : hqlColumnList) {
      fieldList.add(MessageFormat.format("{0} {1}", hqlColumn.getName(), hqlColumn.getType()));
    }

    String sql = "CREATE TEMPORARY EXTERNAL TABLE {0}.{1}({2}) ROW FORMAT SERDE \"org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe\" WITH SERDEPROPERTIES(\"field.delim\"=\"{3}\",\"serialization.encoding\"=\"{4}\") STORED AS textfile LOCATION \"{5}\"";

    sql = MessageFormat
        .format(sql, dbName, tableName, String.join(",", fieldList), fieldDelimiter, fileCode
            , localtion);

    return sql;
  }


  /**
   * 判断是否是查询请求
   */
  public static boolean isTokQuery(String sql) {
    if (StringUtils.isEmpty(sql)) {
      return false;
    }

    sql = sql.toUpperCase();

    if (sql.startsWith("SELECT")) {
      return true;
    }

    return false;
  }


  /**
   * 是否是 DDL 语句, 如 create table 等
   */
  public static boolean isTokDDL(String sql) {
    if (org.apache.commons.lang3.StringUtils.isEmpty(sql)) {
      return false;
    }

    String tmp = sql.toUpperCase();

    if (tmp.startsWith("CREATE") || tmp.startsWith("DROP") || tmp.startsWith("ALTER")) {
      return true;
    }

    return false;
  }

  /**
   * 是否类似于 show 语句的查询（show/desc/describe） <p>
   *
   * @return 如果是 'show/desc/describe' 等语句返回 true, 否则返回 false
   */
  public static boolean isLikeShowStm(String sql) {
    if (StringUtils.isEmpty(sql)) {
      return false;
    }

    sql = sql.toUpperCase();

    if (sql.startsWith("SHOW") ||
        sql.startsWith("ANALYZE") ||
        sql.startsWith("EXPLAIN") ||
        sql.startsWith("DESC") ||
        sql.startsWith("DESCRIBE")) {
      return true;
    }

    return false;
  }

  public HiveService2Client getHiveService2Client() {
    return hiveService2Client;
  }

  public HiveMetaPoolClient getHiveMetaPoolClient() {
    return hiveMetaPoolClient;
  }

  /**
   * 获取连接信息 <p>
   *
   * @see {@link HiveService2ConnectionInfo}
   */
  public HiveService2ConnectionInfo getHiveService2ConnectionInfo(String userName) {
    HiveService2ConnectionInfo hiveService2ConnectionInfo = new HiveService2ConnectionInfo();

    hiveService2ConnectionInfo.setUser(userName);
    hiveService2ConnectionInfo.setPassword("");
    hiveService2ConnectionInfo.setUri(hiveConfig.getThriftUris());

    return hiveService2ConnectionInfo;
  }
}
