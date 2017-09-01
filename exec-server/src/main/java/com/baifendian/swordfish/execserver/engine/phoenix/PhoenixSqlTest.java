package com.baifendian.swordfish.execserver.engine.phoenix;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *
 * @author : shuanghu
 */
public class PhoenixSqlTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(PhoenixUtil.class);

  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    Boolean.FALSE.toString();
    List<String> sqls = new ArrayList<>();
    sqls.add("use dw");
    sqls.add("show tables;");
//    sqls.add("upsert into test1 values (1,'Hello1')");
//    sqls.add("upsert into test values (2,'22222222222')");
//    sqls.add("select * from test");

    PhoenixSqlExec phoenixSqlExec = new PhoenixSqlExec(PhoenixSqlTest::logProcess, "", LOGGER);
    phoenixSqlExec.execute(null, sqls, true, (execResult, startTime, endTime) -> {
      System.out.println("*****");
    }, 100, 100);
  }

  static public void logProcess(List<String> logs) {
    LOGGER.info("(stdout, stderr) -> \n{}", String.join("\n", logs));
  }
}
