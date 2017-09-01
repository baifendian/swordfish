package com.baifendian.swordfish.execserver.engine.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.hive.HiveContext;

/**
 * <p>
 *
 * @author : shuanghu
 */
public class SparkTest {

  public static void main(String[] args) {

    String[] sqls = args[0].split(";");
    SparkConf sparkConf = new SparkConf().setAppName("JavaSparkSQL")
        .setMaster("local[*]")
        .set("hive.aux.jars.path", "/usr/hdp/2.6.1.0-129/hive2/lib/hive-hcatalog-core.jar")
        .set("spark.sql.warehouse.dir", "/opt/udp/tmp/sql");
    JavaSparkContext ctx = new JavaSparkContext(sparkConf);
    HiveContext sqlContext = new HiveContext(ctx);

    for(String sql: sqls) {
      DataFrame sqlDF = sqlContext.sql(sql).limit(5);
      for (String col : sqlDF.columns()) {
        System.out.print(col + "  ");
      }
      System.out.println("\n\n");
      sqlDF.show();
    }
    ctx.close();
  }
}
