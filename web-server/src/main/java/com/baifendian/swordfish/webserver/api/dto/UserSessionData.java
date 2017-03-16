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

import com.baifendian.swordfish.dao.model.User;

/**
 * author: smile8
 * date:   2017/3/16
 * desc:
 */
public class UserSessionData extends BaseData {
  /**
   * 用户 session id
   */
  private String sessionId;

  /**
   * 用户信息
   */
  private User user;

  public UserSessionData() {
  }

  public UserSessionData(String sessionId, User user) {
    this.sessionId = sessionId;
    this.user = user;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "UserSessionData{" +
        "sessionId='" + sessionId + '\'' +
        ", user=" + user +
        '}';
  }
}
