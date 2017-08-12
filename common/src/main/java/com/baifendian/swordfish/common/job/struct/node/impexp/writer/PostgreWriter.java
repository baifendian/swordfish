package com.baifendian.swordfish.common.job.struct.node.impexp.writer;

import com.baifendian.swordfish.common.enums.MysqlWriteMode;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class PostgreWriter implements Writer {
  private String datasource;
  private String table;
  private List<String> session;
  private String preSql;
  private String postSql;
  private MysqlWriteMode writeMode;
  private Long batchSize;
  private List<String> column;

  public String getDatasource() {
    return datasource;
  }

  public void setDatasource(String datasource) {
    this.datasource = datasource;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public List<String> getSession() {
    return session;
  }

  public void setSession(List<String> session) {
    this.session = session;
  }

  public String getPreSql() {
    return preSql;
  }

  public void setPreSql(String preSql) {
    this.preSql = preSql;
  }

  public String getPostSql() {
    return postSql;
  }

  public void setPostSql(String postSql) {
    this.postSql = postSql;
  }

  public MysqlWriteMode getWriteMode() {
    return writeMode;
  }

  public void setWriteMode(MysqlWriteMode writeMode) {
    this.writeMode = writeMode;
  }

  public Long getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(Long batchSize) {
    this.batchSize = batchSize;
  }

  public List<String> getColumn() {
    return column;
  }

  public void setColumn(List<String> column) {
    this.column = column;
  }

  @Override
  public boolean checkValid() {
    return StringUtils.isNotEmpty(datasource) &&
        StringUtils.isNotEmpty(table) &&
        CollectionUtils.isNotEmpty(column);

  }
}
