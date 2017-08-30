package com.baifendian.swordfish.execserver.engine.spark;

import org.apache.spark.sql.SparkSession;

/**
 * <p>
 *
 * @author : shuanghu
 */
public class SparkSqlUtil {

  static SparkSession getHiveContext(){
    SparkSession spark = SparkSession
        .builder()
        .appName("Java Spark Hive Example")
        //.config("spark.sql.warehouse.dir", warehouseLocation)
        .enableHiveSupport()
        .getOrCreate();

    return spark;
  }
}
