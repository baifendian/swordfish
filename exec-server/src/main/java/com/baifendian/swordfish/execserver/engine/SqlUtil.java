package com.baifendian.swordfish.execserver.engine;

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.engine.hive.HiveUtil;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * @author : shuanghu
 */
public class SqlUtil {
  public static void execSql(String sql, Statement statement, int queryLimit, ExecResult execResult)
      throws SQLException {
    // 只对 query 和 show 语句显示结果
    if (HiveUtil.isTokQuery(sql) || HiveUtil.isLikeShowStm(sql)) {
      statement.setMaxRows(queryLimit);
      ResultSet res = statement.executeQuery(sql);

      ResultSetMetaData resultSetMetaData = res.getMetaData();
      int count = resultSetMetaData.getColumnCount();

      List<String> colums = new ArrayList<>();
      for (int i = 1; i <= count; i++) {
        colums.add(resultSetMetaData.getColumnLabel(i));
      }

      execResult.setTitles(colums);

      List<List<String>> datas = new ArrayList<>();

      // 如果字段数大于 1, 或是 query 语句
      if (count > 1 || HiveUtil.isTokQuery(sql)) {
        while (res.next()) {
          List<String> values = new ArrayList<>();
          for (int i = 1; i <= count; ++i) {
            values.add(res.getString(i));
            System.out.println(res.getString(i));
          }

          datas.add(values);
        }
      } else {
        StringBuffer buffer = new StringBuffer();

        while (res.next()) {
          buffer.append(res.getString(1));
          buffer.append("\n");
        }

        List<String> values = new ArrayList<>();
        values.add(buffer.toString().trim());
        System.out.println(buffer.toString());

        datas.add(values);
      }

      execResult.setValues(datas);
    } else {
      statement.execute(sql);
    }

    // 执行到这里，说明已经执行成功了
    execResult.setStatus(FlowStatus.SUCCESS);


  }
}
