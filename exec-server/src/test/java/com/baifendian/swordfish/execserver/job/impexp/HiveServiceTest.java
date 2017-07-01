package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.execserver.job.impexp.Args.HqlColumn;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.thrift.TException;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hive 服务类测试工具
 */
public class HiveServiceTest {
  @Test
  public void testGetHiveDesc() throws SQLException, ClassNotFoundException, TException {
    HiveService hiveService = new HiveService("jdbc:hive2://172.18.1.22:10000", "thrift://172.18.1.22:9083", "shuanghu", "");
    hiveService.init();
    List<HqlColumn> hqlColumnList = hiveService.getHiveDesc("swordfish_test", "data_test22");
    hqlColumnList.size();
  }

  @Test
  public void testCreateHiveTmpTable() throws SQLException, ClassNotFoundException, InterruptedException, MetaException {
    HiveService hiveService = new HiveService("jdbc:hive2://172.18.1.22:10000", "thrift://172.18.1.22:9083", "", "");
    hiveService.init();

    List<HqlColumn> testColumn = new ArrayList<>();
    testColumn.add(new HqlColumn("id", "int"));
    testColumn.add(new HqlColumn("name", "varchar(45)"));
    testColumn.add(new HqlColumn("desc", "varchar(45)"));
    testColumn.add(new HqlColumn("create_time", "timestamp"));

    String localtion = "/tmp/datax_test/data_test";

    String tableName = "debug_swordfish_impexp";

    Thread.sleep(60000L);

    hiveService.close();
  }

  @Test
  public void testInsertTable() throws SQLException, ClassNotFoundException, MetaException {
    String srcTable = "default.debug_swordfish_impexp";
    String destTable = "default.debug_swordfish_impexp1";

    List<HqlColumn> testColumn = new ArrayList<>();
    testColumn.add(new HqlColumn("id", "int"));
    testColumn.add(new HqlColumn("name", "varchar(45)"));
    testColumn.add(new HqlColumn("desc", "varchar(45)"));
    testColumn.add(new HqlColumn("create_time", "timestamp"));

    HiveService hiveService = new HiveService("jdbc:hive2://172.18.1.22:10000", "thrift://172.18.1.22:9083", "", "");
    hiveService.init();

    //hiveService.insertTable(srcTable, destTable, testColumn, WriteMode.APPEND);
  }

  @Test
  public void test() throws MetaException, SQLException, ClassNotFoundException {
    String sql = "INSERT INTO TABLE swordfish_test.data_test221  SELECT `id`,`name`,`desc`,null as `email`,`create_time` FROM swordfish_test.data_test2";
    HiveService hiveService = new HiveService("jdbc:hive2://172.18.1.22:10000", "thrift://172.18.1.22:9083", "shuanghu", "");
    hiveService.init();
    Statement stmt = hiveService.getCon().createStatement();
    stmt.execute(sql);
    hiveService.close();
  }
}
