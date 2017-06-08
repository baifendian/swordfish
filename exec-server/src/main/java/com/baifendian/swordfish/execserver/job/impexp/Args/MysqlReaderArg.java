package com.baifendian.swordfish.execserver.job.impexp.Args;

import com.baifendian.swordfish.common.job.struct.node.impexp.reader.MysqlReader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * DataX 中 mysqlReader的配置
 */
public class MysqlReaderArg {
  private String username;
  private String password;
  private List<String> column;
  private String splitPk;
  private JSONArray connection;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<String> getColumn() {
    return column;
  }

  public void setColumn(List<String> column) {
    this.column = column;
  }

  public String getSplitPk() {
    return splitPk;
  }

  public void setSplitPk(String splitPk) {
    this.splitPk = splitPk;
  }

  public JSONArray getConnection() {
    return connection;
  }

  public void setConnection(JSONArray connection) {
    this.connection = connection;
  }

  public MysqlReaderArg() {
  }

  public MysqlReaderArg(MysqlReader mysqlReader) throws JSONException {
    JSONObject connObject = new JSONObject();

    if (StringUtils.isNotEmpty(mysqlReader.getQuerySql())) {
      connObject.put("querySql", mysqlReader.getQuerySql());
    }

    if (CollectionUtils.isNotEmpty(mysqlReader.getTable())) {
      connObject.put("table", mysqlReader.getTable());
    }

    if (StringUtils.isNotEmpty(mysqlReader.getWhere())) {
      connObject.put("where", mysqlReader.getWhere());
    }

    connection.put(connObject);

    column = mysqlReader.getColumn();
  }


}
