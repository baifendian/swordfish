package com.baifendian.swordfish.execserver.job.impexp;

import com.baifendian.swordfish.common.job.struct.node.BaseParamFactory;
import com.baifendian.swordfish.common.job.struct.node.impexp.ImpExpParam;
import com.baifendian.swordfish.execserver.job.Job;
import com.baifendian.swordfish.execserver.job.JobProps;
import com.baifendian.swordfish.execserver.job.hql.EtlSqlJob;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import static com.baifendian.swordfish.common.job.struct.node.JobType.*;

/**
 * 导入导处理job 生成器
 */
public class ImpExpJobManager {
  public static Job newJob(String jobTypeStr, JobProps props, Logger logger) throws IllegalArgumentException {
    if (!StringUtils.equals(jobTypeStr, IMPEXP)) {
      logger.error("unsupport job type: {}", jobTypeStr);
      throw new IllegalArgumentException("Not support job type");
    }

    ImpExpParam impExpParam = (ImpExpParam) BaseParamFactory.getBaseParam(jobTypeStr, props.getJobParams());
    switch (impExpParam.getType()) {
      case MYSQL_TO_HIVE:
        return new MysqlToHiveJob(props, false, logger, impExpParam);
      case MYSQL_TO_HDFS:
        return new MysqlToHdfsJob(props, false, logger, impExpParam);
      default:
        logger.error("unsupport ImpExp job type: {}", jobTypeStr);
        throw new IllegalArgumentException("Not support job type");
    }
  }
}
