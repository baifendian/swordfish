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

import com.baifendian.swordfish.common.hive.ConnectionInfo;
import com.baifendian.swordfish.common.hive.HiveConnectionClient;
import com.baifendian.swordfish.dao.BaseDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HiveJdbcExec extends BaseDao {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public static final int DEFAULT_QUERY_PROGRESS_THREAD_TIMEOUT = 10 * 1000;

  @Autowired
  HiveConfig hiveConfig;

  @Autowired
  HiveConnectionClient hiveConnectionClient;

  /**
   * 采用非注解方式的时候, 需要自己获取这些实例
   */
  @Override
  public void init() {
    hiveConfig = MyHiveFactoryUtil.getInstance();
    hiveConnectionClient = hiveConfig.hiveConnectionClient();

    logger.info("Hive config, thrift uri:{}, meta uri:{}", hiveConfig.getThriftUris(), hiveConfig.getMetastoreUris());
  }

  /**
   * 判断是否是查询请求
   *
   * @param sql
   * @return
   */
  public static boolean isTokQuery(String sql) {
    if (StringUtils.isEmpty(sql)) {
      return false;
    }

    sql = sql.toLowerCase();

    if (sql.startsWith("select")
        || sql.startsWith("describe")
        || sql.startsWith("explain")) {
      return true;
    }

    return false;
  }

  /**
   * 是否类似于 show 语句的查询（show/desc/describe） <p>
   *
   * @param sql
   * @return 如果是 'show/desc/describe' 语句返回 true, 否则返回 false
   */
  public static boolean isLikeShowStm(String sql) {
    if (StringUtils.isEmpty(sql)) {
      return false;
    }

    sql = sql.toLowerCase();

    if (sql.startsWith("show") || sql.startsWith("desc")) {
      return true;
    }

    return false;
  }

  public HiveConnectionClient getHiveConnectionClient() {
    return hiveConnectionClient;
  }

  /**
   * 获取连接信息 <p>
   *
   * @param userName
   * @return
   * @see {@link ConnectionInfo}
   */
  public ConnectionInfo getConnectionInfo(String userName) {
    ConnectionInfo connectionInfo = new ConnectionInfo();

    connectionInfo.setUser(userName);
    connectionInfo.setUri(hiveConfig.getThriftUris());

    return connectionInfo;
  }
}
