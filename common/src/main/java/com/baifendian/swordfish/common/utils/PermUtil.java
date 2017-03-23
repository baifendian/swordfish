/*
 * Copyright (C) 2017 Baifendian Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baifendian.swordfish.common.utils;

import com.baifendian.swordfish.common.consts.Constants;

public class PermUtil {
  /**
   * 判断是否具备写权限
   *
   * @param perm
   * @return
   */
  public static boolean hasWritePerm(int perm) {
    return (perm & Constants.PROJECT_USER_PERM_WRITE) != 0;
  }

  /**
   * 判断是否具备读权限
   *
   * @param perm
   * @return
   */
  public static boolean hasReadPerm(int perm) {
    return (perm & Constants.PROJECT_USER_PERM_READ) != 0;
  }

  /**
   * 判断是否具备执行权限
   *
   * @param perm
   * @return
   */
  public static boolean hasExecPerm(int perm) {
    return (perm & Constants.PROJECT_USER_PERM_EXEC) != 0;
  }
}
