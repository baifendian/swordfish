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
package com.baifendian.swordfish.execserver.job.hive;

import com.baifendian.swordfish.common.hive.ConnectionInfo;
import com.baifendian.swordfish.common.hive.HiveConnectionClient;
import com.baifendian.swordfish.common.job.ExecResult;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.exception.DaoSemanticException;
import com.baifendian.swordfish.dao.exception.SqlException;
import com.baifendian.swordfish.execserver.utils.hive.HiveJdbcExec;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hive.jdbc.HiveConnection;
import org.apache.hive.jdbc.HiveStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger logger = LoggerFactory.getLogger(HiveSqlExec.class);

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
   * 记录 job 的 id
   */
  private final String jobId;

  /**
   * 执行的结果
   */
  private List<ExecResult> results;

  public HiveSqlExec(List<String> createFuncs, List<String> sqls, String userName, boolean isContinue, ResultCallback resultCallback, Integer queryLimit, String jobId) {
    this.createFuncs = createFuncs;
    this.sqls = sqls;
    this.userName = userName;

    this.isContinue = isContinue;
    this.resultCallback = resultCallback;
    this.queryLimit = (queryLimit != null) ? queryLimit : defualtQueryLimit;

    this.hiveJdbcExec = DaoFactory.getDaoInstance(HiveJdbcExec.class);

    this.jobId = jobId;
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

        FlowStatus status = FlowStatus.FAILED; // 执行结果的状态，默认失败
        try {
          if (hiveJdbcExec.isTokQuery(sql) || isLikeShowStm(sql)) {
            sta.setMaxRows(queryLimit);
            ResultSet res = sta.executeQuery(sql);
            // ResultSet res =
            // sta.executeQuery(transferLimitedSql(sql,
            // queryLimit));
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
          if (e.toString().contains("SemanticException")) {
            logger.error("executeQuery DaoSemanticException", e);
            if (!isContinue) {
              throw new DaoSemanticException(e.getMessage());
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
        } finally {
          execResult.setStatus(status);
          //logResult(execResult);

          // 执行结果回调处理
          if (resultCallback != null) {
            Date endTime = new Date();
            resultCallback.handleResult(execResult, startTime, endTime);
          }
        }
      }

      //hiveJdbcExec.useDatabase(projectId, sta);

    } catch (Exception e) {
      logger.error("execute Querys exception", e);
      throw (new SqlException(e.getMessage()));
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

      if (hiveConnection != null) {
        hiveConnectionClient.returnClient(connectionInfo, hiveConnection);
      }
    }

    return execResults;
  }

  /**
   * 是否类似于 show 语句的查询（show/desc/describe） <p>
   *
   * @return true or false
   */
  private boolean isLikeShowStm(String sql) {
    sql = sql.toLowerCase();
    if (sql.startsWith("show") || sql.startsWith("desc")) {
      return true;
    }
    return false;
  }

  /**
   * 解析列名（简称），如果出现同名列，则改名为：列名1，列名2 <p>
   *
   * @return 列名
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
      tmpName = columnName + i++;
    }
    if (!tmpName.equals(columnName)) {
      return tmpName;
    }
    return columnName;
  }

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

  /**
   * 打印执行结果 <p>
   */
  private void logResult(ExecResult execResult) {
    if (execResult.getStatus().typeIsSuccess()) {
      logger.info("执行结果：成功");
      List<String> titles = execResult.getTitles();
      if (CollectionUtils.isNotEmpty(titles)) {
        StringBuilder titleBuilder = new StringBuilder();
        for (String title : titles) {
          titleBuilder.append(title).append("\t");
        }
        logger.info("{}", titleBuilder.toString());
        logger.info("{}", "-------------------------------");

        List<List<String>> valueList = execResult.getValues();
        if (CollectionUtils.isNotEmpty(valueList)) {
          for (List<String> values : valueList) {
            if (CollectionUtils.isNotEmpty(values)) {
              StringBuilder valueBuilder = new StringBuilder();
              for (String value : values) {
                valueBuilder.append(value).append("\t");
              }
              logger.info("{}", valueBuilder.toString());
            }
          }
        }
      }
    } else {
      logger.info("执行结果：失败");
    }
  }

  public List<ExecResult> getResults() {
    return results;
  }

  /**
   * getter method
   *
   * @return the sqls
   */
  public List<String> getSqls() {
    return sqls;
  }

}
