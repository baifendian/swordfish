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

import com.baifendian.swordfish.common.hive.service2.HiveService2Client;
import com.baifendian.swordfish.common.hive.service2.HiveService2ConnectionInfo;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.dao.exception.DaoSemanticException;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.common.ResultCallback;
import com.baifendian.swordfish.execserver.engine.SqlUtil;
import com.baifendian.swordfish.execserver.utils.Constants;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import org.apache.hive.jdbc.HiveConnection;
import org.apache.hive.jdbc.HiveStatement;
import org.apache.hive.service.cli.HiveSQLException;
import org.slf4j.Logger;

/**
 * Hive sql执行 <p>
 */
public class HiveSqlExec {

  /**
   * 查询限制，默认为 1000
   */
  private static int defaultQueryLimit = 1000;

  /**
   * {@link HiveUtil}
   */
  private final HiveUtil hiveUtil;

  /**
   * 日志处理器
   */
  private Consumer<List<String>> logHandler;

  /**
   * 执行用户
   */
  private String userName;

  /**
   * 记录日志的实例
   */
  private Logger logger;

  public HiveSqlExec(Consumer<List<String>> logHandler, String userName, Logger logger) {
    this.logHandler = logHandler;
    this.userName = userName;
    this.logger = logger;

    this.hiveUtil = DaoFactory.getDaoInstance(HiveUtil.class);
  }

  /**
   * 执行多个 sql 语句 并返回查询的语句, 注意, 多次调用 execute, 上下文是不相关的
   *
   * @param createFuncs 创建自定义函数语句
   * @param sqls 执行的 sql
   * @param isContinue 遇到错误, 是否继续执行下一条语句
   * @param resultCallback 回调, 执行的结果处理
   * @param queryLimit 结果限制
   * @param remainTime 剩余运行时间, 暂没实现
   */
  public boolean execute(List<String> createFuncs, List<String> sqls,
      boolean isContinue, ResultCallback resultCallback, Integer queryLimit, int remainTime) {

    // 没有剩余运行的时间
    if (remainTime <= 0) {
      return false;
    }

    // 查询结果限制
    queryLimit = (queryLimit != null) ? queryLimit : defaultQueryLimit;

    HiveConnection hiveConnection = null;
    Statement sta = null;
    Thread logThread = null;

    // 得到 hive 的连接信息
    HiveService2ConnectionInfo hiveService2ConnectionInfo = hiveUtil
        .getHiveService2ConnectionInfo(userName);

    logger.info("execution connection information:{}", hiveService2ConnectionInfo);

    HiveService2Client hiveService2Client = hiveUtil.getHiveService2Client();

    try {
      try {
        hiveConnection = hiveService2Client.borrowClient(hiveService2ConnectionInfo);

        sta = hiveConnection.createStatement();
//        sta.setQueryTimeout(remainTime);

        // 日志线程
        logThread = new Thread(new JdbcLogRunnable(sta));
        logThread.setDaemon(true);
        logThread.start();

        // 创建临时 function
        if (createFuncs != null) {
          for (String createFunc : createFuncs) {
            logger.info("hive create function sql: {}", createFunc);
            sta.execute(createFunc);
          }
        }
      } catch (Exception e) {
        logger.error("execute query exception", e);

        // 这里就失败了, 会记录下错误记录, 然后返回
        SqlUtil.handlerResults(0, sqls, FlowStatus.FAILED, resultCallback);

        return false;
      }

      // 执行 sql 语句
      for (int index = 0; index < sqls.size(); ++index) {
        String sql = sqls.get(index);

        Date startTime = new Date();

        logger.info("hive execute sql: {}", sql);

        ExecResult execResult = new ExecResult();
        execResult.setIndex(index);
        execResult.setStm(sql);

        try {
          // 只对 query 和 show 语句显示结果
          if (HiveUtil.isTokQuery(sql) || HiveUtil.isLikeShowStm(sql)) {
            sta.setMaxRows(queryLimit);
            ResultSet res = sta.executeQuery(sql);

            ResultSetMetaData resultSetMetaData = res.getMetaData();
            int count = resultSetMetaData.getColumnCount();

            List<String> colums = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
              colums.add(resultSetMetaData.getColumnLabel(i)/*parseColumnName(resultSetMetaData.getColumnLabel(i), colums)*/);
            }

            execResult.setTitles(colums);

            List<List<String>> datas = new ArrayList<>();

            // 如果字段数大于 1, 或是 query 语句
            if (count > 1 || HiveUtil.isTokQuery(sql)) {
              while (res.next()) {
                List<String> values = new ArrayList<>();
                for (int i = 1; i <= count; ++i) {
                  values.add(res.getString(i));
                }

                datas.add(values);
              }
            } else {
              StringBuffer buffer = new StringBuffer();

              while (res.next()) {
                buffer.append(res.getString(1));
                buffer.append("\n");
              }

              List<String> values = new ArrayList<>();
              values.add(buffer.toString().trim());

              datas.add(values);
            }

            execResult.setValues(datas);
          } else {
            sta.execute(sql);
          }

          // 执行到这里，说明已经执行成功了
          execResult.setStatus(FlowStatus.SUCCESS);

          // 执行结果回调处理
          if (resultCallback != null) {
            Date endTime = new Date();
            resultCallback.handleResult(execResult, startTime, endTime);
          }
        } catch (SQLTimeoutException e) {
          // sql 超时异常
          logger.error("executeQuery timeout exception", e);

          SqlUtil.handlerResults(index, sqls, FlowStatus.FAILED, resultCallback);
          return false;
        } catch (DaoSemanticException | HiveSQLException e) {
          // 语义异常
          logger.error("executeQuery exception", e);

          if (isContinue) {
            SqlUtil.handlerResult(index, sql, FlowStatus.FAILED, resultCallback);
          } else {
            SqlUtil.handlerResults(index, sqls, FlowStatus.FAILED, resultCallback);
            return false;
          }
        } catch (Exception e) {
          // TTransport 异常
          if (e.toString().contains("TTransportException")) {
            logger.error("Get TTransportException return a client", e);
            // 这个不需要处理
//            hiveService2Client.invalidateObject(hiveService2ConnectionInfo, hiveConnection);
            SqlUtil.handlerResults(index, sqls, FlowStatus.FAILED, resultCallback);
            return false;
          }

          // socket 异常
          if (e.toString().contains("SocketException")) {
            logger.error("SocketException clear pool", e);
            hiveService2Client.clear();
            SqlUtil.handlerResults(index, sqls, FlowStatus.FAILED, resultCallback);
            return false;
          }

          logger.error("executeQuery exception", e);

          if (isContinue) {
            SqlUtil.handlerResult(index, sql, FlowStatus.FAILED, resultCallback);
          } else {
            SqlUtil.handlerResults(index, sqls, FlowStatus.FAILED, resultCallback);
            return false;
          }
        }
      }
    } finally {
      // 关闭连接
      try {
        if (sta != null) {
          sta.close();
        }
      } catch (Exception e) {
        logger.error("Catch an exception", e);
      }

      try {
        // 返回连接
        if (hiveConnection != null) {
          // 关闭
          hiveConnection.close();

          // 返回连接, 这里不再返回
          // hiveService2Client.returnClient(hiveService2ConnectionInfo, hiveConnection);
        }
      } catch (Exception e) {
        logger.error("Catch an exception", e);
      }

      // 关闭日志
      try {
        if (logThread != null) {
          logThread.interrupt();
          logThread.join(HiveUtil.DEFAULT_QUERY_PROGRESS_THREAD_TIMEOUT);
        }
      } catch (Exception e) {
//        logger.error("Catch an exception", e);
      }
    }

