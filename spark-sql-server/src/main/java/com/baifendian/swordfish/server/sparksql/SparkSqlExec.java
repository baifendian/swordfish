package com.baifendian.swordfish.server.sparksql;

import java.util.Date;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.hive.HiveContext;
import org.slf4j.Logger;

public class SparkSqlExec implements Runnable{

  private int execId;

  private JavaSparkContext ctx;

  private List<String> sqls;
  private boolean isContinue;
  Integer queryLimit;
  long stopTime;
  /**
   * 记录日志的实例
   */
  private Logger logger;

  /**
   * 任务是否停止
   */
  private boolean isStop = false;

  /**
   * 查询限制，默认为 1000
   */
  private static int defaultQueryLimit = 1000;

  public SparkSqlExec(List<String> sqls, boolean isContinue, Integer queryLimit, long stopTime, Logger logger){
    this.sqls = sqls;
    this.isContinue = isContinue;
    // 查询结果限制
    this.queryLimit = (queryLimit != null) ? queryLimit : defaultQueryLimit;

    this.stopTime = System.currentTimeMillis() + 1000L * 60 * 60;
    this.logger = logger;
  }

  public boolean cancel(){
    logger.info("spark job stop.");
    if (ctx != null){
      ctx.close();
    }
    isStop = true;
    return true;
  }

  public boolean execute(){
    if (isStop){
      return true;
    }

    if (System.currentTimeMillis() >= stopTime){
      logger.info("spark hive sql is timeout.");
      return false;
    }

    SparkConf sparkConf = new SparkConf().setAppName("JavaSparkSQL_"+execId)
        .set("spark.sql.warehouse.dir", "/opt/udp/tmp/sql");
    ctx = new JavaSparkContext(sparkConf);

    HiveContext sparkSession = new HiveContext(ctx);

    // 执行 sql 语句
    for (int index = 0; index < sqls.size(); ++index) {
      String sql = sqls.get(index);
      Date startTime = new Date();
      logger.info("spark hive execute sql: {}", sql);
      try {
        sparkSession.sql(sql).limit(queryLimit);
      }catch (Exception e){
        // 语义异常
        logger.error("spark executeQuery exception", e);
      }
    }

    return true;
  }

  @Override
  public void run() {
    execute();
  }
}
