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
package com.baifendian.swordfish.execserver.job.hql;

import com.baifendian.swordfish.common.hive.ConnectionInfo;
import com.baifendian.swordfish.common.hive.HiveConnectionClient;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.exception.DaoSemanticException;
import com.baifendian.swordfish.dao.exception.SqlException;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.common.ResultCallback;
import com.baifendian.swordfish.execserver.engine.hive.HiveJdbcExec;
import com.baifendian.swordfish.execserver.utils.JobLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.hive.jdbc.HiveConnection;
import org.apache.hive.jdbc.HiveStatement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Hive sql执行 <p>
 */
public class HiveSqlExec {
  /**
   * 查询限制，默认为 1000
   */
  private static int defualtQueryLimit = 1000;

  /**
   * create function 语句
   */
  private final List<String> createFuncs;

  /**
   * sql 语句
   */
  private final List<String> sqls;

  /**
   * 执行用户
   */
  private String userName;

  /**
   * 某一句执行失败，是否继续
   */
  private final boolean isContinue;

  /**
   * 执行结果回调处理
   */
  private final ResultCallback resultCallback;

  /**
   * 查询限制
   */
  private final Integer queryLimit;

  /**
   * {@link HiveJdbcExec}
   */
  private final HiveJdbcExec hiveJdbcExec;

  /**
   * 记录日志的实例
   */
  private JobLogger logger;

  /**
   * 执行的结果
   */
  private List<ExecResult> results;

  public HiveSqlExec(List<String> createFuncs, List<String> sqls, String userName, boolean isContinue, ResultCallback resultCallback, Integer queryLimit, JobLogger logger) {
    this.createFuncs = createFuncs;
    this.sqls = sqls;
    this.userName = userName;

    this.isContinue = isContinue;
    this.resultCallback = resultCallback;
    this.queryLimit = (queryLimit != null) ? queryLimit : defualtQueryLimit;

    this.hiveJdbcExec = DaoFactory.getDaoInstance(HiveJdbcExec.class);

    this.logger = logger;
  }

  /**
   * 执行
   *
   * @throws Exception
   */
  public void run() throws Exception {
    results = executeQuerys(createFuncs, sqls, isContinue);
  }

