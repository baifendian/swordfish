package com.baifendian.swordfish.execserver.engine.spark;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
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
    Dataset<String> stringsDS = sqlDF.map(
        (MapFunction<Row, String>) row -> "Key: " + row.get(0) + ", Value: " + row.get(1),
        Encoders.STRING());
    stringsDS.show();

    sparkSession.close();
  }
}
