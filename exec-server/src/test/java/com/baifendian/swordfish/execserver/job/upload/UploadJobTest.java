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
package com.baifendian.swordfish.execserver.job.upload;

import com.baifendian.swordfish.execserver.utils.hive.HiveConfig;
import com.baifendian.swordfish.execserver.utils.hive.MyHiveFactoryUtil;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class UploadJobTest {
  private HiveMetaStoreClient hiveMetaStoreClient;

  @Before
  public void before() throws Exception {
    HiveConfig hiveConfig = MyHiveFactoryUtil.getInstance();
    hiveMetaStoreClient = hiveConfig.hiveMetaStoreClient();
  }

  @Test
  public void testHiveMeta() throws TException {
    List<String> dbs = hiveMetaStoreClient.getAllDatabases();
    System.out.println(dbs);
    List<String> tbs = hiveMetaStoreClient.getAllTables("default");
    System.out.println(tbs);
    Table table = hiveMetaStoreClient.getTable("bfd_test", "test");
    System.out.println(table);
  }
}
