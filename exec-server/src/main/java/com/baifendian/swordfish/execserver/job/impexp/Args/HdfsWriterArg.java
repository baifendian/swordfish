package com.baifendian.swordfish.execserver.job.impexp.Args;

import com.baifendian.swordfish.common.enums.WriteHdfsType;
import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.HiveColumn;

import java.util.List;

/**
 * HDFS 写参数
 */
public class HdfsWriterArg implements WriterArg {
  private String defaultFS;
  private WriteHdfsType fileType;
  private String path;
  private String fileName;
  private String fieldDelimiter;
  private List<HiveColumn> column;

  public HdfsWriterArg() {
  }

  public String getDefaultFS() {
    return defaultFS;
  }

  public void setDefaultFS(String defaultFS) {
    this.defaultFS = defaultFS;
  }

  public WriteHdfsType getFileType() {
    return fileType;
  }

  public void setFileType(WriteHdfsType fileType) {
    this.fileType = fileType;
  }

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

  public String getFieldDelimiter() {
    return fieldDelimiter;
  }

  public List<HiveColumn> getColumn() {
    return column;
  }

  public void setColumn(List<HiveColumn> column) {
    this.column = column;
  }

  public void setFieldDelimiter(String fieldDelimiter) {
    this.fieldDelimiter = fieldDelimiter;
  }

  @Override
  public String dataxName() {
    return "hdfsWriter";
  }
}
