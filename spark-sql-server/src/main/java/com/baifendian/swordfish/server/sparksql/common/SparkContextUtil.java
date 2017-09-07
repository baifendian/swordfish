package com.baifendian.swordfish.server.sparksql.common;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.hive.HiveContext;

public class SparkContextUtil {

  static public HiveContext createHiveContext(String jobId){
    return new HiveContext(get(jobId));
  }
  static public void  close(String jobId){

  }
  static public JavaSparkContext get(String jobId){
    SparkConf sparkConf = new SparkConf().setAppName("JavaSparkSQL_" + jobId)
        .set("spark.sql.warehouse.dir", "/opt/udp/tmp/sql");
    return new JavaSparkContext(sparkConf);
  }

  static public void back(String jobId, boolean hasFunc){

  }
}
