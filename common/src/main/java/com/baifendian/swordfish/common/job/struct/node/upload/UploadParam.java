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
package com.baifendian.swordfish.common.job.struct.node.upload;

import com.baifendian.swordfish.common.job.struct.node.BaseParam;

import java.util.List;

public class UploadParam extends BaseParam {
  private String file;

  private String separator;

  private String coding;

  private boolean hasTitle;

  private String targetDB;

  private String targetTable;

  private List<MappingRelation> mappingRelation;

  private String writerMode;

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public String getSeparator() {
    return separator;
  }

  public void setSeparator(String separator) {
    this.separator = separator;
  }

  public String getCoding() {
    return coding;
  }

  public void setCoding(String coding) {
    this.coding = coding;
  }

  public boolean isHasTitle() {
    return hasTitle;
  }

  public void setHasTitle(boolean hasTitle) {
    this.hasTitle = hasTitle;
  }

  public String getTargetDB() {
    return targetDB;
  }

  public void setTargetDB(String targetDB) {
    this.targetDB = targetDB;
  }

  public String getTargetTable() {
    return targetTable;
  }

  public void setTargetTable(String targetTable) {
    this.targetTable = targetTable;
  }

  public List<MappingRelation> getMappingRelation() {
    return mappingRelation;
  }

  public void setMappingRelation(List<MappingRelation> mappingRelation) {
    this.mappingRelation = mappingRelation;
  }

  public String getWriterMode() {
    return writerMode;
  }

  public void setWriterMode(String writerMode) {
    this.writerMode = writerMode;
  }
}