  /**
   * 执行多个sql 语句 并返回查询的语句
   *
   * @param createFuncs
   * @param sqls
   * @param isContinue
   * @return
   */
  public List<ExecResult> executeQuerys(List<String> createFuncs, List<String> sqls, boolean isContinue) {
    List<ExecResult> execResults = new ArrayList<>();

    HiveConnection hiveConnection = null;
    Statement sta = null;
    Thread logThread = null;

    // 得到 hive 的连接信息
    ConnectionInfo connectionInfo = hiveJdbcExec.getConnectionInfo(userName);
    HiveConnectionClient hiveConnectionClient = hiveJdbcExec.getHiveConnectionClient();

    try {
      hiveConnection = hiveConnectionClient.borrowClient(connectionInfo);
      sta = hiveConnection.createStatement();

      // 日志线程
      logThread = new Thread(new JdbcLogRunnable(sta));
      logThread.setDaemon(true);
      logThread.start();

      // 创建临时 function
      if (createFuncs != null) {
        for (String createFunc : createFuncs) {
          logger.info("hive create function sql : {}", createFunc);
          sta.execute(createFunc);
        }
      }

      // 执行 sql 语句
      int index = 0;
      for (String sql : sqls) {
        Date startTime = new Date();

        logger.info("hive execute sql : {}", sql);

        ExecResult execResult = new ExecResult();
        execResult.setIndex(index++);
        execResult.setStm(sql);
        execResults.add(execResult);

        // 执行结果的状态，默认失败
        FlowStatus status = FlowStatus.FAILED;

        try {
          // 只对 query 和 show 语句显示结果
          if (hiveJdbcExec.isTokQuery(sql) || isLikeShowStm(sql)) {
            sta.setMaxRows(queryLimit);
            ResultSet res = sta.executeQuery(sql);

            ResultSetMetaData resultSetMetaData = res.getMetaData();
            int count = resultSetMetaData.getColumnCount();

            List<String> colums = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
              colums.add(parseColumnName(resultSetMetaData.getColumnLabel(i), colums));
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

          // 执行到这里，说明已经执行成功了
          status = FlowStatus.SUCCESS;
        } catch (Exception e) {
          // 语义异常
          if (e.toString().contains("SemanticException")) {
            logger.error("executeQuery DaoSemanticException", e);
            if (!isContinue) {
              throw new DaoSemanticException(e.getMessage());
            }
          }

          // TTransport 异常
          if (e.toString().contains("TTransportException")) {
            logger.error("Get TTransportException return a client", e);
            hiveConnectionClient.invalidateObject(connectionInfo, hiveConnection);
            hiveConnection = null;
            throw new Exception(e);
          }

          // socket 异常
          if (e.toString().contains("SocketException")) {
            logger.error("SocketException clear pool", e);
            hiveConnectionClient.clear();
            throw new Exception(e);
          }

          logger.error("executeQuery Exception", e);
          if (!isContinue) {
            throw new Exception(e);
          }
        } finally {
          execResult.setStatus(status);

          // 执行结果回调处理
          if (resultCallback != null) {
            Date endTime = new Date();

            resultCallback.handleResult(execResult, startTime, endTime);
          }
        }
      }
    } catch (Exception e) {
      logger.error("execute Querys exception", e);
      throw new SqlException(e.getMessage());
    } finally {
      try {
        if (logThread != null) {
          logThread.interrupt();
          logThread.join(HiveJdbcExec.DEFAULT_QUERY_PROGRESS_THREAD_TIMEOUT);
        }
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }

      try {
        if (sta != null) {
          sta.close();
        }
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }

      // 返回连接
      if (hiveConnection != null) {
        hiveConnectionClient.returnClient(connectionInfo, hiveConnection);
      }
    }

    return execResults;
  }

  /**
   * 是否类似于 show 语句的查询（show/desc/describe） <p>
   *
   * @param sql
   * @return 如果是 'show/desc/describe' 语句返回 true, 否则返回 false
   */
  private boolean isLikeShowStm(String sql) {
    if (StringUtils.isEmpty(sql)) {
      return false;
    }

    sql = sql.toLowerCase();

    if (sql.startsWith("show") || sql.startsWith("desc")) {
      return true;
    }

    return false;
  }

  /**
   * 解析列名（简称），如果出现同名列，则改名为：列名1，列名2 <p>
   *
   * @param columnLabel
   * @param colums
   * @return
   */
  private String parseColumnName(String columnLabel, List<String> colums) {
    String columnName;

    if (columnLabel.contains(".")) {
      columnName = columnLabel.substring(columnLabel.lastIndexOf(".") + 1);
    } else {
      columnName = columnLabel;
    }

    // 如果存在相同列名，则列名加序号
    int i = 1;

    String tmpName = columnName;

    while (colums.contains(tmpName)) {
      tmpName = columnName + (i++);
    }

    if (!tmpName.equals(columnName)) {
      return tmpName;
    }

    return columnName;
  }

  public List<ExecResult> getResults() {
    return results;
  }

  /**
   * 打印 jdbc 日志
   */
  private class JdbcLogRunnable implements Runnable {

    private static final int DEFAULT_QUERY_PROGRESS_INTERVAL = 1000;

    private HiveStatement hiveStatement;

    public JdbcLogRunnable(Statement statement) {
      if (statement instanceof HiveStatement) {
        this.hiveStatement = (HiveStatement) statement;
      }
    }

    @Override
    public void run() {
      while (true) {
        try {
          for (String log : hiveStatement.getQueryLog()) {
            logger.info("hive execute log : {}", log);
          }

          Thread.sleep(DEFAULT_QUERY_PROGRESS_INTERVAL);
        } catch (InterruptedException e) {
          showRemainingLogsIfAny(hiveStatement);
          return;
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
          return;
        }
      }
    }

    private void showRemainingLogsIfAny(Statement statement) {
      List<String> logsTemp;
      do {
        try {
          logsTemp = hiveStatement.getQueryLog();
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
          return;
        }
        for (String log : logsTemp) {
          logger.info("hive execute log : {}", log);
        }
      } while (logsTemp.size() > 0);
    }
  }
}
