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

    JSONArray connection = mysqlReaderArg.getConnection();
    connection.getJSONObject(0).put("jdbcUrl", Arrays.asList(mysqlDatasource.getJdbcUrl()));
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
    hdfsWriterArg.setFileName(DEFAULT_FILE_TYPE);

    logger.info("Finish MysqlToHdfsJob get dataX writer arg...");
    return hdfsWriterArg;
  }

  @Override
  public void clean() throws Exception {
    //不需要做清理
  }

  @Override
  public void afterWorke() throws Exception {
    //无相关后处理
  }
}
