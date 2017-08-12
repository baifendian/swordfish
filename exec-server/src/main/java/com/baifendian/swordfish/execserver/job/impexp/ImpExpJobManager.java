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

import static com.baifendian.swordfish.common.job.struct.node.JobType.IMPEXP;

import com.baifendian.swordfish.common.job.struct.node.BaseParamFactory;
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;
import com.baifendian.swordfish.execserver.job.Job;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.impexp.Args.ImpExpProps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * 导入导处理job 生成器
 */
public class ImpExpJobManager {

  public static Job newJob(String jobTypeStr, JobProps props, Logger logger)
      throws IllegalArgumentException {
    if (!StringUtils.equals(jobTypeStr, IMPEXP)) {
      logger.error("unsupport job type: {}", jobTypeStr);
      throw new IllegalArgumentException("Not support job type");
    }

    ImpExpParam impExpParam = (ImpExpParam) BaseParamFactory
        .getBaseParam(jobTypeStr, props.getJobParams());
    ImpExpProps impExpProps = new ImpExpProps(impExpParam, logger);

    switch (impExpParam.getType()) {
      case MYSQL_TO_HIVE:
        return new MysqlToHiveJob(props, false, logger, impExpProps);
      case MYSQL_TO_HDFS:
        return new MysqlToHdfsJob(props, false, logger, impExpProps);
      case HIVE_TO_MYSQL:
        return new HiveToMysqlJob(props, false, logger, impExpProps);
      case HIVE_TO_MONGODB:
        return new HiveToMongoJob(props, false, logger, impExpProps);
      case FILE_TO_HIVE:
        return new FileToHiveJob(props, false, logger, impExpProps);
      case POSTGRES_TO_HIVE:
        return new PostgreToHiveJob(props, false, logger, impExpProps);
      case HIVE_TO_POSTGRES:
        return new HiveToPostgreJob(props, false, logger, impExpProps);
      default:
        logger.error("unsupport ImpExp job type: {}", jobTypeStr);
        throw new IllegalArgumentException("Not support job type");
    }
  }
}
