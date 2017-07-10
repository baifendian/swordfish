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

import com.baifendian.swordfish.common.enums.WriteHdfsType;

/**
 * 导入导出任务常量
 */
public class ImpExpJobConst {

  /**
   * 写入 hdfs 临时文件的默认分隔符
   */
  public static final String DEFAULT_DELIMITER = ",";

  /**
   * 写入 hdfs 临时文件的默认文件类型
   */
  public static final WriteHdfsType DEFAULT_FILE_TYPE = WriteHdfsType.TEXT;

  /**
   * hive 的默认数据库
   */
  public static final String DEFAULT_DB = "default";

  /**
   * 默认创建的 dataX 配置文件文件名
   */
  public static final String DATAX_FILE_NAME = "dataXJson";

  /**
   * dataX 配置文件结构
   */
  public static final String DATAX_JSON = "'{'\"job\":'{'\"content\":['{'\"reader\":'{'\"name\":\"{0}\",\"parameter\":{1}},\"writer\":'{'\"name\":\"{2}\",\"parameter\":{3}}}],\"setting\":{4}}}";

  /**
   * dataX 命令
   */
  public static final String COMMAND = "python {0} {1}";

  /**
   * HIVE 驱动
   */
  public static final String HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";
}
