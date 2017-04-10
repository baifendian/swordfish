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
package com.baifendian.swordfish.masterserver.utils;

import com.baifendian.swordfish.rpc.RetInfo;

/**
 * 返回结果帮助类 <p>
 */
public class ResultHelper {
  /**
   * SUCCESS
   */
  public static final RetInfo SUCCESS = new RetInfo(0, "success");

  /**
   * 创建一个错误异常的返回包 <p>
   *
   * @return {@link RetInfo}
   */
  public static RetInfo createErrorResult(String msg) {
    return new RetInfo(1, msg);
  }
}
