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
package com.baifendian.swordfish.common.job.struct.node.impexp.reader;

import com.baifendian.swordfish.common.job.struct.node.impexp.column.FileColumn;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.stringtemplate.v4.ST;

import java.util.List;

/**
 * 文件读取
 */
public class FileReader implements Reader {
  private List<FileColumn> srcColumn;
  private List<FileColumn> targetColumn;
  private String hdfsPath;
  private String fieldDelimiter;
  private String fileName;
  private String fileCode = "UTF-8";

  public List<FileColumn> getSrcColumn() {
    return srcColumn;
  }

  public void setSrcColumn(List<FileColumn> srcColumn) {
    this.srcColumn = srcColumn;
  }

  public List<FileColumn> getTargetColumn() {
    return targetColumn;
  }

  public void setTargetColumn(List<FileColumn> targetColumn) {
    this.targetColumn = targetColumn;
  }

  public String getHdfsPath() {
    return hdfsPath;
  }

  public void setHdfsPath(String hdfsPath) {
    this.hdfsPath = hdfsPath;
  }

  public String getFieldDelimiter() {
    return fieldDelimiter;
  }

  public void setFieldDelimiter(String fieldDelimiter) {
    this.fieldDelimiter = fieldDelimiter;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileCode() {
    return fileCode;
  }

  public void setFileCode(String fileCode) {
    this.fileCode = fileCode;
  }

  @Override
  public boolean checkValid() {
    return CollectionUtils.isNotEmpty(srcColumn) &&
            CollectionUtils.isNotEmpty(targetColumn) &&
            StringUtils.isNotEmpty(fieldDelimiter) &&
            !StringUtils.equalsIgnoreCase(fieldDelimiter, "\n") &&
            (StringUtils.isNotEmpty(hdfsPath) || StringUtils.isNotEmpty(fileName));
  }
}
