package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.job.struct.datasource.DatasourceFactory;
import com.baifendian.swordfish.common.job.struct.datasource.PostgreDatasource;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.HiveReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.PostgreWriter;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.HiveReaderArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.ImpExpProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.PostgreWriterArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.ReaderArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.WriterArg;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.text.MessageFormat;
import org.slf4j.Logger;

public class HiveToPostgreJob extends DataXJob {

  public HiveToPostgreJob(JobProps props, boolean isLongJob,
      Logger logger,
      ImpExpProps impExpProps) {
    super(props, isLongJob, logger, impExpProps);
  }

  @Override
  public ReaderArg getDataXReaderArg() throws Exception {
    logger.info("Start HiveToPostgreJob get dataX reader arg...");

    String hiveUrl = impExpProps.getHiveConf().getString("hive.thrift.uris");
    HiveReader hiveReader = (HiveReader) impExpProps.getImpExpParam().getReader();

    HiveReaderArg hiveReaderArg = new HiveReaderArg(hiveReader);
    hiveReaderArg.setUsername(props.getProxyUser());
    ObjectNode connection = (ObjectNode) hiveReaderArg.getConnection().get(0);
    String jdbcUrl = MessageFormat.format("{0}/{1}", hiveUrl, hiveReader.getDatabase());
    connection.putArray("jdbcUrl").add(jdbcUrl);
    logger.info("Finish HiveToPostgreJob get dataX reader arg!");
    return hiveReaderArg;
  }

  @Override
  public WriterArg getDateXWriterArg() throws Exception {
    logger.info("Start HiveToPostgreJob get dataX writer arg...");

    PostgreWriter postgreWriter = (PostgreWriter) impExpProps.getImpExpParam().getWriter();

    PostgreWriterArg postgreWriterArg = new PostgreWriterArg(postgreWriter);

    DataSource datasource = impExpProps.getDatasourceDao()
        .queryResource(props.getProjectId(), postgreWriter.getDatasource());

    if (datasource == null) {
      throw new NoSuchFieldException(MessageFormat
          .format("Datasource {0} in project {1} not found!", postgreWriter.getDatasource(),
              props.getProjectId()));
    }

    PostgreDatasource postgreDatasource = (PostgreDatasource) DatasourceFactory
        .getDatasource(DbType.POSTGRES, datasource.getParameter());

    ObjectNode connection = (ObjectNode) postgreWriterArg.getConnection().get(0);
    connection.put("jdbcUrl", postgreDatasource.getJdbcUrl());

    postgreWriterArg.setUsername(postgreDatasource.getUser());
    postgreWriterArg.setPassword(postgreDatasource.getPassword());

    logger.info("Finish HiveToPostgreJob get dataX writer arg...");

    return postgreWriterArg;
  }
}
