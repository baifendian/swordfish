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

package com.baifendian.swordfish.dao.model;

import com.baifendian.swordfish.common.consts.Constants;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Node 执行日志的信息 <p>
 *
 * @author : wenting.wang
 * @date : 2016年8月30日
 */
public class ExecNodeLog {

  /**
   * 日志id
   **/
  private Long logId;

  private byte[] logBytes;

  /**
   * 日志内容
   **/
  private String logInfo;

  /**
   * 日志开始位置
   **/
  private int startByte;

  /**
   * 日志结束位置
   **/
  private int endByte;

  /**
   * 上传时间
   **/
  private Date uploadTime;

  public byte[] getLogBytes() {
    return logBytes;
  }

  public void setLogBytes(byte[] logBytes) {
    this.logBytes = logBytes;
  }

  public Long getLogId() {
    return logId;
  }

  public void setLogId(Long logId) {
    this.logId = logId;
  }

  public String getLogInfo() {
    if (logInfo == null && logBytes != null) {
      try {
        logInfo = new String(logBytes, Constants.UTF_8);
      } catch (UnsupportedEncodingException e) {
        logInfo = new String(logBytes);
      }
    }
    return logInfo;
  }

  public void setLogInfo(String logInfo) {
    this.logInfo = logInfo;
  }

  public int getStartByte() {
    return startByte;
  }

  public void setStartByte(int startByte) {
    this.startByte = startByte;
  }

  public int getEndByte() {
    return endByte;
  }

  public void setEndByte(int endByte) {
    this.endByte = endByte;
  }

  public Date getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Date uploadTime) {
    this.uploadTime = uploadTime;
  }
}
