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
package com.baifendian.swordfish.execserver.engine.hive;

import com.baifendian.swordfish.common.hive.metastore.HiveMetaPoolClient;
import com.baifendian.swordfish.common.hive.service2.HiveService2Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:common/hive/hive.properties"})
public class HiveConfig {

  @Value("${hive.metastore.uris}")
  private String metastoreUris;

  @Value("${hive.thrift.uris}")
  private String thriftUris;

  @Bean
  public HiveService2Client hiveService2Client() {
    return HiveService2Client.getInstance();
  }

  @Bean
  HiveMetaPoolClient hiveMetaPoolClient() {
    HiveMetaPoolClient.init(getMetastoreUris());
    return HiveMetaPoolClient.getInstance();
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
