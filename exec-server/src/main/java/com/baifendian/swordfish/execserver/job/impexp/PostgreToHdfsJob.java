package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.job.struct.datasource.DatasourceFactory;
import com.baifendian.swordfish.common.job.struct.datasource.PostgreDatasource;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.PostgreReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.HdfsWriter;
import com.baifendian.swordfish.dao.enums.DbType;
import com.baifendian.swordfish.dao.model.DataSource;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.HdfsWriterArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.ImpExpProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.PostgreReaderArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.ReaderArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.WriterArg;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.text.MessageFormat;
import org.slf4j.Logger;

public class PostgreToHdfsJob extends DataXJob  {

  public PostgreToHdfsJob(JobProps props, boolean isLongJob,
      Logger logger,
      ImpExpProps impExpProps) {
    super(props, isLongJob, logger, impExpProps);
  }

  @Override
  public ReaderArg getDataXReaderArg() throws Exception {
    logger.info("Start PostgreToHdfsJob get dataX reader arg...");

    PostgreReader postgreReader = (PostgreReader) impExpProps.getImpExpParam().getReader();
    PostgreReaderArg postgreReaderArg = new PostgreReaderArg(postgreReader);

    // TODO 增加一个判断根据类型
    DataSource datasource = impExpProps.getDatasourceDao()
        .queryResource(props.getProjectId(), postgreReader.getDatasource());
    if (datasource == null) {
      throw new NoSuchFieldException(MessageFormat
          .format("Datasource {0} in project {1} not found!", postgreReader.getDatasource(),
              String.valueOf(props.getProjectId())));
    }

    PostgreDatasource postgreDatasource = (PostgreDatasource) DatasourceFactory
        .getDatasource(DbType.POSTGRES, datasource.getParameter());
    ObjectNode connection = (ObjectNode) postgreReaderArg.getConnection().get(0);
    connection.putArray("jdbcUrl").add(postgreDatasource.getJdbcUrl());
    postgreReaderArg.setUsername(postgreDatasource.getUser());
    postgreReaderArg.setPassword(postgreDatasource.getPassword());

    logger.info("Finish PostgreToHdfsJob get dataX reader arg!");

    return postgreReaderArg;
  }

  @Override
  public WriterArg getDateXWriterArg() throws Exception {
    logger.info("Start PostgreToHdfsJob get dataX writer arg...");

    HdfsWriter hdfsWriter = (HdfsWriter) impExpProps.getImpExpParam().getWriter();
    String defaultFS = impExpProps.getHadoopConf().getString("fs.defaultFS");

    HdfsWriterArg hdfsWriterArg = new HdfsWriterArg();
    hdfsWriterArg.setPath(hdfsWriter.getPath());
    hdfsWriterArg.setFileName(hdfsWriter.getFileName());
    hdfsWriterArg.setFieldDelimiter(hdfsWriter.getFieldDelimiter());
    hdfsWriterArg.setDefaultFS(defaultFS);
    hdfsWriterArg.setColumn(hdfsWriter.getColumn());
    hdfsWriterArg.setWriteMode(hdfsWriter.getWriteMode().getHdfsType());
    hdfsWriterArg.setFileType(hdfsWriter.getFileType());

    logger.info("Finish PostgreToHdfsJob get dataX writer arg...");
    return hdfsWriterArg;
  }
}
