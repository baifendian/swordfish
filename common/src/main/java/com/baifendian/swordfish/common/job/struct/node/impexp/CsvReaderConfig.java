package com.baifendian.swordfish.common.job.struct.node.impexp;

/**
 * CSV读取配置
 */
public class CsvReaderConfig {
  private boolean caseSensitive;
  private char textQualifier;
  private boolean trimWhitespace;
  private boolean useTextQualifier;
  private char delimiter;
  private char recordDelimiter;
  private boolean useComments;
  private int escapeMode;
  private boolean safetySwitch;
  private boolean skipEmptyRecords;
  private boolean captureRawRecord;

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public char getTextQualifier() {
    return textQualifier;
  }

  public void setTextQualifier(char textQualifier) {
    this.textQualifier = textQualifier;
  }

  public boolean isTrimWhitespace() {
    return trimWhitespace;
  }

  public void setTrimWhitespace(boolean trimWhitespace) {
    this.trimWhitespace = trimWhitespace;
  }

  public boolean isUseTextQualifier() {
    return useTextQualifier;
  }

  public void setUseTextQualifier(boolean useTextQualifier) {
    this.useTextQualifier = useTextQualifier;
  }

  public char getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(char delimiter) {
    this.delimiter = delimiter;
  }

  public char getRecordDelimiter() {
    return recordDelimiter;
  }

  public void setRecordDelimiter(char recordDelimiter) {
    this.recordDelimiter = recordDelimiter;
  }

  public boolean isUseComments() {
    return useComments;
  }

  public void setUseComments(boolean useComments) {
    this.useComments = useComments;
  }

  public int getEscapeMode() {
    return escapeMode;
  }

  public void setEscapeMode(int escapeMode) {
    this.escapeMode = escapeMode;
  }

  public boolean isSafetySwitch() {
    return safetySwitch;
  }

  public void setSafetySwitch(boolean safetySwitch) {
    this.safetySwitch = safetySwitch;
  }

  public boolean isSkipEmptyRecords() {
    return skipEmptyRecords;
  }

  public void setSkipEmptyRecords(boolean skipEmptyRecords) {
    this.skipEmptyRecords = skipEmptyRecords;
  }

  public boolean isCaptureRawRecord() {
    return captureRawRecord;
  }

  public void setCaptureRawRecord(boolean captureRawRecord) {
    this.captureRawRecord = captureRawRecord;
  }
}
