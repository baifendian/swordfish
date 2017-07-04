package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.config.BaseConfig;
import com.baifendian.swordfish.common.enums.FileColumnType;
import com.baifendian.swordfish.common.enums.WriteMode;
import com.baifendian.swordfish.common.hadoop.HdfsClient;
import com.baifendian.swordfish.common.job.struct.node.BaseParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.FileColumn;
import com.baifendian.swordfish.common.job.struct.node.impexp.column.HiveColumn;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.FileReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.HdfsReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.reader.MysqlReader;
import com.baifendian.swordfish.common.job.struct.node.impexp.writer.HiveWriter;
import com.baifendian.swordfish.dao.DaoFactory;
import com.baifendian.swordfish.dao.DatasourceDao;
import com.baifendian.swordfish.execserver.engine.hive.HiveMetaExec;
import com.baifendian.swordfish.execserver.engine.hive.HiveSqlExec;
import com.baifendian.swordfish.execserver.engine.hive.HiveUtil;
import com.baifendian.swordfish.execserver.job.AbstractJob;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.HqlColumn;
import com.baifendian.swordfish.execserver.parameter.ParamHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.slf4j.Logger;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

import static com.baifendian.swordfish.execserver.job.impexp.ImpExpJobConst.DEFAULT_DB;

/**
 * 文件导入hive任务
 */
public class FileToHiveJob extends AbstractJob {

  /**
   * 读取的配置文件
   */
  protected Configuration hadoopConf;
  protected Configuration workConf;
  protected Configuration hiveConf;

  private ImpExpParam impExpParam;

  private HiveMetaExec hiveMetaExec;

  /**
   * swordfish reader配置
   */
  private FileReader fileReader;

  /**
   * swordfish wirter配置
   */
  private HiveWriter hiveWriter;
  /**
   * 源 Hql 字段
   */
  private List<HqlColumn> srcHiveColumns;

  /**
   * 目标Hql字段
   */
  private List<HqlColumn> destHiveColumns;

  /**
   * @param props     作业配置信息, 各类作业根据此配置信息生成具体的作业
   * @param isLongJob
   * @param logger    日志
   */
  protected FileToHiveJob(JobProps props, boolean isLongJob, Logger logger, ImpExpParam impExpParam) {
    super(props, isLongJob, logger);
    this.impExpParam = impExpParam;
    this.fileReader = (FileReader) impExpParam.getReader();
    this.hiveWriter = (HiveWriter) impExpParam.getWriter();
  }

  @Override
  public void init() {
    super.init();
    logger.info("Start init base job...");
    try {
      hadoopConf = new PropertiesConfiguration("common/hadoop/hadoop.properties");
      workConf = new PropertiesConfiguration("worker.properties");
      hiveConf = new PropertiesConfiguration("common/hive/hive.properties");
    } catch (ConfigurationException e) {
      logger.error("Init work conf error", e);
    }
    logger.info("Finish init base job!");
  }

  @Override
  public void before() throws Exception {
    super.before();
    logger.info("Start FileToHiveJob before function...");
    //变量替换
    for (FileColumn fileColumn : fileReader.getTargetColumn()) {
      fileColumn.setName(ParamHelper.resolvePlaceholders(fileColumn.getName(), props.getDefinedParams()));
    }

    //进行参数合理性检测
    checkParam();

    //构造一个hive元数据服务类
    hiveMetaExec = new HiveMetaExec(logger);

    // 获取源HQL字段
    destHiveColumns = hiveMetaExec.getHiveDesc(hiveWriter.getDatabase(), hiveWriter.getTable());
    // 获取源字段
    srcHiveColumns = hiveMetaExec.checkHiveColumn(hiveWriter.getColumn(), destHiveColumns);

    logger.info("Finish FileToHiveJob before function!");
  }

