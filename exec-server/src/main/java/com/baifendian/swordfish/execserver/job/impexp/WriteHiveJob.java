package com.baifendian.swordfish.execserver.job.impexp;

import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.DEFAULT_DB;
import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.DEFAULT_DELIMITER;
import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.DEFAULT_FILE_TYPE;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.HiveWriter;
import com.baifendian.swordfish.dao.utils.json.JsonUtil;
import com.baifendian.swordfish.execserver.engine.hive.HiveMetaExec;
import com.baifendian.swordfish.execserver.engine.hive.HiveSqlExec;
import com.baifendian.swordfish.execserver.engine.hive.HiveUtil;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.HdfsWriterArg;
import com.baifendian.swordfish.execserver.job.impexp.Args.HqlColumn;
import com.baifendian.swordfish.execserver.job.impexp.Args.ImpExpProps;
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
 * 把XXX 写入到 hive的任务
 */
public abstract class WriteHiveJob extends DataXJob {
  private HiveWriter hiveWriter;

  private HdfsWriterArg hdfsWriterArg;

  private HiveMetaExec hiveMetaExec;

  private List<HqlColumn> destColumns;

  private List<HqlColumn> srcColumns;

  public WriteHiveJob(JobProps props, boolean isLongJob, Logger logger, ImpExpProps impExpProps) {
    super(props, isLongJob, logger, impExpProps);
    // 先获取 swordfish 读写配置方便后面使用
    hiveWriter = (HiveWriter) impExpProps.getImpExpParam().getWriter();
  }

  @Override
  public void before() throws Exception {
    super.before();
    try {
      logger.info("Start MysqlToHiveJob before function...");

      // 构造一个hive服务类，预备使用
      hiveMetaExec = new HiveMetaExec(logger);
      // 获取源HQL字段
      destColumns = hiveMetaExec.getHiveDesc(hiveWriter.getDatabase(), hiveWriter.getTable());
      logger.info("dest col: {}", JsonUtil.toJsonString(destColumns));

      // 获取源字段
      srcColumns = hiveMetaExec.checkHiveColumn(hiveWriter.getColumn(), destColumns);

      logger.info("Finish MysqlToHiveJob before function!");
    } catch (Exception e) {
      logger.error("MysqlToHiveJob before function error", e);
      throw e;
    }

  }

  @Override
  public final HdfsWriterArg getDateXWriterArg() throws Exception {
    logger.info("Start MysqlToHiveJob get dataX writer arg...");
    //由于DataX不能直接写入到hive中，我们这里先生成写入到HDFS的任务。
    String path = BaseConfig
        .getHdfsImpExpDir(props.getProjectId(), props.getExecId(), props.getNodeName());
    HdfsClient hdfsClient = HdfsClient.getInstance();

    //如果目录不存在就新建
    if (!hdfsClient.exists(path)) {
      logger.info("path: {} not exists try create", path);
      hdfsClient.mkdir(path, FsPermission.createImmutable((short) 0777));
    }

    //设置权限
    Path dir = new Path(path);
    Path dirBase = new Path(BaseConfig.getHdfsImpExpDir(props.getProjectId()));
    while (!dir.getName().equalsIgnoreCase(dirBase.getParent().getName())) {
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
    hdfsWriterArg.setDefaultFS(impExpProps.getHadoopConf().getString("fs.defaultFS"));
    hdfsWriterArg.setColumn(((HiveWriter) impExpProps.getImpExpParam().getWriter()).getColumn());
    hdfsWriterArg.setFileType(DEFAULT_FILE_TYPE);
    hdfsWriterArg.setWriteMode(WriteMode.OVERWRITE.getHdfsType());
    logger.info("Finish MysqlToHiveJob get dataX writer arg!");
    this.hdfsWriterArg = hdfsWriterArg;
    return hdfsWriterArg;
  }

  @Override
  public void process() throws Exception {
    super.process();

    if (exitCode != 0) {
      logger.info("DataX exec failed, job exit!");
      return;
    }

    exitCode = -1;

    List<String> execSqls = new ArrayList<>();

    try {
      logger.info("Start MysqlToHiveJob after function...");

      // 注册临时外部表
      String srcTableName = HiveUtil.getTmpTableName(props.getProjectId(), props.getExecId());

      // 构造生成临时表 sql
      String ddl = HiveUtil
          .getTmpTableDDL(DEFAULT_DB, srcTableName, srcColumns, hdfsWriterArg.getPath(),
              DEFAULT_DELIMITER, "UTF-8");
      logger.info("Create temp hive table ddl: {}", ddl);

      // 构造插入数据 sql
      String insertSql = insertTable(DEFAULT_DB, srcTableName, hiveWriter.getDatabase(),
          hiveWriter.getTable(), srcColumns, destColumns, hiveWriter.getWriteMode());
      logger.info("Insert to hive table sql: {}", insertSql);

      logger.info("Start exec sql to hive...");
      execSqls = Arrays.asList(ddl, "SET hive.exec.dynamic.partition.mode=nonstrict", insertSql);

      // 执行 sql
      HiveSqlExec hiveSqlExec = new HiveSqlExec(this::logProcess, props.getProxyUser(), logger);

      exitCode = (hiveSqlExec.execute(null, execSqls, false, null, null, getRemainTime())) ? 0 : -1;

      logger.info("Finish exec sql!");
    } catch (Exception e) {
      logger.error(String.format("hql process exception, sql: %s", String.join(";", execSqls)), e);
      exitCode = -1;
    }
  }

  @Override
  public BaseParam getParam() {
    return impExpProps.getImpExpParam();
  }

  /**
   * 生成insert sql
   */
  public String insertTable(String srcDbNmae, String srcTableName, String destDbName,
      String destTableName, List<HqlColumn> srcHqlColumnList, List<HqlColumn> destHqlColumnList,
      WriteMode writeMode) throws Exception {
    String insertSql = "INSERT {0} TABLE {1}.{2} {3} SELECT {4} FROM {5}.{6}";
    String partFieldSql = "";

    // 所有的分区都是必传字段先整理出分区字段
    List<FieldSchema> partFieldList = hiveMetaExec.getPartionField(destDbName, destTableName);

    if (CollectionUtils.isNotEmpty(partFieldList)) {
      List<String> partNameList = new ArrayList<>();
      for (FieldSchema fieldSchema : partFieldList) {
        partNameList.add(fieldSchema.getName());
      }

      partFieldSql = MessageFormat.format("PARTITION({0})", String.join(",", partNameList));
    }

    List<String> fieldList = new ArrayList<>();

    // 预处理字段，如果字段为空就加上 NULL
    for (HqlColumn destHqlColumn : destHqlColumnList) {
      boolean found = false;
      for (HqlColumn srcHqlColumn : srcHqlColumnList) {
        if (StringUtils.containsIgnoreCase(srcHqlColumn.getName(), destHqlColumn.getName())) {
          fieldList.add(MessageFormat.format("`{0}`", destHqlColumn.getName()));
          found = true;
          break;
        }
      }
      if (!found) {
        fieldList.add(MessageFormat.format("null as `{0}`", destHqlColumn.getName()));
      }
    }

    insertSql = MessageFormat
        .format(insertSql, writeMode.gethiveSql(), destDbName, destTableName, partFieldSql,
            String.join(",", fieldList), srcDbNmae, srcTableName);
    logger.info("Insert table sql: {}", insertSql);
    return insertSql;
  }
}
