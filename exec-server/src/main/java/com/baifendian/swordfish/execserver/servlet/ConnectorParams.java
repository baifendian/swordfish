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
package com.baifendian.swordfish.execserver.servlet;

public interface ConnectorParams {
  public static final String EXECUTOR_ID_PARAM = "executorId";
  public static final String ACTION_PARAM = "action";
  public static final String EXECID_PARAM = "execid";
  public static final String USER_PARAM = "user";

  public static final String UPDATE_ACTION = "update";
  public static final String STATUS_ACTION = "status";
  public static final String EXECUTE_ACTION = "execute";
  public static final String CANCEL_ACTION = "cancel";

  public static final String STATUS_PARAM = "status";

  public static final String RESPONSE_ERROR = "error";
  public static final String RESPONSE_SUCCESS = "success";

}