  @Override
  public void process() throws Exception {
    super.process();
    List<String> execSqls = new ArrayList<>();
    try {
      String hdfsPath = fileReader.getHdfsPath();
      String fileName = fileReader.getFileName();


      // 判断文件是否需要上传
      if (StringUtils.isNotEmpty(fileName)) {
        //1. 下载文件到本地
        logger.info("Has file name try to process file");
        String filePath = MessageFormat.format("{0}/{1}", props.getWorkDir(), fileName);
        File file = new File(filePath);
        if (!file.exists()) {
          logger.error("file {} not exists!", file.getAbsoluteFile());
          throw new Exception("file not exists!");
        }
        hdfsPath = BaseConfig.getHdfsImpExpDir(props.getProjectId(), props.getExecId(), props.getNodeName());
        logger.info("Start upload file to temp hdfs dir: {} ...", hdfsPath);
        //设置目录
        if (!HdfsClient.getInstance().exists(hdfsPath)) {
          logger.info("path: {} not exists try create", hdfsPath);
          HdfsClient.getInstance().mkdir(hdfsPath, FsPermission.createImmutable((short) 0777));
        }

        HdfsClient.getInstance().copyLocalToHdfs(filePath, hdfsPath, true, true);

        String fileHdfsPath = MessageFormat.format("{0}/{1}", hdfsPath, fileName);
        //设置权限
        Path dir = new Path(fileHdfsPath);
        while (!dir.getName().equalsIgnoreCase("swordfish")) {
          HdfsClient.getInstance().setPermissionThis(dir, FsPermission.createImmutable((short) 0777));
          dir = dir.getParent();
        }
        logger.info("Finish upload file to temp hdfs dir!");
      }

      // 1.创建临时表
      logger.info("First, create temp table...");
      String srcTable = HiveUtil.getTmpTableName(props.getProjectId(), props.getExecId());
      logger.info("Temp table name: {}", srcTable);

      //生成临时表ddl
      String ddl = HiveUtil.getTmpTableDDL(DEFAULT_DB, srcTable, getFileHqlColumn(), hdfsPath, fileReader.getFieldDelimiter(), fileReader.getFileCode());
      logger.info("Create temp hive table ddl: {}", ddl);

      // 2.插入数据
      logger.info("Second, insert into target table...");
      String sql = getInsertSql(DEFAULT_DB, srcTable, hiveWriter.getDatabase(), hiveWriter.getTable(), destHiveColumns, getFileHiveColumnRel(fileReader.getTargetColumn(), hiveWriter.getColumn()), hiveWriter.getWriteMode());

      logger.info("Start exec sql to hive...");
      execSqls = Arrays.asList(ddl, "SET hive.exec.dynamic.partition.mode=nonstrict", sql);
      HiveSqlExec hiveSqlExec = new HiveSqlExec(props.getProxyUser(), logger);

      exitCode = (hiveSqlExec.execute(null, execSqls, false, null, null)) ? 0 : -1;

      logger.info("Finish exec sql!");
    } catch (Exception e) {
      logger.error(String.format("hql process exception, sql: %s", String.join(";", execSqls)), e);
      exitCode = -1;
    } finally {
      complete = true;
    }
  }

  @Override
  public void after() throws Exception {
    super.after();
  }

  @Override
  public void cancel(boolean cancelApplication) throws Exception {

  }

  /**
   * 参数校验
   */
  public void checkParam() {
    //TODO 参数校验
  }


  /**
   * 获取导入导出字段关系
   *
   * @return
   */
  public Map<String, FileColumn> getFileHiveColumnRel(List<FileColumn> fileColumnList, List<HiveColumn> hiveColumnList) {
    Map<String, FileColumn> res = new HashMap<>();
    for (int i = 0, len = fileColumnList.size(); i < len; i++) {
      res.put(hiveColumnList.get(i).getName(), fileColumnList.get(i));
    }

    return res;
  }

  /**
   * 根据file字段，构建hive临时表hqlcolumn
   */
  public List<HqlColumn> getFileHqlColumn() {
    logger.info("Start get file hql column...");
    List<HqlColumn> res = new ArrayList<>();
    for (FileColumn fileColumn : fileReader.getSrcColumn()) {
      if (fileColumn.getType() == FileColumnType.BOOLEAN) {
        res.add(new HqlColumn(fileColumn.getName(), "boolean"));
      } else {
        res.add(new HqlColumn(fileColumn.getName(), "string"));
      }
    }
    logger.info("Finish get file hql column!");
    return res;
  }

  /**
   * 生成插入sql
   *
   * @return
   */
  public String getInsertSql(String srcDbName, String srcTable, String destDbName, String destTable, List<HqlColumn> destHiveColumns, Map<String, FileColumn> columnRet, WriteMode writeMode) throws Exception {
    logger.info("Start create insert sql...");
    String insertSql = "INSERT {0} TABLE {1}.{2} {3} SELECT {4} FROM {5}.{6}";
    String partFieldSql = "";

    // 所有的分区都是必传字段先整理出分区字段

    List<FieldSchema> partFieldList = hiveMetaExec.getPartionField(destDbName, destTable);

    if (CollectionUtils.isNotEmpty(partFieldList)) {
      List<String> partNameList = new ArrayList<>();
      for (FieldSchema fieldSchema : partFieldList) {
        partNameList.add(fieldSchema.getName());
      }

      partFieldSql = MessageFormat.format("PARTITION({0})", String.join(",", partNameList));
    }

    List<String> fieldList = new ArrayList<>();

    //预处理字段，如果字段为空就加上NULL
    for (HqlColumn destHqlColumn : destHiveColumns) {
      boolean found = false;
      String destCol = destHqlColumn.getName();
      FileColumn srcCol = columnRet.get(ImpExpUtil.exceptBackQuota(destCol));
      String srcColVal = "null";


      if (srcCol != null) {
        if (srcCol.getType() == FileColumnType.DATE) {
          //srcColVal = MessageFormat.format("CAST(TO_DATE(from_unixtime(UNIX_TIMESTAMP({0},\"{1}\"))) AS DATE)", srcCol.getName(), srcCol.getDateFormat());
          srcColVal = MessageFormat.format("CAST(date_format({0},\"{1}\") AS {2})", srcCol.getName(), srcCol.getDateFormat(), destHqlColumn.getType());
        } else {
          srcColVal = srcCol.getName();
        }
      }


      fieldList.add(MessageFormat.format("{0} as {1}", srcColVal, ImpExpUtil.addBackQuota(destCol)));
    }

    insertSql = MessageFormat.format(insertSql, writeMode.gethiveSql(), destDbName, destTable, partFieldSql, String.join(",", fieldList), srcDbName, srcTable);
    logger.info("Finish create insert sql: {}", insertSql);
    return insertSql;
  }


  @Override
  public BaseParam getParam() {
    return impExpParam;
  }
}
