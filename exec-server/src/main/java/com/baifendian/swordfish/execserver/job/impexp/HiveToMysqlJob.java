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
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.HiveReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.MysqlWriter;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;

import java.text.MessageFormat;

/**
 * hive 导出到 mysql
 */
public class HiveToMysqlJob extends ImpExpJob {

  private HiveReader hiveReader;
  private MysqlWriter mysqlWriter;

  public HiveToMysqlJob(JobProps props, boolean isLongJob, Logger logger, ImpExpParam impExpParam) {
    super(props, isLongJob, logger, impExpParam);
    hiveReader = (HiveReader) impExpParam.getReader();
    mysqlWriter = (MysqlWriter) impExpParam.getWriter();
  }

  @Override
  public ReaderArg getDataXReaderArg() throws Exception {
    logger.info("Start HiveToMysqlJob get dataX reader arg...");
    HiveReaderArg hiveReaderArg = new HiveReaderArg(hiveReader);
    hiveReaderArg.setUsername(props.getProxyUser());
    ObjectNode connection = (ObjectNode) hiveReaderArg.getConnection().get(0);
    String jdbcUrl = MessageFormat.format("{0}/{1}", hiveConf.getString("hive.thrift.uris"), hiveReader.getDatabase());
    connection.putArray("jdbcUrl").add(jdbcUrl);
    logger.info("Finish HiveToMysqlJob get dataX reader arg!");
    return hiveReaderArg;
  }

  @Override
  public WriterArg getDateXWriterArg() throws Exception {
    logger.info("Start HiveToMysqlJob get dataX writer arg...");
    MysqlWriterArg mysqlWriterArg = new MysqlWriterArg(mysqlWriter);

    DataSource datasource = datasourceDao.queryResource(props.getProjectId(), mysqlWriter.getDatasource());
    if (datasource == null) {
      throw new NoSuchFieldException(MessageFormat.format("Datasource {0} in project {1} not found!", mysqlWriter.getDatasource(), props.getProjectId()));
    }
    MysqlDatasource mysqlDatasource = (MysqlDatasource) DatasourceFactory.getDatasource(DbType.MYSQL, datasource.getParameter());

    ObjectNode connection = (ObjectNode) mysqlWriterArg.getConnection().get(0);
    connection.put("jdbcUrl", mysqlDatasource.getJdbcUrl());

    mysqlWriterArg.setUsername(mysqlDatasource.getUser());
    mysqlWriterArg.setPassword(mysqlDatasource.getPassword());
    logger.info("Finish HiveToMysqlJob get dataX writer arg...");
    return mysqlWriterArg;
  }

}
