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
package com.baifendian.swordfish.common.enums;

/**
 * 导入导出类型
 */
public enum ImpExpType {
  /**
   * 0 mysql 到 hive ,1 mysql 到 hdfs
   */
  MYSQL_TO_HIVE(ReaderType.MYSQL, WriterType.HIVE),
  MYSQL_TO_HDFS(ReaderType.MYSQL, WriterType.HDFS),
  HIVE_TO_MYSQL(ReaderType.HIVE, WriterType.MYSQL),
  HIVE_TO_MONGODB(ReaderType.HIVE, WriterType.MONGO),
  FILE_TO_HIVE(ReaderType.FILE, WriterType.HIVE),
  POSTGRES_TO_HIVE(ReaderType.POSTGRES, WriterType.HIVE),
  HIVE_TO_POSTGRES(ReaderType.HIVE, WriterType.POSTGRES),
  POSTGRES_TO_HDFS(ReaderType.POSTGRES, WriterType.HDFS);

  private ReaderType reader;
  private WriterType writer;

  public ReaderType getReader() {
    return reader;
  }

  public void setReader(ReaderType reader) {
    this.reader = reader;
  }

  public WriterType getWriter() {
    return writer;
  }

  public void setWriter(WriterType writer) {
    this.writer = writer;
  }

  ImpExpType(ReaderType reader, WriterType writer) {
    this.reader = reader;
    this.writer = writer;
  }
}
