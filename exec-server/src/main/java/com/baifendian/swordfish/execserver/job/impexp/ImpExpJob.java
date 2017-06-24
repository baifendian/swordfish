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

import com.baifendian.swordfish.common.hadoop.ConfigurationUtil;
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.BaseParamFactory;
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;

import com.baifendian.swordfish.common.job.struct.node.impexp.reader.MysqlReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.Reader;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.HiveWriter;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.Writer;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.DatasourceDao;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.job.AbstractProcessJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.HdfsWriterArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.MysqlReaderArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.ReaderArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.WriterArg;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.apache.avro.data.Json;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

import static com.baifendian.swordfish.common.job.struct.node.JobType.*;

import org.json.JSONException;
import org.slf4j.Logger;

import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * 导入任务接口
 */
public abstract class ImpExpJob extends AbstractProcessJob {

  protected DatasourceDao datasourceDao;

  /**
   * 读取的配置文件
   */
  protected Configuration hadoopConf;
  protected Configuration workConf;
  protected Configuration hiveConf;

  /**
   * swordfish的导入导出配置
   */
  protected ImpExpParam impExpParam;

  /**
   * swordfish 读配置
   */
  protected WriterArg writerArg;

  /**
   * swordfish 写配置
   */
  protected ReaderArg readerArg;


  public ImpExpJob(JobProps props, boolean isLongJob, Logger logger, ImpExpParam impExpParam) {
    super(props, isLongJob, logger);
    this.impExpParam = impExpParam;
  }

  @Override
  public void init() {
    logger.info("Start init base job...");
    datasourceDao = DaoFactory.getDaoInstance(DatasourceDao.class);
    try {
      hadoopConf = new PropertiesConfiguration("common/hadoop/hadoop.properties");
      workConf = new PropertiesConfiguration("worker.properties");
      hiveConf = new PropertiesConfiguration("common/hive/hive.properties");
    } catch (ConfigurationException e) {
      logger.error("Init work conf error", e);
    }
    logger.info("Finish init base job!");
  }

  /**
   * 生成datax需要的json
   *
   * @return
   */
  public final String getDataXJson() throws Exception {
    logger.info("Start get DataX json ...");
    readerArg = getDataXReaderArg();
    writerArg = getDateXWriterArg();
    String readerJson = JsonUtil.toJsonString(readerArg);
    String writerJson = JsonUtil.toJsonString(writerArg);
    String settingJson = JsonUtil.toJsonString(impExpParam.getSetting());
    String json = MessageFormat.format(DATAX_JSON, readerArg.dataxName(), readerJson, writerArg.dataxName(), writerJson, settingJson);
    logger.info("Finish get DataX json: {}", json);
    logger.info("Start parameter replacement...");
    json = ParamHelper.resolvePlaceholders(json, props.getDefinedParams());
    logger.info("Finish parameter replacement, json:{}", json);
    return json;
  }

  /**
   * 获取dataX的reader
   *
   * @return
   */
  public abstract ReaderArg getDataXReaderArg() throws Exception;

  /**
   * 获取dataX的writer
   *
   * @return
   */
  public abstract WriterArg getDateXWriterArg() throws Exception;

  /**
   * 生成datax 文件
   *
   * @return
   */
  public final File createDataXParam(String dataXJson) throws Exception {
    // 工作目录
    logger.info("Start create DataX json file...");
    String fileName = DATAX_FILE_NAME + UUID.randomUUID() + ".json";
    String path = MessageFormat.format("{0}/{1}", getWorkingDirectory(), fileName);
    logger.info("Datax json file path: {}", path);
    File file = new File(path);
    try {
      FileUtils.writeStringToFile(file, dataXJson, Charset.forName("utf-8"));
    } catch (IOException e) {
      logger.error("Create dataX json file error", e);
      throw e;
    }
    logger.info("Finish create DataX json file!");
    return file;
  }

  @Override
  public final String createCommand() throws Exception {
    logger.info("Start create DataX command...");
    File dataXJson = createDataXParam(getDataXJson());

    String dataxMian = MessageFormat.format("{0}/bin/datax.py", workConf.getString("executor.datax.home"));
    String command = MessageFormat.format(COMMAND, dataxMian, dataXJson.getAbsolutePath());
    logger.info("Finish create DataX commond: {}", command);
    return command;
  }

  @Override
  public BaseParam getParam() {
    return this.impExpParam;
  }
}
