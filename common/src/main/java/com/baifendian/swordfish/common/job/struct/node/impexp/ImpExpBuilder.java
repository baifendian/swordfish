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
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.Reader;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.ReaderFactory;
import com.baifendian.swordfish.common.job.struct.node.impexp.setting.Setting;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.Writer;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.WriterFactory;
import com.baifendian.swordfish.dao.utils.json.JsonObjectDeserializer;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * 导入导出参数构造器
 */
public class ImpExpBuilder {
  private ImpExpType type;
  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String reader;
  @JsonRawValue
  @JsonDeserialize(using = JsonObjectDeserializer.class)
  private String writer;
  private Setting setting;
  private Writer writerParam;
  private Reader readerParam;

  public Setting getSetting() {
    return setting;
  }

  public ImpExpBuilder setSetting(Setting setting) {
    this.setting = setting;
    return this;
  }

  public ImpExpType getType() {
    return type;
  }

  public ImpExpBuilder setType(ImpExpType type) {
    this.type = type;
    return this;
  }

  public String getReader() {
    return reader;
  }

  public ImpExpBuilder setReader(String reader) {
    this.reader = reader;
    return this;
  }

  public String getWriter() {
    return writer;
  }

  public ImpExpBuilder setWriter(String writer) {
    this.writer = writer;
    return this;
  }

  public Writer getWriterParam() {
    return writerParam;
  }

  public Reader getReaderParam() {
    return readerParam;
  }

  public ImpExpParam buildImpExp() {
    this.readerParam = ReaderFactory.getReader(this.type, this.reader);
    this.writerParam = WriterFactory.getWriter(this.type, this.writer);
    return new ImpExpParam(this);
  }
}
