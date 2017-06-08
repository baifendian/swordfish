package com.baifendian.swordfish.execserver.job.impexp.Args;

import com.baifendian.swordfish.common.enums.WriteHdfsType;
import com.baifendian.swordfish.common.enums.WriteMode;

/**
 * HDFS 写参数
 */
public class HdfsWriterArg {
  private String defaultFS;
  private WriteHdfsType fileType;
  private String path;
  private String fileName;
  private String fieldDelimiter;

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

  public void setFieldDelimiter(String fieldDelimiter) {
    this.fieldDelimiter = fieldDelimiter;
  }
}
