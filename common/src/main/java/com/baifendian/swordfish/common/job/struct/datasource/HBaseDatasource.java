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
package com.baifendian.swordfish.common.job.struct.datasource;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HBASE参数配置
 */
public class HBaseDatasource extends Datasource {

  private static Logger logger = LoggerFactory.getLogger(HBaseDatasource.class.getName());

  private String zkQuorum;

  private String zkZnodeParent;

  private Integer zkPort;

  public String getZkQuorum() {
    return zkQuorum;
  }

  public void setZkQuorum(String zkQuorum) {
    this.zkQuorum = zkQuorum;
  }

  public String getZkZnodeParent() {
    return zkZnodeParent;
  }

  public void setZkZnodeParent(String zkZnodeParent) {
    this.zkZnodeParent = zkZnodeParent;
  }

  public Integer getZkPort() {
    return zkPort;
  }

  public void setZkPort(Integer zkPort) {
    this.zkPort = zkPort;
  }

  @Override
  public void isConnectable() throws Exception {
    Connection con = null;
    try {
      Configuration config = HBaseConfiguration.create();
      config.set("hbase.zookeeper.quorum", this.zkQuorum);
      if (!StringUtils.isEmpty(this.zkZnodeParent)) {
        config.set("zookeeper.znode.parent", this.zkZnodeParent);
      }
      if (this.zkPort != null && this.zkPort != 0) {
        config.set("hbase.zookeeper.property.clientPort", this.zkPort.toString());
      }
      con = ConnectionFactory.createConnection(config);
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (IOException e) {
          logger.error("hbase try conn close conn error", e);
          throw e;
        }
      }
    }
  }
}
