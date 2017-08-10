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

import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源工厂
 */
public class DatasourceFactory {

  private static Logger logger = LoggerFactory.getLogger(DatasourceFactory.class.getName());

  public static Datasource getDatasource(DbType dbType, String parameter) {
    try {
      switch (dbType) {
        case MONGODB:
          return JsonUtil.parseObject(parameter, MongoDatasource.class);
        case HBASE:
          return JsonUtil.parseObject(parameter, HBaseDatasource.class);
        case MYSQL:
          return JsonUtil.parseObject(parameter, MysqlDatasource.class);
        case ORACLE:
          return JsonUtil.parseObject(parameter, OracleDatasource.class);
        case FTP:
          return JsonUtil.parseObject(parameter, FtpDatasource.class);
        case POSTGRE:
          return JsonUtil.parseObject(parameter, PostgreDatasource.class);
        default:
          return null;
      }
    } catch (Exception e) {
      logger.error("Get datasource object error", e);
      return null;
    }
  }
}
