package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.HiveColumn;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.ExecThriftServer;
import com.baifendian.swordfish.execserver.job.impexp.Args.HqlColumn;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.MessageFormat;
import java.util.*;

import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.*;

/**
 * hive 服务类
 * 这不是一个线程安全的类,只能在线程中封闭使用。
 */
public class HiveService {

  private static Logger logger = LoggerFactory.getLogger(HiveService.class);

  private Connection con;

  private String url;

  private String username;

  private String password;

  public HiveService(String url, String username, String password) {
    if (StringUtils.isEmpty(url)) {
      logger.error("Url must not be null!");
      throw new IllegalArgumentException("Url must not be null!");
    }
    this.url = url;
    this.username = username;
    this.password = password;
  }

  /**
   * 初始化hive 服务类
   *
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public void init() throws ClassNotFoundException, SQLException {
    //防止重复初始化
    if (con == null) {
      Class.forName(HIVE_DRIVER);
      con = DriverManager.getConnection(url, username, password);
    }
  }

  /**
   * 获取临时表名称
   *
   * @return
   */
  public String getTbaleName(int projectId, String workflowName, String nodeName) {
    return MessageFormat.format("{0}_{1}_{2}_{3}", String.valueOf(projectId), workflowName, nodeName, UUID.randomUUID());
  }

  /**
   * 获取一个hive的表结构
   *
   * @param dbName
   * @param tableName
   * @return
   */
  public List<HqlColumn> getHiveDesc(String dbName, String tableName) throws SQLException {
    //构造查询SQL
    String sql = MessageFormat.format("desc {0}.{1}", dbName, tableName);
    Statement stmt = null;
    try {
      stmt = con.createStatement();
      ResultSet resultSet = stmt.executeQuery(sql);

      List<HqlColumn> res = new ArrayList<>();
      while (resultSet.next()) {
        res.add(new HqlColumn(resultSet.getString(1), resultSet.getString(2)));
      }
      resultSet.close();
      return res;
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }
  }

  /**
   * 检测一个 HiveColumn 是否合法，如果合法就返回hql
   *
   * @return
   */
  public List<HqlColumn> checkHiveColumn(List<HiveColumn> srcColumn, List<HqlColumn> destColumn) throws Exception {
    List<HqlColumn> hqlColumnList = new ArrayList<>();
    for (HiveColumn hiveColumn : srcColumn) {
      boolean found = false;
      for (HqlColumn hqlColumn : destColumn) {
        if (hqlColumn.equals(hiveColumn)) {
          hqlColumnList.add(hqlColumn);
        }
      }

      if (!found) {
        //如果没有找到匹配的抛出异常
        String msg = "Write hive column {0} not found";
        throw new Exception(MessageFormat.format(msg, hiveColumn.getName()));
      }
    }
    return hqlColumnList;
  }

  /**
   * 组装一个临时表
   *
   * @param
   * @return
   */
  public void createHiveTmpTable(String tableName, List<HqlColumn> hqlColumnList, String localtion) throws SQLException {

    List<String> fieldList = new ArrayList<>();

    for (HqlColumn hqlColumn : hqlColumnList) {
      fieldList.add(MessageFormat.format("`{0}` {1}", hqlColumn.getName(), hqlColumn.getType()));
    }

    String sql = "CREATE TEMPORARY EXTERNAL TABLE `{0}`({1}) ROW FORMAT DELIMITED FIELDS TERMINATED BY \"{2}\" STORED AS {3} LOCATION \"{4}\"";

    sql = MessageFormat.format(sql, tableName, String.join(",", fieldList), DEFAULT_DELIMITER, DEFAULT_FILE_TYPE, localtion);

    logger.info("Create temp hive table sql: {}", sql);

    Statement stmt = null;
    try {
      logger.info("Start create temp hive table ...");
      stmt = con.createStatement();
      stmt.execute(sql);
      logger.info("Finish create temp hive table!");
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }
  }

  /**
   * 把数据插入表中
   */
  public void insertTable(String srcTableName, String destTableName, List<HqlColumn> hqlColumnList, WriteMode writeMode) throws SQLException {
    String selectSql = "SELECT {0} FROM `{1}`";
    String insertSql = "INSERT {0} TABLE `{1}` {2}";

    List<String> fieldList = new ArrayList<>();
    for (HqlColumn hqlColumn : hqlColumnList) {
      fieldList.add(MessageFormat.format("`{0}`", hqlColumn.getName()));
    }

    selectSql = MessageFormat.format(selectSql, String.join(",", fieldList), srcTableName);
    insertSql = MessageFormat.format(insertSql, writeMode.gethiveSql(), destTableName, selectSql);
    logger.info("Insert table sql: {}", insertSql);

    Statement stmt = null;
    try {
      logger.info("Start insert hive table ...");
      stmt = con.createStatement();
      int num = stmt.executeUpdate(insertSql);
      logger.info("Finish insert hive table! insert count: {}", num);
    } finally {
      if (stmt != null) {
        stmt.close();
      }
    }

  }

  /**
   * 使用完毕关闭连接
   */
  public void close() {
    if (con != null) {
      try {
        con.close();
      } catch (SQLException e) {
        logger.error("Hive con close error", e);
      }
    }
  }
}
