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
package com.baifendian.swordfish.execserver.utils.hive;

import com.baifendian.swordfish.common.hive.HiveConnectionClient;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:common/hive/hive.properties"})
public class HiveConfig {
  private static final Logger logger = LoggerFactory.getLogger(HiveConfig.class.getName());

  @Value("${hive.metastore.uris}")
  private String metastoreUris;

  @Value("${hive.thrift.uris}")
  private String thriftUris;

  org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();

  @Bean
  public HiveConnectionClient hiveConnectionClient() {
    return HiveConnectionClient.getInstance();
  }

  public HiveMetaStoreClient hiveMetaStoreClient() throws MetaException {
    return new HiveMetaStoreClient(hiveConf());
  }

  @Bean
  public HiveConf hiveConf() {
    HiveConf hConf = null;
    org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();

    if (StringUtils.isNotEmpty(metastoreUris)) {
      conf.set("hive.metastore.uris", metastoreUris);
    } else {
      logger.error("meta store is not set.");
      return hConf;
    }

    hConf = new HiveConf(conf, HiveConf.class);
    return hConf;
  }

  public String getMetastoreUris() {
    return metastoreUris;
  }

  public void setMetastoreUris(String metastoreUris) {
    this.metastoreUris = metastoreUris;
  }

  public String getThriftUris() {
    return thriftUris;
  }

  public void setThriftUris(String thriftUris) {
    this.thriftUris = thriftUris;
  }
}
