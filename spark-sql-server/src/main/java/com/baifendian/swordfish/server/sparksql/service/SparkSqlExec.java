package com.baifendian.swordfish.server.sparksql.service;

import com.baifendian.swordfish.rpc.AdhocResultInfo;
import com.baifendian.swordfish.server.sparksql.common.FlowStatus;
import com.baifendian.swordfish.server.sparksql.common.SparkContextUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkSqlExec implements Runnable {

  private String jobId;

  private List<String> sqls;
  private List<String> createFuncs;
  private Integer queryLimit;
  private long stopTime;
  /**
   * 记录日志的实例
   */
  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 任务是否停止
   */
  private boolean isStop = false;

  AdhocResultData adhocResultData = null;

  public SparkSqlExec(String jobId, List<String> createFuncs, List<String> sqls, Integer queryLimit,
      long stopTime) {
    this.jobId = jobId;
    this.createFuncs = createFuncs;
    this.sqls = sqls;
    if (queryLimit != null){
      // adhoc query
      adhocResultData = new AdhocResultData(sqls.size());
    }
    // 查询结果限制
    this.queryLimit = queryLimit==null? 1000:queryLimit;

    this.stopTime = stopTime;
  }

  boolean cancel() {
    logInfo("kill spark job.");
    SparkContextUtil.close(jobId);
    isStop = true;
    if (adhocResultData != null){
      adhocResultData.cancel();
    }
    return true;
  }

  private boolean execute(HiveContext sparkSession) {
    if (!createFuncs.isEmpty()) {
      try {
        for (String sql : createFuncs) {
          logInfo("spark hive create function sql: {}", sql);
          sparkSession.sql(sql);
        }
      } catch (Exception e) {
        logError("spark execute query exception", e);

        // 这里就失败了, 会记录下错误记录, 然后返回
        handlerResults(0, FlowStatus.FAILED);

        return false;
      }
    }

    // 执行 sql 语句
    for (int index = 0; index < sqls.size(); ++index) {
      String sql = sqls.get(index);
      logInfo("spark hive execute sql: {}", sql);
      AdhocResultInfo execResult = new AdhocResultInfo();
      execResult.setStm(sql);
      execResult.setIndex(index);
      try {
        execSql(sql, sparkSession, queryLimit, execResult);
        // 执行结果回调处理
        addResult(execResult);
      }catch (Exception e){
        // 语义异常
        logError("spark executeQuery exception", e);

       handlerResults(index, FlowStatus.FAILED);
      }
    }

    return true;
  }

  private void execSql(String sql, HiveContext sparkSession, int queryLimit,
      AdhocResultInfo execResult)
      throws SQLException {

    // 只对 query 和 show 语句显示结果
    if (isTokQuery(sql) || isLikeShowStm(sql)) {
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
    execResult.setStatus(FlowStatus.SUCCESS.ordinal());
  }

  private void handlerResults(int fromIndex, FlowStatus status){
      for (int i=fromIndex; i<sqls.size(); ++i){
        handlerResult(i, sqls.get(i), status);
      }
  }

  private void handlerResult(int fromIndex, String sql, FlowStatus status){
    if (adhocResultData == null){
      return;
    }

    adhocResultData.handlerResult(fromIndex, sql, status);
  }

  private void  addResult(AdhocResultInfo adhocResultInfo){
    if (adhocResultData == null){
      return;
    }
    adhocResultData.addResult(adhocResultInfo);
  }

  public AdhocResultInfo getAdHocResult(int index){
    logger.info("Begin get adhoc result");
    if (adhocResultData == null){
      throw new RuntimeException("Job is not adhoc result");
    }

    return adhocResultData.getAdHocResult(index);
  }

  @Override
  public void run() {
    if (isStop) {
      return ;
    }

    if (System.currentTimeMillis() >= stopTime) {
      logInfo("spark hive sql is timeout.");
      return ;
    }

    HiveContext sparkSession = SparkContextUtil.createHiveContext(jobId);
    execute(sparkSession);
    logger.info("End exec sql.");
    SparkContextUtil.back(jobId, !createFuncs.isEmpty());
  }

  private void logInfo(String msg, Object... objects){
    logger.info(String.format("%s %s", String.format("[jobId=%s]", jobId), msg), objects);
  }

  private void logError(String msg, Object... objects){
    logger.error(String.format("%s %s", String.format("[jobId=%s]", jobId), msg), objects);
  }
  /**
   * 判断是否是查询请求
   */
  public static boolean isTokQuery(String sql) {
    if (sql ==null|| sql.isEmpty()) {
      return false;
    }

    sql = sql.toUpperCase();

    if (sql.startsWith("SELECT")) {
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
    if (sql ==null|| sql.isEmpty()) {
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
}
