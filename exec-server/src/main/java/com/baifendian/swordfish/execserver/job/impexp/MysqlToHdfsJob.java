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
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.HdfsWriter;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.HdfsWriterArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.ImpExpProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.MysqlReaderArg;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;

import java.text.MessageFormat;

/**
 * mysql 到 Hdfs
 */
public class MysqlToHdfsJob extends DataXJob {

  public MysqlToHdfsJob(JobProps props, boolean isLongJob, Logger logger, ImpExpProps impExpProps) {
    super(props, isLongJob, logger, impExpProps);
  }

  @Override
  public MysqlReaderArg getDataXReaderArg() throws Exception {
    logger.info("Start MysqlToHdfsJob get dataX reader arg...");

    MysqlReader mysqlReader = (MysqlReader) impExpProps.getImpExpParam().getReader();


    MysqlReaderArg mysqlReaderArg = new MysqlReaderArg(mysqlReader);
    //TODO 增加一个判断根据类型
    DataSource datasource = impExpProps.getDatasourceDao().queryResource(props.getProjectId(), mysqlReader.getDatasource());
    if (datasource == null) {
      throw new NoSuchFieldException(MessageFormat.format("Datasource {0} in project {1} not found!", mysqlReader.getDatasource(), props.getProjectId()));
    }
    MysqlDatasource mysqlDatasource = (MysqlDatasource) DatasourceFactory.getDatasource(DbType.MYSQL, datasource.getParameter());

    ObjectNode connection = (ObjectNode) mysqlReaderArg.getConnection().get(0);
    connection.putArray("jdbcUrl").add(mysqlDatasource.getJdbcUrl());
    mysqlReaderArg.setUsername(mysqlDatasource.getUser());
    mysqlReaderArg.setPassword(mysqlDatasource.getPassword());
    logger.info("Finish MysqlToHdfsJob get dataX reader arg!");
    return mysqlReaderArg;
  }

  @Override
  public HdfsWriterArg getDateXWriterArg() throws Exception {
    logger.info("Start MysqlToHdfsJob get dataX writer arg...");

    HdfsWriter hdfsWriter = (HdfsWriter) impExpProps.getImpExpParam().getWriter();
    String defaultFS = impExpProps.getHadoopConf().getString("fs.defaultFS");

    HdfsWriterArg hdfsWriterArg = new HdfsWriterArg();
    hdfsWriterArg.setPath(hdfsWriter.getPath());
    hdfsWriterArg.setFileName(hdfsWriter.getFileName());
    hdfsWriterArg.setFieldDelimiter(hdfsWriter.getFieldDelimiter());
    hdfsWriterArg.setDefaultFS(defaultFS);
    hdfsWriterArg.setColumn(hdfsWriter.getColumn());
    hdfsWriterArg.setWriteMode(hdfsWriter.getWriteMode().getHdfsType());
    hdfsWriterArg.setFileType(hdfsWriter.getFileType());

    logger.info("Finish MysqlToHdfsJob get dataX writer arg...");
    return hdfsWriterArg;
  }

}
