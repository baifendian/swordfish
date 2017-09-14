/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.execserver.engine;

import com.baifendian.swordfish.dao.enums.FlowStatus;
import com.baifendian.swordfish.execserver.common.ExecResult;
import com.baifendian.swordfish.execserver.common.ResultCallback;
import com.baifendian.swordfish.execserver.engine.hive.HiveUtil;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * sql util
 */
public class SqlUtil {

  /**
   * 处理结果, 从 fromIndex 开始
   */
  public static void handlerResults(int fromIndex, List<String> sqls, FlowStatus status,
      ResultCallback resultCallback) {
    for (int i = fromIndex; i < sqls.size(); ++i) {
      String sql = sqls.get(i);

      handlerResult(i, sql, status, resultCallback);
    }
  }

  /**
   * 处理单条记录
   */
  public static void handlerResult(int index, String sql, FlowStatus status,
      ResultCallback resultCallback) {
    Date now = new Date();

    ExecResult execResult = new ExecResult();

    execResult.setIndex(index);
    execResult.setStm(sql);
    execResult.setStatus(status);

    if (resultCallback != null) {
      // 执行结果回调处理
      resultCallback.handleResult(execResult, now, now);
    }
  }

  public static void execSql(String sql, Statement statement, int queryLimit, ExecResult execResult)
      throws SQLException {
    // 只对 query 和 show 语句显示结果
    if (HiveUtil.isTokQuery(sql) || HiveUtil.isLikeShowStm(sql)) {
      statement.setMaxRows(queryLimit);
      try (ResultSet res = statement.executeQuery(sql)) {

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

          datas.add(values);
        }

        execResult.setValues(datas);
      }
    } else {
      statement.execute(sql);
    }

    // 执行到这里，说明已经执行成功了
    execResult.setStatus(FlowStatus.SUCCESS);
  }
}
