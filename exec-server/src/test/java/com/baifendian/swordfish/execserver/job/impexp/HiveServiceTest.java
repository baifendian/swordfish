package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.execserver.job.impexp.Args.HqlColumn;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hive 服务类测试工具
 */
public class HiveServiceTest {
  @Test
  public void testCreateHiveTmpTable() throws SQLException, ClassNotFoundException, InterruptedException {
    HiveService hiveService = new HiveService("jdbc:hive2://172.18.1.22:10000", "", "");
    hiveService.init();

    List<HqlColumn> testColumn = new ArrayList<>();
    testColumn.add(new HqlColumn("id", "int"));
    testColumn.add(new HqlColumn("name", "varchar(45)"));
    testColumn.add(new HqlColumn("desc", "varchar(45)"));
    testColumn.add(new HqlColumn("create_time", "timestamp"));

    String localtion = "/tmp/datax_test/data_test";

    String tableName = "debug_swordfish_impexp";

    hiveService.createHiveTmpTable(tableName, testColumn, localtion);

    Thread.sleep(60000L);

    hiveService.close();
  }

  @Test
  public void testInsertTable() throws SQLException, ClassNotFoundException {
    String srcTable = "default.debug_swordfish_impexp";
    String destTable = "default.debug_swordfish_impexp1";

    List<HqlColumn> testColumn = new ArrayList<>();
    testColumn.add(new HqlColumn("id", "int"));
    testColumn.add(new HqlColumn("name", "varchar(45)"));
    testColumn.add(new HqlColumn("desc", "varchar(45)"));
    testColumn.add(new HqlColumn("create_time", "timestamp"));

    HiveService hiveService = new HiveService("jdbc:hive2://172.18.1.22:10000", "", "");
    hiveService.init();

    hiveService.insertTable(srcTable, destTable, testColumn, WriteMode.APPEND);
  }
}
