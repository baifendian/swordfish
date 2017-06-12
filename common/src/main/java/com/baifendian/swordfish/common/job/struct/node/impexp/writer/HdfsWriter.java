package com.baifendian.swordfish.common.job.struct.node.impexp.writer;

import com.baifendian.swordfish.common.enums.WriteHdfsType;
import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.HiveColumn;

import java.util.List;

/**
 * Hdfs 写入参数
 */
public class HdfsWriter implements Writer {
  private String path;
  private String fileName;
  private WriteMode writeMode;
  private WriteHdfsType writeHdfsType;
  private List<HiveColumn> column;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public WriteMode getWriteMode() {
    return writeMode;
  }

  public void setWriteMode(WriteMode writeMode) {
    this.writeMode = writeMode;
  }

  public WriteHdfsType getWriteHdfsType() {
    return writeHdfsType;
  }

  public void setWriteHdfsType(WriteHdfsType writeHdfsType) {
    this.writeHdfsType = writeHdfsType;
  }

  public List<HiveColumn> getColumn() {
    return column;
  }

  public void setColumn(List<HiveColumn> column) {
    this.column = column;
  }
}
