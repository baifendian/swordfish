package com.baifendian.swordfish.execserver.engine.spark;

import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;

/**
 * <p>
 *
 * @author : shuanghu
 */
public class SparkTest {

  public static void main(String[] args) {

    SparkConf sparkConf = new SparkConf().setAppName("JavaSparkSQL")
        .setMaster("local")
        .set("hadoop.home.dir", "/tmp/spark/")
        .set("hive.metastore.uris", "thrift://172.24.8.95:9083");
    JavaSparkContext ctx = new JavaSparkContext(sparkConf);
    HiveContext sqlContext = new HiveContext(ctx);

    DataFrame teenagers = sqlContext.sql("show databases;");

    List<String> teenagerNames = teenagers.toJavaRDD().map(
        (Function<Row, String>) row -> "Name: " + row.getString(0)).collect();
    for (String name: teenagerNames) {
      System.out.println(name);
    }
    System.out.println("************");

  }
}