    return true;
  }

  /**
   * 打印 jdbc 日志
   */
  private class JdbcLogRunnable implements Runnable {

    private static final int DEFAULT_QUERY_PROGRESS_INTERVAL = 1000;

    private HiveStatement hiveStatement;
    private List<String> logs;

    public JdbcLogRunnable(Statement statement) {
      if (statement instanceof HiveStatement) {
        this.hiveStatement = (HiveStatement) statement;
      }

      this.logs = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void run() {
      if (hiveStatement == null) {
        return;
      }

      // 当前 flush 的时间
      long preFlushTime = System.currentTimeMillis();

      synchronized (this) { // 不能异步处理
        while (true) {
          try {
            for (String log : hiveStatement.getQueryLog()) {
              logs.add(log);

              long now = System.currentTimeMillis();

              // 到一定日志量就输出处理
              if (logs.size() >= Constants.defaultLogBufferSize
                  || now - preFlushTime > Constants.defaultLogFlushInterval) {
                preFlushTime = now;

                // 日志处理器
                logHandler.accept(logs);

                logs.clear();
              }
            }

            Thread.sleep(DEFAULT_QUERY_PROGRESS_INTERVAL);
          } catch (SQLException e) {
            return;
          } catch (InterruptedException e) {
//            logger.error(e.getMessage(), e);
            return;
          } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
          } finally {
            // 处理剩余日志
            showRemainingLogsIfAny();
          }
        }
      }
    }

    private void showRemainingLogsIfAny() {
      List<String> logsTemp;
      do {
        try {
          logsTemp = hiveStatement.getQueryLog();
        } catch (Exception e) {
          /*logger.error(e.getMessage(), e);*/
          return;
        }

        for (String log : logsTemp) {
          logs.add(log);

          // 日志处理器
          logHandler.accept(logs);

          logs.clear();
        }
      } while (logsTemp.size() > 0);
    }
  }
}
