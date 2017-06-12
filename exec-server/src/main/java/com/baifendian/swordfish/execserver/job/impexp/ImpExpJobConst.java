package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.enums.WriteHdfsType;

/**
 * 导入导出任务常量
 */
public class ImpExpJobConst {

  /**
   * 写入hdfs临时文件的默认分隔符
   */
  public static final String DEFAULT_DELIMITER = ",";

  /**
   * 写入hdfs临时文件的默认文件类型
   */
  public static final String DEFAULT_FILE_TYPE = WriteHdfsType.TEXT.getType();

  /**
   * 默认创建的 dataX 配置文件文件名
   */
  public static final String DATAX_FILE_NAME = "dataXJson";

  /**
   * dataX 配置文件结构
   */
  public static final String DATAX_JSON = "{\"job\":{\"content\":[{\"reader\":{0},\"writer\":{1}}],\"setting\":{2}}}";

  /**
   * dataX 命令
   */
  public static final String COMMAND = "python {0} {1}";

  /**
   * HIVE 驱动
   */
  public static final String HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";

}
