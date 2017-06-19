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
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.MysqlReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.HdfsWriter;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.HiveWriter;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.HdfsWriterArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.MysqlReaderArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.ReaderArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.WriterArg;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.configuration.ConfigurationException;
import org.json.JSONArray;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.Arrays;

import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.DEFAULT_DELIMITER;
import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.DEFAULT_FILE_TYPE;

/**
 * mysql 到 Hdfs
 */
public class MysqlToHdfsJob extends ImpExpJob {

  /**
   * swordfish reader配置
   */
  private MysqlReader mysqlReader;

  /**
   * swordfish wirter配置
   */
  private HdfsWriter hdfsWriter;

  public MysqlToHdfsJob(JobProps props, boolean isLongJob, Logger logger, ImpExpParam impExpParam) {
    super(props, isLongJob, logger, impExpParam);
    mysqlReader = (MysqlReader) impExpParam.getReader();
    hdfsWriter = (HdfsWriter) impExpParam.getWriter();
  }


  @Override
  public MysqlReaderArg getDataXReaderArg() throws Exception {
    logger.info("Start MysqlToHdfsJob get dataX reader arg...");
    MysqlReaderArg mysqlReaderArg = new MysqlReaderArg(mysqlReader);
    //TODO 增加一个判断根据类型
    DataSource datasource = datasourceDao.queryResource(props.getProjectId(), mysqlReader.getDatasource());
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
  public HdfsWriterArg getDateXWriterArg() throws ConfigurationException, Exception {
    logger.info("Start MysqlToHdfsJob get dataX writer arg...");
    HdfsWriterArg hdfsWriterArg = new HdfsWriterArg();
    hdfsWriterArg.setPath(hdfsWriter.getPath());
    hdfsWriterArg.setFileName(props.getNodeName());
    hdfsWriterArg.setFieldDelimiter(DEFAULT_DELIMITER);
    hdfsWriterArg.setDefaultFS(hadoopConf.getString("fs.defaultFS"));
    hdfsWriterArg.setColumn(hdfsWriter.getColumn());
    hdfsWriterArg.setWriteMode(hdfsWriter.getWriteMode().getHdfsType());
    hdfsWriterArg.setFileType(hdfsWriter.getFileType());

    logger.info("Finish MysqlToHdfsJob get dataX writer arg...");
    return hdfsWriterArg;
  }

}
