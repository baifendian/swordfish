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
    sqls.add("upsert into MY_BLOXY.COUNT_RESULT(\"id\", \"num\") select 1,count(1) from MY_BLOXY.TEMP_TEST");
    sqls.add("select * from MY_BLOXY.COUNT_RESULT");

    PhoenixSqlExec phoenixSqlExec = new PhoenixSqlExec(PhoenixSqlTest::logProcess, "", LOGGER);
    phoenixSqlExec.execute(null, sqls, true, (execResult, startTime, endTime) -> {
      System.out.println("**");
      if (execResult.getValues() == null){
        return;
      }
      for (List<String> rs:execResult.getValues()){
        rs.forEach(o -> System.out.print(o+"  "));
        System.out.println();
      }
    }, 100, 100);
  }

  static public void logProcess(List<String> logs) {
    LOGGER.info("(stdout, stderr) -> \n{}", String.join("\n", logs));
  }
}
