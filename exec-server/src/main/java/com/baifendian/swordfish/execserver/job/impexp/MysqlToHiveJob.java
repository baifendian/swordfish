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
package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.job.struct.datasource.DatasourceFactory;
import com.baifendian.swordfish.common.job.struct.datasource.MysqlDatasource;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.MysqlReader;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.ImpExpProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.MysqlReaderArg;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.text.MessageFormat;
import org.slf4j.Logger;

/**
 * mysql 导入 hive 任务
 */
public class MysqlToHiveJob extends WriteHiveJob {

  public MysqlToHiveJob(JobProps props, boolean isLongJob, Logger logger, ImpExpProps impExpProps) {
    super(props, isLongJob, logger, impExpProps);
  }

  @Override
  public MysqlReaderArg getDataXReaderArg() throws Exception {
    logger.info("Start MysqlToHiveJob get dataX reader arg...");

    MysqlReader mysqlReader = (MysqlReader) impExpProps.getImpExpParam().getReader();
    MysqlReaderArg mysqlReaderArg = new MysqlReaderArg(mysqlReader);

    // TODO:: 增加一个判断根据类型
    DataSource datasource = impExpProps.getDatasourceDao()
        .queryResource(props.getProjectId(), mysqlReader.getDatasource());
    if (datasource == null) {
      throw new NoSuchFieldException(MessageFormat
          .format("Datasource {0} in project {1} not found!", mysqlReader.getDatasource(),
              String.valueOf(props.getProjectId())));
    }

    MysqlDatasource mysqlDatasource = (MysqlDatasource) DatasourceFactory
        .getDatasource(DbType.MYSQL, datasource.getParameter());
    ObjectNode connection = (ObjectNode) mysqlReaderArg.getConnection().get(0);
    connection.putArray("jdbcUrl").add(mysqlDatasource.getJdbcUrl());
    mysqlReaderArg.setUsername(mysqlDatasource.getUser());
    mysqlReaderArg.setPassword(mysqlDatasource.getPassword());

    logger.info("Finish MysqlToHiveJob get dataX reader arg!");

    return mysqlReaderArg;
  }
}
