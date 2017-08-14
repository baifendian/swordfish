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

import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.DEFAULT_DB;
import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.DEFAULT_DELIMITER;
import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.DEFAULT_FILE_TYPE;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.job.struct.datasource.DatasourceFactory;
import com.baifendian.swordfish.common.job.struct.datasource.MysqlDatasource;
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.MysqlReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.HiveWriter;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.engine.hive.HiveMetaExec;
import com.baifendian.swordfish.execserver.engine.hive.HiveSqlExec;
import com.baifendian.swordfish.execserver.engine.hive.HiveUtil;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.HdfsWriterArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.HqlColumn;
import com.baifendian.swordfish.execserver.job.impexp.Args.ImpExpProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.MysqlReaderArg;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.slf4j.Logger;

/**
 * mysql 导入 hive 任务
 */
public class MysqlToHiveJob extends WriteHiveJob {

  public MysqlToHiveJob(JobProps props, boolean isLongJob, Logger logger, ImpExpProps impExpProps) {
    super(props, isLongJob, logger, impExpProps);
    // 先获取 swordfish 读写配置方便后面使用
  }

  @Override
  public MysqlReaderArg getDataXReaderArg() throws Exception {
    logger.info("Start MysqlToHiveJob get dataX reader arg...");

    MysqlReader mysqlReader = (MysqlReader) impExpProps.getImpExpParam().getReader();
    MysqlReaderArg mysqlReaderArg = new MysqlReaderArg(mysqlReader);

    // TODO 增加一个判断根据类型
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
