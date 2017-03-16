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

/**
 * @auth: ronghua.yu
 * @time: 16/12/22
 * @desc:
 */
public class DataSourceHbase extends DataSourceDbBase {
  private String address;
  private String zkQuorum;
  private Boolean distributed;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getZkQuorum() {
    return zkQuorum;
  }

  public void setZkQuorum(String zkQuorum) {
    this.zkQuorum = zkQuorum;
  }

  public Boolean getDistributed() {
    return distributed;
  }

  public void setDistributed(Boolean distributed) {
    this.distributed = distributed;
  }

  @Override
  public String toString() {
    return "DataSourceHbase{" +
            "address='" + address + '\'' +
            ", zkQuorum='" + zkQuorum + '\'' +
            ", distributed=" + distributed +
            '}';
  }
}
