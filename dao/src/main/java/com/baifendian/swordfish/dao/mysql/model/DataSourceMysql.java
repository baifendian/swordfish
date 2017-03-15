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

package com.baifendian.swordfish.dao.mysql.model;

/**
 * @auth: ronghua.yu
 * @time: 16/8/19
 * @desc:
 */
public class DataSourceMysql extends DataSourceDbBase {
  private String address;
  private String database;
  private String user;
  private String password;
  private Boolean autoRec;
  private Integer maxRec;
  private Integer initialTimeout;
  private Boolean useUnicode;
  private String characterEncoding;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getAutoRec() {
    return autoRec;
  }

  public void setAutoRec(Boolean autoRec) {
    this.autoRec = autoRec;
  }

  public Integer getMaxRec() {
    return maxRec;
  }

  public void setMaxRec(Integer maxRec) {
    this.maxRec = maxRec;
  }

  public Integer getInitialTimeout() {
    return initialTimeout;
  }

  public void setInitialTimeout(Integer initialTimeout) {
    this.initialTimeout = initialTimeout;
  }

  public Boolean getUseUnicode() {
    return useUnicode;
  }

  public void setUseUnicode(Boolean useUnicode) {
    this.useUnicode = useUnicode;
  }

  public String getCharacterEncoding() {
    return characterEncoding;
  }

  public void setCharacterEncoding(String characterEncoding) {
    this.characterEncoding = characterEncoding;
  }

  @Override
  public String toString() {
    return "DataSourceMysql{" +
        "address='" + address + '\'' +
        ", database='" + database + '\'' +
        ", user='" + user + '\'' +
        ", password='" + password + '\'' +
        ", autoRec=" + autoRec +
        ", maxRec=" + maxRec +
        ", initialTimeout=" + initialTimeout +
        ", useUnicode=" + useUnicode +
        ", characterEncoding='" + characterEncoding + '\'' +
        '}';
  }
}
