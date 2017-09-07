package com.baifendian.swordfish.server.sparksql.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.hive.HiveContext;

public class SparkContextUtil {
  private static JavaSparkContext sparkContext;

  static public HiveContext createHiveContext(String jobId){
    return new HiveContext(get(jobId));
  }

  static public void  close(String jobId){
    sparkContext.close();
  }

  static public JavaSparkContext get(String jobId){
    SparkConf sparkConf = new SparkConf().setAppName("JavaSparkSQL_" + jobId)
        .set("spark.sql.warehouse.dir", "/opt/udp/tmp/sql");
    sparkContext = new JavaSparkContext(sparkConf);
    return sparkContext;
  }

  static public void back(String jobId, boolean hasFunc){
    sparkContext.close();
  }
}
