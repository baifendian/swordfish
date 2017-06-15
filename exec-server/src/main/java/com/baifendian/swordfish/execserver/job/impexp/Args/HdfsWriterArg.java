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
  private String writeMode;
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

  public String getWriteMode() {
    return writeMode;
  }

  public void setWriteMode(String writeMode) {
    this.writeMode = writeMode;
  }

  @Override
  public String dataxName() {
    return "hdfswriter";
  }
}
