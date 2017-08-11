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

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveMetaPoolClient {

  private static final Logger logger = LoggerFactory.getLogger(HiveMetaPoolClient.class);

  private GenericObjectPool pool;

  /**
   * 最大活跃连接数
   */
  private int maxActive = 256;

  /**
   * 链接池中最大空闲的连接数
   */
  private int maxIdle = 64;

  /**
   * 连接池中最少空闲的连接数
   */
  private int minIdle = 0;

  /**
   * 当连接池资源耗尽时，调用者最大阻塞的时间
   */
  private int maxWait = 2000;

  /**
   * 空闲链接检测线程，检测的周期，毫秒数，-1 表示关闭空闲检测
   */
  private int timeBetweenEvictionRunsMillis = 180000;

  /**
   * 空闲时是否进行连接有效性验证，如果验证失败则移除，默认为 true
   */
  private boolean testWhileIdle = true;

  /**
   * hive 的连接客户端
   */
  private static HiveMetaPoolClient hiveMetaPoolClient;

  private HiveMetaPoolClient(String metastoreUris) {
    try {
      pool = bulidClientPool(metastoreUris);
    } catch (Exception e) {
      logger.error("build client pool exception", e);
    }
  }

  public static void init(String metastoreUris) {
    if (hiveMetaPoolClient == null) {
      synchronized (HiveMetaPoolClient.class) {
        if (hiveMetaPoolClient == null) {
          hiveMetaPoolClient = new HiveMetaPoolClient(metastoreUris);
        }
      }
    }
  }

  public static HiveMetaPoolClient getInstance() {
    if (hiveMetaPoolClient == null) {
      logger.error("Get HiveMetaPoolClient failed，please init first");
      throw new RuntimeException("Get HiveMetaPoolClient failed，please init first");
    }

    return hiveMetaPoolClient;
  }

  protected GenericObjectPool bulidClientPool(String metastoreUris) {
    // 设置 poolConfig
    GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();

    poolConfig.maxActive = maxActive;
    poolConfig.maxIdle = maxIdle;
    poolConfig.minIdle = minIdle;
    poolConfig.maxWait = maxWait;
    poolConfig.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    poolConfig.testWhileIdle = testWhileIdle;
    poolConfig.testOnBorrow = true;
    poolConfig.testOnReturn = true;

    HiveMetaStorePoolFactory clientFactory = new HiveMetaStorePoolFactory(metastoreUris);
    return new GenericObjectPool(clientFactory, poolConfig);
  }

  /**
   * 从连接池获取一个具体的 hive 连接
   */
  public HiveMetaStoreClient borrowClient() throws Exception {
    return (HiveMetaStoreClient) pool.borrowObject();
  }

  /**
   * 返回一个 hive 连接对象
   */
  public void returnClient(HiveMetaStoreClient client) {
    if (client != null) {
      try {
        pool.returnObject(client);
      } catch (Exception e) {
        logger.warn("HiveMetaStoreClient returnClient exception", e);
      }
    }
  }

  /**
   * 校验连接信息是否合法
   */
  public void invalidateObject(HiveMetaStoreClient client) {
    try {
      pool.invalidateObject(client);
    } catch (Exception e) {
      logger.error("HiveMetaStoreClient invalidateObject error", e);
    }
  }

  /**
   * 清空连接池
   */
  public void clear() {
    pool.clear();
  }
}