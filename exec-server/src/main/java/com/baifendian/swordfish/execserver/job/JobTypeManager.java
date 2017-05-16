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
package com.baifendian.swordfish.execserver.job;

import com.baifendian.swordfish.execserver.job.hql.EtlSqlJob;
import com.baifendian.swordfish.execserver.job.mr.MrJob;
import com.baifendian.swordfish.execserver.job.shell.ShellJob;
import com.baifendian.swordfish.execserver.job.spark.SparkJob;
import com.baifendian.swordfish.execserver.job.upload.UploadJob;
import com.baifendian.swordfish.execserver.job.virtual.VirtualJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.baifendian.swordfish.common.job.struct.node.JobType.*;

public class JobTypeManager {

  private static final Logger logger = LoggerFactory.getLogger(JobTypeManager.class);

  public static Job newJob(String jobId, String jobTypeStr, JobProps props, Logger logger) throws IllegalArgumentException {
    switch (jobTypeStr) {
      case HQL:
        return new EtlSqlJob(jobId, props, logger);
      case MR:
        return new MrJob(jobId, props, logger);
      case SHELL:
        return new ShellJob(jobId, props, logger);
      case SPARK:
        return new SparkJob(jobId, props, logger);
      case VIRTUAL:
        return new VirtualJob(jobId, props, logger);
      case SPARK_STREAMING:
        return new SparkJob(jobId, props, logger);
      case UPLOAD:
        return new UploadJob(jobId, props, logger);
      default:
        logger.error("unsupport job type: {}", jobTypeStr);
        throw new IllegalArgumentException("Not support job type");
    }
  }

  /**
   * @param jobTypeStr
   * @return
   */
  public static boolean isLongJob(String jobTypeStr) {
    switch (jobTypeStr) {
      case SPARK_STREAMING:
        return true;
      case MR:
      case SHELL:
      case HQL:
      case SPARK:
      case VIRTUAL:
      case UPLOAD:
        return false;
      default:
        logger.error("unsupport job type: {}", jobTypeStr);
        throw new IllegalArgumentException("Not support job type");
    }
  }
}
