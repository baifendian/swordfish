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
package com.baifendian.swordfish.common.job.struct.node.impexp.writer;

import com.baifendian.swordfish.common.enums.WriteHdfsType;
import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.HiveColumn;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Hdfs 写入参数
 */
public class HdfsWriter implements Writer {
  private String path;
  private String fileName;
  private WriteMode writeMode;
  private WriteHdfsType fileType;
  private String fieldDelimiter;
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

  public WriteHdfsType getFileType() {
    return fileType;
  }

  public void setFileType(WriteHdfsType fileType) {
    this.fileType = fileType;
  }

  public List<HiveColumn> getColumn() {
    return column;
  }

  public String getFieldDelimiter() {
    return fieldDelimiter;
  }

  public void setFieldDelimiter(String fieldDelimiter) {
    this.fieldDelimiter = fieldDelimiter;
  }

  public void setColumn(List<HiveColumn> column) {
    this.column = column;
  }

  /**
   * 检测fieldDelimiter是否合法
   *
   * @return
   */
  private boolean checkFieldDelimiter(String fieldDelimiter) {
    return StringUtils.isNotEmpty(fieldDelimiter) && !StringUtils.equalsIgnoreCase(fieldDelimiter, "\n");
  }

  @Override
  public boolean checkValid() {
    boolean checkFieldDelimiter = true;
    if (fileType == WriteHdfsType.TEXT) {
      checkFieldDelimiter = checkFieldDelimiter(fieldDelimiter);
    }

    return StringUtils.isNotEmpty(path) && StringUtils.isNotEmpty(fileName) && checkFieldDelimiter && writeMode != null && CollectionUtils.isNotEmpty(column);
  }
}
