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
import com.baifendian.swordfish.common.job.struct.datasource.MongoDatasource;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.HiveReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.MongoWriter;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Hive 导入 Mysql 插件
 */
public class HiveToMongoJob extends DataXJob {

  public HiveToMongoJob(JobProps props, boolean isLongJob, Logger logger, ImpExpProps impExpProps) {
    super(props, isLongJob, logger, impExpProps);
  }

  @Override
  public ReaderArg getDataXReaderArg() throws Exception {
    logger.info("Start HiveToMongoJob get dataX reader arg...");

    String hiveUrl = impExpProps.getHiveConf().getString("hive.thrift.uris");
    HiveReader hiveReader = (HiveReader) impExpProps.getImpExpParam().getReader();

    HiveReaderArg hiveReaderArg = new HiveReaderArg(hiveReader);
    hiveReaderArg.setUsername(props.getProxyUser());
    ObjectNode connection = (ObjectNode) hiveReaderArg.getConnection().get(0);
    String jdbcUrl = MessageFormat.format("{0}/{1}", hiveUrl, hiveReader.getDatabase());
    connection.putArray("jdbcUrl").add(jdbcUrl);
    logger.info("Finish HiveToMongoJob get dataX reader arg!");
    return hiveReaderArg;
  }

  @Override
  public WriterArg getDateXWriterArg() throws Exception {
    logger.info("Start HiveToMongoJob get dataX writer arg...");

    MongoWriter mongoWriter = (MongoWriter) impExpProps.getImpExpParam().getWriter();

    MongoWriterArg mongoWriterArg = new MongoWriterArg(mongoWriter);
    DataSource datasource = impExpProps.getDatasourceDao().queryResource(props.getProjectId(), mongoWriter.getDatasource());
    if (datasource == null) {
      throw new NoSuchFieldException(MessageFormat.format("Datasource {0} in project {1} not found!", mongoWriter.getDatasource(), String.valueOf(props.getProjectId())));
    }
    MongoDatasource mongoDatasource = (MongoDatasource) DatasourceFactory.getDatasource(DbType.MONGODB, datasource.getParameter());
    //这里的ip格式不能直接用要去掉mongodb://前缀
    String address = mongoDatasource.getAddress();
    mongoWriterArg.setAddress(Arrays.asList(address.replace("mongodb://", "")));
    mongoWriterArg.setDbName(mongoDatasource.getDatabase());
    logger.info("Finish HiveToMongoJob get dataX writer arg!");
    return mongoWriterArg;
  }
}
