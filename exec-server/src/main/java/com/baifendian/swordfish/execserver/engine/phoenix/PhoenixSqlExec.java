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

package com.baifendian.swordfish.execserver.engine.phoenix;

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.common.ResultCallback;
import com.baifendian.swordfish.execserver.engine.SqlUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

/**
 * 官方推荐，不使用连接池。详情请参见： https://phoenix.apache.org/faq.html#Should_I_pool_Phoenix_JDBC_Connections
 */
public class PhoenixSqlExec {

  /**
   * 查询限制，默认为 1000
   */
  private static int defaultQueryLimit = 1000;

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


  public PhoenixSqlExec(Consumer<List<String>> logHandler, String userName, Logger logger) {
    this.logHandler = logHandler;
    this.userName = userName;
    this.logger = logger;
  }

  /**
   * 执行多个 sql 语句 并返回查询的语句, 注意, 多次调用 execute, 上下文是不相关的
   *
   * @param createFuncs 创建自定义函数语句
   * @param sqls 执行的 sql
   * @param resultCallback 回调, 执行的结果处理
   * @param queryLimit 结果限制
   * @param remainTime 剩余运行时间, 暂没实现
   */
  public boolean execute(List<String> createFuncs, List<String> sqls, boolean isContinue,
      ResultCallback resultCallback, Integer queryLimit, int remainTime) throws SQLException {
    // 没有剩余运行的时间
    if (remainTime <= 0) {
      return false;
    }

    // 查询结果限制
    queryLimit = (queryLimit != null) ? queryLimit : defaultQueryLimit;

    try (Connection con = PhoenixUtil.getPhoenixConnection(userName, remainTime)) {
      if (con == null) {
        logger.error("Failed to get phoenix connect");
        return false;
      }

      try (Statement sta = con.createStatement()) {

        if (CollectionUtils.isNotEmpty(createFuncs)) {
          try {
            for (String sql : createFuncs) {
              logger.info("Phoenix create function sql: {}", sql);
              sta.execute(sql);
            }
          } catch (SQLException e) {
            logger.error("Phoenix execute query exception", e);

            // 这里就失败了, 会记录下错误记录, 然后返回
            SqlUtil.handlerResults(0, sqls, FlowStatus.FAILED, resultCallback);

            return false;
          }
        }

        // 日志线程
        // 执行 sql 语句
        for (int index = 0; index < sqls.size(); ++index) {
          String sql = sqls.get(index);
          Date startTime = new Date();

          logger.info("Phoenix execute sql: {}", sql);
          ExecResult execResult = new ExecResult();
          execResult.setIndex(index);
          execResult.setStm(sql);
          try {
            SqlUtil.execSql(sql, sta, queryLimit, execResult);
            con.commit();
            // 执行结果回调处理
            if (resultCallback != null) {
              Date endTime = new Date();
              resultCallback.handleResult(execResult, startTime, endTime);
            }
          } catch (SQLException e) {
            // 语义异常
            logger.error("Phoenix execute query exception", e);
            if (isContinue) {
              SqlUtil.handlerResult(index, sql, FlowStatus.FAILED, resultCallback);
            } else {
              SqlUtil.handlerResults(index, sqls, FlowStatus.FAILED, resultCallback);
              return false;
            }
          }
        }
      }

      con.commit();
    }

    return true;
  }
}
