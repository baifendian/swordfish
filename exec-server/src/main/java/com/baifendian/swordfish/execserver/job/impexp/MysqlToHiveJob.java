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
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.HiveWriter;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.MysqlReader;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.DatasourceDao;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.MysqlReaderArg;
import org.json.JSONArray;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * mysql 导入 hive 任务
 */
public class MysqlToHiveJob extends ImpExpJob {

  private DatasourceDao datasourceDao;

  private MysqlReader mysqlReader;
  private HiveWriter hiveWriter;

  public MysqlToHiveJob(JobProps props, boolean isLongJob, Logger logger) {
    super(props, isLongJob, logger);
    datasourceDao = DaoFactory.getDaoInstance(DatasourceDao.class);
  }


  @Override
  String getDataXReader() throws Exception {
    MysqlReaderArg mysqlReaderArg = new MysqlReaderArg(mysqlReader);

    //TODO 增加一个判断根据类型
    DataSource datasource = datasourceDao.queryResource(props.getProjectId(), mysqlReader.getDatasource());
    if (datasource == null) {
      throw new NoSuchFieldException(MessageFormat.format("Datasource {0} in project {1} not found!", mysqlReader.getDatasource(), props.getProjectId()));
    }
    MysqlDatasource mysqlDatasource = (MysqlDatasource) DatasourceFactory.getDatasource(DbType.MYSQL, datasource.getParameter());

    JSONArray connection = mysqlReaderArg.getConnection();
    connection.getJSONObject(0).put("jdbcUrl", Arrays.asList(mysqlDatasource.getJdbcUrl()));
    mysqlReaderArg.setUsername(mysqlDatasource.getUser());
    mysqlReaderArg.setPassword(mysqlDatasource.getPassword());

    return JsonUtil.toJsonString(mysqlReaderArg);
  }

  @Override
  String getDateXWriter() {
    return null;
  }

  @Override
  public BaseParam getParam() {
    return null;
  }

  @Override
  public String createCommand() throws Exception {
    return null;
  }

  @Override
  public void after() throws Exception {
    super.after();
  }

  @Override
  public void initJob() {
    super.initJob();
    this.mysqlReader = (MysqlReader) impExpParam.getReader();
    this.hiveWriter = (HiveWriter) impExpParam.getWriter();
  }
}
