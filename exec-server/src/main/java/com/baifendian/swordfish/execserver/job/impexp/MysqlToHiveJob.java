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

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.hadoop.ConfigurationUtil;
import com.baifendian.swordfish.common.job.struct.datasource.DatasourceFactory;
import com.baifendian.swordfish.common.job.struct.datasource.MysqlDatasource;
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.HiveWriter;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.MysqlReader;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.DatasourceDao;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.HdfsWriterArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.MysqlReaderArg;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.json.JSONArray;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Arrays;

import static com.baifendian.swordfish.execserver.utils.Constants.*;
import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.*;


/**
 * mysql 导入 hive 任务
 */
public class MysqlToHiveJob extends ImpExpJob {

  //默认使用逗号分隔
  private final String DEFAULT_DELIMITER = ",";

  private MysqlReader mysqlReader;
  private HiveWriter hiveWriter;

  private String hdfsPath;


  public MysqlToHiveJob(JobProps props, boolean isLongJob, Logger logger, ImpExpParam impExpParam) {
    super(props, isLongJob, logger, impExpParam);
    this.mysqlReader = (MysqlReader) impExpParam.getReader();
    this.hiveWriter = (HiveWriter) impExpParam.getWriter();
  }


  @Override
  public String getDataXReader() throws Exception {
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
  public String getDateXWriter() throws Exception {
    //由于DataX不能直接写入到hive中，我们这里先生成写入到HDFS的任务。
    String path = BaseConfig.getHdfsImpExpDir(props.getProjectId(), props.getExecId(), props.getExecId(), props.getNodeName());

    HdfsWriterArg hdfsWriterArg = new HdfsWriterArg();
    hdfsWriterArg.setPath(path);
    hdfsWriterArg.setFileName(props.getNodeName());
    hdfsWriterArg.setFieldDelimiter(DEFAULT_DELIMITER);
    hdfsWriterArg.setDefaultFS(hadoopConf.getString("fs.defaultFS"));

    return JsonUtil.toJsonString(hdfsWriterArg);
  }

  @Override
  public void clean() {

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
    //如果dataX任务执行失败不用做后续操作
    if (exitCode != 0){
      return;
    }

    try {
      Class.forName(HIVE_DRIVER);
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(1);
    }

    Connection con = DriverManager.getConnection(hiveConf.getString("hive.thrift.uris"), "", "");
    Statement stmt = con.createStatement();

    //TODO 创建外部表，构造插入语句

  }


}
