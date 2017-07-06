package com.baifendian.swordfish.execserver.job.impexp.Args;

import com.baifendian.swordfish.common.job.struct.node.impexp.reader.HiveReader;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * dataX hive 读配置
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HiveReaderArg implements ReaderArg {
  private String username;
  private String password;
  private String where;
  private String querySql;
  private List<String> column;
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

  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  public String getQuerySql() {
    return querySql;
  }

  public void setQuerySql(String querySql) {
    this.querySql = querySql;
  }

  public List<String> getColumn() {
    return column;
  }

  public void setColumn(List<String> column) {
    this.column = column;
  }

  public ArrayNode getConnection() {
    return connection;
  }

  public void setConnection(ArrayNode connection) {
    this.connection = connection;
  }

  public HiveReaderArg(HiveReader hiveReader) {
    where = hiveReader.getWhere();
    querySql = hiveReader.getQuerySql();
    column = hiveReader.getColumn();

    ObjectNode connObject = JsonUtil.createObjectNode();

    List<String> tableList = Arrays.asList(hiveReader.getTable());
    if (CollectionUtils.isNotEmpty(tableList)) {
      ArrayNode tableJsonList = connObject.putArray("table");
      for (String table : tableList) {
        tableJsonList.add(table);
      }
    }

    connection.add(connObject);
  }

  @Override
  public String dataxName() {
    return "hivereader";
  }

}
