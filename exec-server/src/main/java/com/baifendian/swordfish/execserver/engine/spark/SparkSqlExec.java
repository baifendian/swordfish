package com.baifendian.swordfish.execserver.engine.spark;

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.common.ResultCallback;
import com.baifendian.swordfish.execserver.engine.SqlUtil;
import com.baifendian.swordfish.execserver.engine.hive.HiveUtil;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;
import org.slf4j.Logger;

/**
 * <p>
 *
 * @author : shuanghu
 */
public class SparkSqlExec {
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

  public SparkSqlExec(Consumer<List<String>> logHandler, String userName, Logger logger){
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

    HiveContext sparkSession = SparkSqlUtil.getHiveContext();

      if (CollectionUtils.isNotEmpty(createFuncs)) {
        try {
          for (String sql : createFuncs) {
            logger.info("spark hive create function sql: {}", sql);
            sparkSession.sql(sql);
          }
        } catch (Exception e) {
          logger.error("spark execute query exception", e);

          // 这里就失败了, 会记录下错误记录, 然后返回
          SqlUtil.handlerResults(0, sqls, FlowStatus.FAILED, resultCallback);

          return false;
        }
      }

      //HiveContext hiveContext = SparkSqlUtil.getHiveContext();

      // 查询结果限制
      queryLimit = (queryLimit != null) ? queryLimit : defaultQueryLimit;

      // 日志线程
      // 执行 sql 语句
      for (int index = 0; index < sqls.size(); ++index) {
        String sql = sqls.get(index);
        Date startTime = new Date();
        logger.info("spark hive execute sql: {}", sql);
        ExecResult execResult = new ExecResult();
        execResult.setIndex(index);
        execResult.setStm(sql);
        try {
          execSql(sql, sparkSession, queryLimit, execResult);
          // 执行结果回调处理
          if (resultCallback != null) {
            Date endTime = new Date();
            resultCallback.handleResult(execResult, startTime, endTime);
          }
        }catch (Exception e){
          // 语义异常
          logger.error("spark executeQuery exception", e);

          if (isContinue) {
            SqlUtil.handlerResult(index, sql, FlowStatus.FAILED, resultCallback);
          } else {
            SqlUtil.handlerResults(index, sqls, FlowStatus.FAILED, resultCallback);
            return false;
          }
        }
      }

    return true;
  }

  static void execSql(String sql, HiveContext sparkSession, int queryLimit, ExecResult execResult)
      throws SQLException {

    // 只对 query 和 show 语句显示结果
    if (HiveUtil.isTokQuery(sql) || HiveUtil.isLikeShowStm(sql)) {
      DataFrame sqlDF = sparkSession.sql(sql).limit(queryLimit);

      String[] colums = sqlDF.columns();
      if (colums != null && colums.length > 0) {

        execResult.setTitles(Arrays.asList(colums));
        List<List<String>> datas = new ArrayList<>();
        for (Row row : sqlDF.collectAsList()) {
          List<String> values = new ArrayList<>();
          for (int i = 0; i < row.length(); ++i) {
            Object obj = row.get(i);
            if (obj == null){
              values.add("null");
            }else {
              values.add(obj.toString());
            }
          }

          datas.add(values);
        }
        execResult.setValues(datas);
      }
    } else {
      sparkSession.sql(sql);
    }

    // 执行到这里，说明已经执行成功了
    execResult.setStatus(FlowStatus.SUCCESS);
  }
}
