package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.job.struct.datasource.DatasourceFactory;
import com.baifendian.swordfish.common.job.struct.datasource.MysqlDatasource;
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;
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
 * mysql 到 Hdfs
 */
public class MysqlToHdfsJob extends ImpExpJob {

  public MysqlToHdfsJob(JobProps props, boolean isLongJob, Logger logger, ImpExpParam impExpParam) {
    super(props, isLongJob, logger, impExpParam);
  }

  @Override
  public String createCommand() throws Exception {
    return null;
  }

  @Override
  public void initJob() {

  }

  @Override
  public String getDataXReader() throws Exception {
    return null;
  }

  @Override
  public String getDateXWriter() {
    return null;
  }

  @Override
  public void clean() {

  }
}
