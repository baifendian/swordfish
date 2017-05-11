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

import com.baifendian.swordfish.common.hive.ConnectionInfo;
import com.baifendian.swordfish.common.hive.HiveConnectionClient;
import com.baifendian.swordfish.dao.BaseDao;
import com.baifendian.swordfish.dao.exception.DaoSemanticException;
import com.baifendian.swordfish.dao.exception.SqlException;
import com.baifendian.swordfish.execserver.common.ExecResult;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hive.jdbc.HiveConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class HiveJdbcExec extends BaseDao {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public static final int DEFAULT_QUERY_PROGRESS_THREAD_TIMEOUT = 10 * 1000;

  @Autowired
  HiveConfig hiveConfig;

  @Autowired
  HiveConnectionClient hiveConnectionClient;

//  ParseDriver pd = new ParseDriver();

  /**
   * 判断是否是查询请求
   *
   * @param sql
   * @return
   */
  public static boolean isTokQuery(String sql) {
    if (StringUtils.isEmpty(sql)) {
      return false;
    }

    sql = sql.toLowerCase();

    if (sql.startsWith("select")
        || sql.startsWith("describe")
        || sql.startsWith("explain")) {
      return true;
    }

    return false;
  }

  /**
   * 是否类似于 show 语句的查询（show/desc/describe） <p>
   *
   * @param sql
   * @return 如果是 'show/desc/describe' 语句返回 true, 否则返回 false
   */
  public static boolean isLikeShowStm(String sql) {
    if (StringUtils.isEmpty(sql)) {
      return false;
    }

    sql = sql.toLowerCase();

    if (sql.startsWith("show") || sql.startsWith("desc")) {
      return true;
    }

    return false;
  }

  @Override
  public void init() {
    hiveConfig = MyHiveFactoryUtil.getInstance();
    hiveConnectionClient = hiveConfig.hiveConnectionClient();
  }

  public HiveConnectionClient getHiveConnectionClient() {
    return hiveConnectionClient;
  }

  /**
   * 获取连接信息 <p>
   *
   * @param userName
   * @return
   * @see {@link ConnectionInfo}
   */
  public ConnectionInfo getConnectionInfo(String userName) {
    ConnectionInfo connectionInfo = new ConnectionInfo();

    connectionInfo.setUser(userName);
    connectionInfo.setUri(hiveConfig.getThriftUris());

    return connectionInfo;
  }

  /**
   * 执行一条 sql 语句 不支持 use database
   *
   * @param sql
   * @param connectionInfo
   * @throws SqlException
   */
  public void execSql(String sql, ConnectionInfo connectionInfo) throws SqlException {
    HiveConnection hiveConnection = null;
    try {
      hiveConnection = hiveConnectionClient.borrowClient(connectionInfo);
      Statement sta = hiveConnection.createStatement();
      sta.execute(sql);
      sta.close();
    } catch (Exception e) {
      if (e.toString().contains("SemanticException")) {
        logger.error("execSql DaoSemanticException", e);
        throw (new DaoSemanticException(e.getMessage()));
      }

      if (e.toString().contains("TTransportException")) {
        logger.error("Get TTransportException return a client", e);
        hiveConnectionClient.invalidateObject(connectionInfo, hiveConnection);
        hiveConnection = null;
      }

      if (e.toString().contains("SocketException")) {
        logger.error("SocketException clear pool", e);
        hiveConnectionClient.clear();
      }

      logger.error("execSql SqlException", e);
      throw (new SqlException(e.getMessage()));
    } finally {
      hiveConnectionClient.returnClient(connectionInfo, hiveConnection);
    }
  }

  public void useDatabase(String dbName, Statement sta) throws SQLException {
    String sql = String.format("use %s", dbName);
    sta.execute(sql);
  }

  /**
   * 执行多个sql 语句 并返回查询的语句
   * <p>
   *
   * @param sqls
   * @param userId
   * @param dbName
   * @param isContinue
   * @param isGetLog
   * @return
   */
    /*
    public List<ExecResult> execute(List<String> sqls, int userId, String dbName, boolean isContinue, boolean isGetLog) {
        List<ExecResult> execResults = new ArrayList<>();
        HiveConnection hiveConnection = null;
        Statement sta = null;
        Thread logThread = null;
        ConnectionInfo connectionInfo = getConnectionInfo(userId, dbName);

        try {
            hiveConnection = hiveConnectionClient.borrowClient(connectionInfo);
            sta = hiveConnection.createStatement();
            List<String> logs = new ArrayList<>();
            if (isGetLog) {
                logThread = new Thread(new JdbcLogRunnable(sta, logs));
                logThread.setDaemon(true);
                logThread.start();
            }
            for (String sql : sqls) {
                if (sql.trim().startsWith("#") || sql.trim().startsWith("--")) {
                    continue;
                }
                ExecResult execResult = new ExecResult();
                execResult.setStm(sql);
                execResults.add(execResult);
                logs.clear();
                try {
                    if (isTokQuery(sql)) {

                        ResultSet res = sta.executeQuery(sql);
                        ResultSetMetaData resultSetMetaData = res.getMetaData();
                        int count = resultSetMetaData.getColumnCount();

                        List<String> colums = new ArrayList<>();
                        for (int i = 1; i <= count; i++) {
                            colums.add(resultSetMetaData.getColumnLabel(i));
                        }
                        execResult.setTitles(colums);

                        List<List<String>> datas = new ArrayList<>();
                        while (res.next()) {
                            List<String> values = new ArrayList<>();
                            for (int i = 1; i <= count; i++) {
                                values.add(res.getString(i));
                            }
                            datas.add(values);
                        }

                        execResult.setValues(datas);

                    } else {
                        sta.execute(sql);
                    }
                    for (String log : logs) {
                        execResult.getLogs().add(log);
                    }
                } catch (Exception e) {
                    if (e.toString().contains("SemanticException")) {
                        logger.error("executeQuery DaoSemanticException", e);
                        if (!isContinue) {
                            throw (new DaoSemanticException(e.getMessage()));
                        }
                    }
                    if (e.toString().contains("TTransportException")) {
                        logger.error("Get TTransportException return a client", e);
                        hiveConnectionClient.invalidateObject(connectionInfo, hiveConnection);
                        hiveConnection = null;
                        throw new Exception(e);
                    }
                    if (e.toString().contains("SocketException")) {
                        logger.error("SocketException clear pool", e);
                        hiveConnectionClient.clear();
                        throw new Exception(e);
                    }
                    logger.error("executeQuery Exception", e);
                    if (!isContinue) {
                        throw new Exception(e);
                    }
                    if (isGetLog) {
                        execResult.getLogs().add(e.getMessage());
                    }
                }
            }
            try {
                if (logThread != null) {
                    logThread.interrupt();
                    logThread.join(DEFAULT_QUERY_PROGRESS_THREAD_TIMEOUT);
                }
                logThread = null;
            } catch (Exception e) {
            }
            useDatabase(dbName, sta);
            sta.close();
            sta = null;
        } catch (Exception e) {
            logger.error("execute exception", e);
            throw (new SqlException(e.getMessage()));
        } finally {
            try {
                if (logThread != null) {
                    logThread.interrupt();
                    logThread.join(DEFAULT_QUERY_PROGRESS_THREAD_TIMEOUT);
                }
                if (sta != null) {
                    sta.close();
                }
            } catch (Exception e) {
            }
            hiveConnectionClient.returnClient(connectionInfo, hiveConnection);
        }

        return execResults;
    }
*/

  /**
   * 执行一个sql 语句 并返回查询的语句
   *
   * @param sql
   * @param connectionInfo
   * @return
   * @throws SqlException
   */
  public ExecResult executeQuery(String sql, ConnectionInfo connectionInfo) throws SqlException {
    HiveConnection hiveConnection = null;

    try {
      hiveConnection = hiveConnectionClient.borrowClient(connectionInfo);
      Statement sta = hiveConnection.createStatement();
      ExecResult execResult = new ExecResult();
      execResult.setStm(sql);
      ResultSet res = sta.executeQuery(sql);
      ResultSetMetaData resultSetMetaData = res.getMetaData();

      int count = resultSetMetaData.getColumnCount();

      List<String> colums = new ArrayList<>();
      for (int i = 1; i <= count; i++) {
        colums.add(resultSetMetaData.getColumnLabel(i));
      }
      execResult.setTitles(colums);

      List<List<String>> datas = new ArrayList<>();
      while (res.next()) {
        List<String> values = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
          values.add(res.getString(i));
        }
        datas.add(values);
      }

      execResult.setValues(datas);
      sta.close();
      return execResult;
    } catch (Exception e) {
      if (e.toString().contains("SemanticException")) {
        logger.error("execSql DaoSemanticException", e);
        throw (new DaoSemanticException(e.getMessage()));
      }
      if (e.toString().contains("TTransportException")) {
        logger.error("Get TTransportException return a client", e);
        hiveConnectionClient.invalidateObject(connectionInfo, hiveConnection);
        hiveConnection = null;
      }
      if (e.toString().contains("SocketException")) {
        logger.error("SocketException clear pool", e);
        hiveConnectionClient.clear();
      }
      logger.error("execSql SqlException", e);
      throw (new SqlException(e.getMessage()));
    } finally {
      hiveConnectionClient.returnClient(connectionInfo, hiveConnection);
    }
  }
}
