package com.baifendian.swordfish.execserver.job.impexp.Args;

import com.baifendian.swordfish.common.job.struct.node.impexp.reader.MysqlReader;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.avro.data.Json;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * DataX 中 mysqlReader的配置
 */
public class MysqlReaderArg implements ReaderArg {
  private String username;
  private String password;
  private List<String> column;
  private String splitPk;
  private ArrayNode connection = JsonUtil.createArrayNode();

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

  public ArrayNode getConnection() {
    return connection;
  }

  public void setConnection(ArrayNode connection) {
    this.connection = connection;
  }

  public MysqlReaderArg(MysqlReader mysqlReader) throws JSONException {
    ObjectNode connObject = JsonUtil.createObjectNode();

    if (StringUtils.isNotEmpty(mysqlReader.getQuerySql())) {
      connObject.put("querySql", mysqlReader.getQuerySql());
    }

    List<String> tableList = mysqlReader.getTable();
    if (CollectionUtils.isNotEmpty(tableList)) {
      ArrayNode tableJsonList = connObject.putArray("table");
      for (String table : tableList) {
        tableJsonList.add(table);
      }
    }

    if (StringUtils.isNotEmpty(mysqlReader.getWhere())) {
      connObject.put("where", mysqlReader.getWhere());
    }

    connection.add(connObject);

    column = mysqlReader.getColumn();
  }


  @Override
  public String getName() {
    return "mysqlreader";
  }
}
