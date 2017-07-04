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
package com.baifendian.swordfish.common.job.struct.node.impexp.column;

import com.baifendian.swordfish.common.enums.FileColumnType;

/**
 * 文件列
 */
public class FileColumn {
  private String name;
  private FileColumnType type;
  private String dateFormat = "yyyy-MM-dd";

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public FileColumnType getType() {
    return type;
  }

  public void setType(FileColumnType type) {
    this.type = type;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }
}
