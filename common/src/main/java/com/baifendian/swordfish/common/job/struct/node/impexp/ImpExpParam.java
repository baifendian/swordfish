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
package com.baifendian.swordfish.common.job.struct.node.impexp;

import com.baifendian.swordfish.common.enums.ImpExpType;
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.Reader;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.Writer;

/**
 * 导入导出参数
 */
public class ImpExpParam extends BaseParam {
  private ImpExpType type;
  private Setting setting;
  private Reader reader;
  private Writer writer;

  public ImpExpType getType() {
    return type;
  }

  public void setType(ImpExpType type) {
    this.type = type;
  }

  public Setting getSetting() {
    return setting;
  }

  public void setSetting(Setting setting) {
    this.setting = setting;
  }

  public Reader getReader() {
    return reader;
  }

  public void setReader(Reader reader) {
    this.reader = reader;
  }

  public Writer getWriter() {
    return writer;
  }

  public void setWriter(Writer writer) {
    this.writer = writer;
  }

  public ImpExpParam() {
  }

  public ImpExpParam(ImpExpBuilder impExpBuilder) {
    this.type = impExpBuilder.getType();
    this.reader = impExpBuilder.getReaderParam();
    this.writer = impExpBuilder.getWriterParam();
    this.setting = impExpBuilder.getSettingParam();
  }

  @Override
  public boolean checkValid() {
    return type != null &&
            reader != null &&
            reader.checkValid() &&
            writer != null &&
            writer.checkValid();
  }
}
