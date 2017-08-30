package com.baifendian.swordfish.execserver.engine.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * <p>
 *
 * @author : shuanghu
 */
public class SparkTest {

  public static void main(String[] args) {

    String sql = args[0];

    SparkSession sparkSession = SparkSqlUtil.getHiveContext();
    Dataset<Row> sqlDF = sparkSession.sql(sql).limit(5);
    for (String col: sqlDF.columns()) {
      System.out.print(col+"  ");
    }
    System.out.println("\n\n");
    sqlDF.show();

    sparkSession.close();
  }
}
