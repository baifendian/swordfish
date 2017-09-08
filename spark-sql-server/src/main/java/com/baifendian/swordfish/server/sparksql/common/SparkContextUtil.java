package com.baifendian.swordfish.server.sparksql.common;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.hive.HiveContext;

public class SparkContextUtil {
  private static JavaSparkContext sparkContext;

  static public HiveContext createHiveContext(String jobId){
    return new HiveContext(get(jobId));
  }

  static public void  close(String jobId){
    if (sparkContext == null){
      return;
    }
    sparkContext.close();
    sparkContext = null;
  }

  private static JavaSparkContext get(String jobId){
    if (sparkContext != null){
      return sparkContext;
    }

    SparkConf sparkConf = new SparkConf().setAppName("JavaSparkSQL_" + jobId)
        .set("spark.sql.warehouse.dir", "/opt/udp/tmp/sql");
    sparkContext = new JavaSparkContext(sparkConf);
    return sparkContext;
  }

  /**
   * 如果spark没有添加任何函数，则context可以复用
   * 否则，关闭
   */
  static public void back(String jobId, boolean hasFunc){
    if (hasFunc){
      close(jobId);
    }
  }
}
