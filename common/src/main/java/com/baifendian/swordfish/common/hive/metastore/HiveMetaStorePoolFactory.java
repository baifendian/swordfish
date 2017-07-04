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
package com.baifendian.swordfish.common.hive.metastore;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveMetaStorePoolFactory extends BasePoolableObjectFactory {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private HiveConf hConf;

  public HiveMetaStorePoolFactory(String metastoreUris) {
    Configuration conf = new Configuration();

    if (StringUtils.isNotEmpty(metastoreUris)) {
      conf.set("hive.metastore.uris", metastoreUris);
    } else {
      logger.error("Metastore conf is empty.");
      throw new RuntimeException("Metastore conf is empty.");
    }

    hConf = new HiveConf(conf, HiveConf.class);
  }

  @Override
  public Object makeObject() throws Exception {
    HiveMetaStoreClient hmsc = new HiveMetaStoreClient(hConf);
    return hmsc;
  }

  @Override
  public void destroyObject(Object client) throws Exception {
    HiveMetaStoreClient hmsc = (HiveMetaStoreClient) client;
    hmsc.close();
  }

  @Override
  public boolean validateObject(Object client) {
    HiveMetaStoreClient hmsc = (HiveMetaStoreClient) client;
    try {
      Database database = hmsc.getDatabase("default");
      if (database != null) {
        return true;
      }
    } catch (Exception e) {
      logger.warn("HiveMetaPoolFactory validateObject", e);
    }

    return false;
  }
}
