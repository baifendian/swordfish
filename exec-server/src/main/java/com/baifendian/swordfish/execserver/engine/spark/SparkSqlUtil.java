package com.baifendian.swordfish.execserver.engine.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.hive.HiveContext;

/**
 * <p>
 *
 * @author : shuanghu
 */
public class SparkSqlUtil {

  static JavaSparkContext ctx;
  static {
    SparkConf sparkConf = new SparkConf().setAppName("JavaSparkSQL")
        .setMaster("local[*]")
        .set("spark.sql.warehouse.dir", "/opt/udp/tmp/sql");
    ctx = new JavaSparkContext(sparkConf);
  }

  static HiveContext getHiveContext(){
    return new HiveContext(ctx);
  }
}
