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
import com.baifendian.swordfish.common.enums.WriteHdfsType;
import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.common.hadoop.ConfigurationUtil;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
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
import com.baifendian.swordfish.execserver.job.impexp.Args.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.apache.hadoop.fs.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static com.baifendian.swordfish.execserver.utils.Constants.*;
import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.*;


/**
 * mysql 导入 hive 任务
 */
public class MysqlToHiveJob extends ImpExpJob {


  private HiveService hiveService;

  /**
   * swordfish reader配置
   */
  private MysqlReader mysqlReader;

  /**
   * swordfish wirter配置
   */
  private HiveWriter hiveWriter;
  /**
   * 源 Hql 字段
   */
  private List<HqlColumn> srcColumns;

  /**
   * 目标 Hql 字段
   */
  private List<HqlColumn> destColumns;

  public MysqlToHiveJob(JobProps props, boolean isLongJob, Logger logger, ImpExpParam impExpParam) {
    super(props, isLongJob, logger, impExpParam);
    mysqlReader = (MysqlReader) impExpParam.getReader();
    hiveWriter = (HiveWriter) impExpParam.getWriter();
  }

  @Override
  public void before() throws Exception {
    logger.info("Start MysqlToHiveJob before function...");
    // 构造一个hive服务类，预备使用
    hiveService = new HiveService(hiveConf.getString("hive.thrift.uris"), hiveConf.getString("hive.metastore.uris"), props.getProxyUser(), "");
    hiveService.init();
    // 获取源HQL字段
    destColumns = hiveService.getHiveDesc(hiveWriter.getDatabase(), hiveWriter.getTable());
    // 获取源字段
    srcColumns = hiveService.checkHiveColumn(hiveWriter.getColumn(), destColumns);
    logger.info("Finish MysqlToHiveJob before function!");
  }

  @Override
  public MysqlReaderArg getDataXReaderArg() throws Exception {
    logger.info("Start MysqlToHiveJob get dataX reader arg...");
    MysqlReaderArg mysqlReaderArg = new MysqlReaderArg(mysqlReader);
    //TODO 增加一个判断根据类型
    DataSource datasource = datasourceDao.queryResource(props.getProjectId(), mysqlReader.getDatasource());
    if (datasource == null) {
      throw new NoSuchFieldException(MessageFormat.format("Datasource {0} in project {1} not found!", mysqlReader.getDatasource(), String.valueOf(props.getProjectId())));
    }
    MysqlDatasource mysqlDatasource = (MysqlDatasource) DatasourceFactory.getDatasource(DbType.MYSQL, datasource.getParameter());
    ObjectNode connection = (ObjectNode) mysqlReaderArg.getConnection().get(0);
    connection.putArray("jdbcUrl").add(mysqlDatasource.getJdbcUrl());
    mysqlReaderArg.setUsername(mysqlDatasource.getUser());
    mysqlReaderArg.setPassword(mysqlDatasource.getPassword());
    logger.info("Finish MysqlToHiveJob get dataX reader arg!");
    return mysqlReaderArg;
  }

  @Override
  public HdfsWriterArg getDateXWriterArg() throws Exception {
    logger.info("Start MysqlToHiveJob get dataX writer arg...");
    //由于DataX不能直接写入到hive中，我们这里先生成写入到HDFS的任务。
    String path = BaseConfig.getHdfsImpExpDir(props.getProjectId(), props.getExecId(), props.getNodeName());
    HdfsClient hdfsClient = HdfsClient.getInstance();
    //如果目录不存在就新建
    if (!hdfsClient.exists(path)) {
      logger.info("path: {} not exists try create", path);
      hdfsClient.mkdir(path, FsPermission.createImmutable((short) 0777));
    }
    //设置权限
    Path dir = new Path(path);
    while (!dir.getName().equalsIgnoreCase("swordfish")) {
      hdfsClient.setPermissionThis(dir, FsPermission.createImmutable((short) 0777));
      dir = dir.getParent();
    }

    //hdfsClient.setPermission(new Path(path), FsPermission.createImmutable((short) 0777));

    //设置父目录所有人
    //Path dir = new Path(path);
    //hdfsClient.setOwner(dir, props.getProxyUser(), workConf.getString("executor.user.group"));
    //hdfsClient.setOwner(dir.getParent(), props.getProxyUser(), workConf.getString("executor.user.group"));

    HdfsWriterArg hdfsWriterArg = new HdfsWriterArg();
    hdfsWriterArg.setPath(path);
    hdfsWriterArg.setFileName(props.getNodeName());
    hdfsWriterArg.setFieldDelimiter(DEFAULT_DELIMITER);
    hdfsWriterArg.setDefaultFS(hadoopConf.getString("fs.defaultFS"));
    hdfsWriterArg.setColumn(hiveWriter.getColumn());
    hdfsWriterArg.setFileType(DEFAULT_FILE_TYPE);
    hdfsWriterArg.setWriteMode(WriteMode.OVERWRITE.getHdfsType());
    logger.info("Finish MysqlToHiveJob get dataX writer arg!");
    return hdfsWriterArg;
  }

  public void clean() throws Exception {
    //操作完成做一些清理工作
    logger.info("Start MysqlToHiveJob clean...");
    HdfsClient hdfsClient = HdfsClient.getInstance();
    logger.info("delete hdfs path: {}", ((HdfsWriterArg) writerArg).getPath());
    hdfsClient.delete(((HdfsWriterArg) writerArg).getPath(), true);
    logger.info("Finish MysqlToHiveJob clean!");
  }

  @Override
  public void after() throws Exception {
    if (exitCode != 0) {
      logger.info("DataX exec failed, job exit!");
      return;
    }

    logger.info("Start MysqlToHiveJob after function...");
    //注册临时外部表
    String srcTableName = hiveService.getTbaleName(props.getProjectId(), props.getExecId(), props.getJobAppId());
    logger.info("Start create temp hive table: {}", srcTableName);
    hiveService.createHiveTmpTable(DEFAULT_DB, srcTableName, srcColumns, ((HdfsWriterArg) writerArg).getPath(), DEFAULT_DELIMITER, "UTF-8");
    logger.info("Finsh create temp hive table: {}", srcTableName);


    //插入数据
    logger.info("Start insert to hive table: {}", hiveWriter.getTable());
    hiveService.insertTable(DEFAULT_DB, srcTableName, hiveWriter.getDatabase(), hiveWriter.getTable(), srcColumns, destColumns, hiveWriter.getWriteMode());
    logger.info("Finish insert to hive table: {}", hiveWriter.getTable());
    //hive操作完成，关闭连接释放临时表
    hiveService.close();

    //完成进行清理
    clean();
  }

}
