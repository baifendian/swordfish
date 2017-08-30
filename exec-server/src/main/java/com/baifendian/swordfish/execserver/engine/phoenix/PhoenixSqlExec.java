package com.baifendian.swordfish.execserver.engine.phoenix;

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.common.ResultCallback;
import com.baifendian.swordfish.execserver.engine.SqlUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 官方推荐，不使用连接池。详情请参见： https://phoenix.apache.org/faq.html#Should_I_pool_Phoenix_JDBC_Connections
 * <p>
 *
 * @author : shuanghu
 */
public class PhoenixSqlExec {
  private static final Logger LOGGER = LoggerFactory.getLogger(PhoenixUtil.class);

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
   *  @param createFuncs 创建自定义函数语句
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

    try(Connection con = PhoenixUtil.getPhoenixConnection()) {
      if (con == null) {
        logger.error("Failed to get phoenix connect");
        return false;
      }

      Statement sta = con.createStatement();

      if (CollectionUtils.isNotEmpty(createFuncs)) {
        try {
          for (String sql : createFuncs) {
            logger.info("hive create function sql: {}", sql);
            sta.execute(sql);
          }
        } catch (SQLException e) {
          logger.error("execute query exception", e);

          // 这里就失败了, 会记录下错误记录, 然后返回
          handlerResults(0, sqls, FlowStatus.FAILED, resultCallback);

          return false;
        }
      }

      // 日志线程
      // 执行 sql 语句
      for (int index = 0; index < sqls.size(); ++index) {
        String sql = sqls.get(index);
        Date startTime = new Date();

        logger.info("hive execute sql: {}", sql);
        ExecResult execResult = new ExecResult();
        execResult.setIndex(index);
        execResult.setStm(sql);
        try {
          SqlUtil.execSql(sql, sta, queryLimit, execResult);
          // 执行结果回调处理
          if (resultCallback != null) {
            Date endTime = new Date();
            resultCallback.handleResult(execResult, startTime, endTime);
          }
        } catch (SQLException e) {
          if (isContinue) {
            handlerResult(index, sql, FlowStatus.FAILED, resultCallback);
          } else {
            handlerResults(index, sqls, FlowStatus.FAILED, resultCallback);
            return false;
          }
        }
      }
    }

    return true;
  }

  /**
   * 处理结果, 从 fromIndex 开始
   */
  private void handlerResults(int fromIndex, List<String> sqls, FlowStatus status,
      ResultCallback resultCallback) {
    for (int i = fromIndex; i < sqls.size(); ++i) {
      String sql = sqls.get(i);

      handlerResult(i, sql, status, resultCallback);
    }
  }

  /**
   * 处理单条记录
   */
  private void handlerResult(int index, String sql, FlowStatus status,
      ResultCallback resultCallback) {
    Date now = new Date();

    ExecResult execResult = new ExecResult();

    execResult.setIndex(index);
    execResult.setStm(sql);
    execResult.setStatus(status);

    if (resultCallback != null) {
      // 执行结果回调处理
      resultCallback.handleResult(execResult, now, now);
    }
  }

  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    List<String> sqls = new ArrayList<>();
    sqls.add("upsert into test1 values (1,'Hello1')");
    sqls.add("upsert into test values (2,'22222222222')");
    sqls.add("select * from test");

    PhoenixSqlExec phoenixSqlExec = new PhoenixSqlExec(PhoenixSqlExec::logProcess, "", LOGGER);
    phoenixSqlExec.execute(null, sqls, true, (execResult, startTime, endTime) -> {
      System.out.println("*****");
    }, 100, 100);
  }

  static public void logProcess(List<String> logs) {
    LOGGER.info("(stdout, stderr) -> \n{}", String.join("\n", logs));
  }
}
