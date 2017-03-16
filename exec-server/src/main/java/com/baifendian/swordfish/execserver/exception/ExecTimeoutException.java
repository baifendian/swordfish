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
package com.baifendian.swordfish.execserver.exception;

/**
 * 执行超时异常 <p>
 *
 * @author : dsfan
 * @date : 2016年11月15日
 */
public class ExecTimeoutException extends RuntimeException {
  /**
   * Serial version UID
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param msg
   */
  public ExecTimeoutException(String msg) {
    super(msg);
  }

  /**
   * @param msg
   * @param cause
   */
  public ExecTimeoutException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
