package com.baifendian.swordfish.common.job.struct.node.impexp;

import com.baifendian.swordfish.common.enums.ImpExpType;
import com.baifendian.swordfish.common.job.struct.node.BaseParam;

/**
 * 导入导出参数
 */
public class ImpExpParam extends BaseParam {
  private ImpExpType type;
  private Reader readerParam;
  private Writer writerParam;
  private Setting setting;

  private Reader reader;
  private Writer writer;

  public ImpExpType getType() {
    return type;
  }

  public void setType(ImpExpType type) {
    this.type = type;
  }

  public Reader getReaderParam() {
    return readerParam;
  }

  public void setReaderParam(Reader readerParam) {
    this.readerParam = readerParam;
  }

  public Writer getWriterParam() {
    return writerParam;
  }

  public void setWriterParam(Writer writerParam) {
    this.writerParam = writerParam;
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
}
