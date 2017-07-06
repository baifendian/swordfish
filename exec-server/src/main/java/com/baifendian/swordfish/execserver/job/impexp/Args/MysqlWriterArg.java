package com.baifendian.swordfish.execserver.job.impexp.Args;

import com.baifendian.swordfish.common.enums.MysqlWriteMode;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.MysqlWriter;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * mysql writer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MysqlWriterArg implements WriterArg {

  private MysqlWriteMode writeMode;

  private String username;

  private String password;

  private List<String> column;

  private List<String> session;

  private List<String> preSql;

  private List<String> postSql;

  private Long batchSize;

  private ArrayNode connection = JsonUtil.createArrayNode();

  public MysqlWriteMode getWriteMode() {
    return writeMode;
  }

  public void setWriteMode(MysqlWriteMode writeMode) {
    this.writeMode = writeMode;
  }

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

  public List<String> getSession() {
    return session;
  }

  public void setSession(List<String> session) {
    this.session = session;
  }

  public List<String> getPreSql() {
    return preSql;
  }

  public void setPreSql(List<String> preSql) {
    this.preSql = preSql;
  }

  public List<String> getPostSql() {
    return postSql;
  }

  public void setPostSql(List<String> postSql) {
    this.postSql = postSql;
  }

  public Long getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(Long batchSize) {
    this.batchSize = batchSize;
  }

  public ArrayNode getConnection() {
    return connection;
  }

  public void setConnection(ArrayNode connection) {
    this.connection = connection;
  }

  public MysqlWriterArg(MysqlWriter mysqlWriter) {
    ObjectNode connObject = JsonUtil.createObjectNode();

    List<String> tableList = Arrays.asList(mysqlWriter.getTable());
    if (CollectionUtils.isNotEmpty(tableList)) {
      ArrayNode tableJsonList = connObject.putArray("table");
      for (String table : tableList) {
        tableJsonList.add(table);
      }
    }

    preSql = mysqlWriter.getPreSql();
    postSql = mysqlWriter.getPostSql();
    column = mysqlWriter.getColumn();
    session = mysqlWriter.getSession();
    batchSize = mysqlWriter.getBatchSize();
    writeMode = mysqlWriter.getWriteMode();

    connection.add(connObject);
  }

  @Override
  public String dataxName() {
    return "mysqlwriter";
  }
}
