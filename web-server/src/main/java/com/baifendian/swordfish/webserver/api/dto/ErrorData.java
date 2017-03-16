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
package com.baifendian.swordfish.webserver.api.dto;

public class ErrorData extends BaseData {
  public enum Code {
    PARAM_NOT_VALID
  }

  /**
   * 编码信息
   */
  private Code code;

  /**
   * 错误消息
   */
  private String message;

  public ErrorData() {

  }

  public ErrorData(Code code, String message) {
    this.code = code;
    this.message = message;
  }

  public Code getCode() {
    return code;
  }

  public void setCode(Code code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "ErrorData{" +
        "code=" + code +
        ", message='" + message + '\'' +
        '}';
  }
}
